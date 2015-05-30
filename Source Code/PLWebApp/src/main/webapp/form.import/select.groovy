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

//sql = new Sql(helper.connection)
//
////row = sql.firstRow(query1)
//
//sql.close()


helper.attr 'helper', helper
helper.attr 'course_id', course_id
helper.attr 'lesson_id', lesson_id

helper.forward 'select.gsp'
