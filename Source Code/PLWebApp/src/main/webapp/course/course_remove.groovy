import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

if (!session) {
	response.sendError 403
	return
}

def course_id = helper.fetch('id')

def sql = new Sql(helper.connection)

if (course_id!=null && course_id!='') {

	try {	
		sql.execute('update COURSE set VISIBLED=? where COURSE_ID=?', ['n', course_id])

		helper.sess 'alert_message', '教材已經移除'
	}
	catch (e) {
		helper.sess 'alert_message', e.message
	}

}

helper.redirect 'index.groovy'
