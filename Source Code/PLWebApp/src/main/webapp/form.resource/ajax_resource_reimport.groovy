/**
 * 
 * @param	course_id	:integer
 * @param	lesson_id	:integer
 * 
 * @return	:plain
 */
import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	helper.redirect('../form.login/login.groovy')
	return
}

course_id = helper.fetch('course_id')
lesson_id = helper.fetch('lesson_id')
		
sql = new Sql(helper.connection)

html_link = sql.firstRow("""
	select HTML_LINK
	from COURSE_FILE
	where COURSE_ID=?
	and LESSON_ID=?
""", [course_id, lesson_id]).HTML_LINK

if (html_link) {
	println "Import from url: ${html_link}."

	try {
		now = new Date().time

		html_text = helper.http_fetch(html_link, true)
	
		sql.executeUpdate("""
			update COURSE_FILE
			set HTML_TEXT=?
			where COURSE_ID=?
			and LESSON_ID=?
		""", [html_text, course_id, lesson_id])

		diff = new Date().time - now
		println "Action completed in ${diff} ms."
	}
	catch (e) {
		println e.message
	}
	finally {
	}
}

sql.close()
