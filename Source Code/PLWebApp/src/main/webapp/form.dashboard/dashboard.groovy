import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

load = helper.fetch_keep('load')

uid = helper.sess('uid')

if (!uid) {
	helper.redirect('../form.login/login.groovy')
	return
}

//sql = new Sql(helper.connection)
//
////row = sql.firstRow(query1)
//
//sql.close()
if (load == null) {
	load = 'panel.resource/show_recommend'
}
content_url = "../${load}.groovy"
//content_url = '../panel.lesson/show_lesson.groovy'

helper.attr('helper', helper)
helper.attr('content_url', content_url)

helper.include('dashboard.gsp')