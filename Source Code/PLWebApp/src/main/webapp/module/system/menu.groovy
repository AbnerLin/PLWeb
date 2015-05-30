import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)

//read sessions
uid = helper.sess('uid')

sql = new Sql(helper.connection)

helper.attr 'helper', helper

if (!uid) {
	helper.include 'menu.gsp'
}
else {
	year = Calendar.getInstance().get(Calendar.YEAR)
	
	query1 = """
		select b.CLASS_ID, b.CLASS_NAME, b.SCHOOL, b.DEPARTMENT
		from USER_CLASS a
		inner join ST_CLASS b
		on a.CLASS_ID=b.CLASS_ID
		where a.USER_ID=?
		and b.YEARS='${year}'
		and b.ALIVE='Y'
		and b.SEMESTER<5
		order by b.CLASS_ID desc
	"""
	
	classes = []
	
	sql.eachRow(query1, [uid]) {
		row ->
		
		classes << [
			id: row.CLASS_ID,
			name: row.CLASS_NAME,
			school: row.SCHOOL,
			dept: row.DEPARTMENT,
			href: helper.make_url("${helper.servletPath}", ['panel_class_id':"${row.CLASS_ID}", 'panel_lesson_id':'', 'load':'panel.class/show_class']),
		]
	}
	helper.attr 'my_classes', classes
	helper.attr 'show_admin_menu', 'y'.equalsIgnoreCase(helper.sess('is_admin'))
	helper.include 'menu.login.gsp'
}

sql.close()