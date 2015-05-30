import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

uid		= helper.sess('uid')
utype	= helper.sess('utype')

class_id = helper.fetch_keep('panel_class_id')

sql = new Sql(helper.connection)

is_student = sql.firstRow("""
	select COUNT(1) as CC
	from USER_CLASS
	where USER_ID=?
	and CLASS_ID=?
""", [uid, class_id]).CC > 0

is_teacher = sql.firstRow("""
	select IS_TEACHER
	from USER_CLASS
	where USER_ID=?
	and CLASS_ID=?
""", [uid, class_id]).IS_TEACHER.equalsIgnoreCase('y')

row = sql.firstRow("""
	select CLASS_NAME, HTML_TEXT
	from ST_CLASS
	where CLASS_ID=?
""", [class_id])


html_text	= row.HTML_TEXT
class_name	= row.CLASS_NAME

if (!html_text) {
	html_text = """
<h1>Empty Title</h1>
Objectives
<ol>
<li>first</li>
<li>second</li>
<li>third</li>
</ol>
"""
}


students = []
sql.eachRow("""
	select u.NAME, uc.IS_TEACHER
	from USER_CLASS uc
	inner join ST_USER u on u.USER_ID=uc.USER_ID
	where uc.CLASS_ID=?
""", [class_id]) {
	row->
	if ('n'.equals(row.IS_TEACHER)) {
		students << [name: row.NAME]
	}
}

// Making URL
schedule_url = helper.make_url('form.compatible/backward.groovy', [url: "schedule.groovy?id=${class_id}"], true)
status_url   = helper.make_url('panel.class/student_report.groovy', [class_id: class_id], true)
sql.close()

helper.attr 'helper',		helper
helper.attr 'is_student',	is_student
helper.attr 'is_teacher',	is_teacher
helper.attr 'schedule_url',	schedule_url
helper.attr 'status_url',	status_url
helper.attr 'html_text',	html_text
helper.attr 'students',		students
helper.attr 'class_id',		class_id
helper.attr 'class_name',	class_name
helper.attr 'class_html_form_action', "panel.class/class_html_save_ajax.groovy?class_id=${class_id}"

helper.include('show_class.gsp')
