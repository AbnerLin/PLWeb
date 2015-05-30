/**
 * @require js/jquery.poshytip.min.js
 */

import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

uid		= helper.sess('uid')
utype	= helper.sess('utype')

class_id = helper.fetch_keep('panel_class_id')

panel_lesson_id	= helper.fetch_keep('panel_lesson_id')

class_id2	= null
course_id	= null
lesson_id	= null

if (panel_lesson_id) {
	tokens = panel_lesson_id.split(',')
	
	//class_id2	= tokens[0]
	//course_id	= tokens[1]
	//lesson_id	= tokens[2]
}

sql = new Sql(helper.connection)

query1 = """
	select ST_CLASS.*
	from USER_CLASS
	inner join ST_CLASS
	on USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID
	where USER_CLASS.USER_ID=?
	and ST_CLASS.ALIVE='Y'
	and ST_CLASS.SEMESTER<5
	order by ST_CLASS.CLASS_ID desc
"""

query2 = """
	select CLASS_COURSE.*, COURSE.COURSE_TITLE
	from CLASS_COURSE
	inner join COURSE
		on CLASS_COURSE.COURSE_ID=COURSE.COURSE_ID
	where CLASS_COURSE.CLASS_ID=?
	order by CLASS_COURSE.SEQNUM
"""

classes = []

sql.eachRow(query1, [uid]) {
	row ->
	
	lessons = []

	if (class_id && (class_id == row.CLASS_ID.toString())) {
		sql.eachRow(query2, [class_id]) {
			row2->
			
			lessons << [
				class_id:	row2.CLASS_ID,
				course_id:	row2.COURSE_ID,
				lesson_id:	row2.LESSON_ID,
				title:		row2.TITLE,
				course:		row2.COURSE_TITLE,
				href:		helper.make_url("${helper.servletPath}", ['panel_lesson_id':"${row2.CLASS_ID},${row2.COURSE_ID},${row2.LESSON_ID}", 'load':'panel.lesson/show_lesson'], true),
				selected:	(row2.CLASS_ID.toString()==class_id2)&&(row2.COURSE_ID.toString()==course_id)&&(row2.LESSON_ID.toString()==lesson_id)
			]
		}
	}
	
	classes << [
		id: row.CLASS_ID,
		name: row.CLASS_NAME,
		school: row.SCHOOL,
		dept: row.DEPARTMENT,
		href: helper.make_url("${helper.servletPath}", ['panel_class_id':"${row.CLASS_ID}", 'panel_lesson_id':'', 'load':'panel.class/show_class']),
		lessons: lessons,
		selected: row.CLASS_ID.toString()==class_id
	]
}



sql.close()

helper.attr('helper', helper)
helper.attr('classes', classes)

helper.include('list_normal.gsp')