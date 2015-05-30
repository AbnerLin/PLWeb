import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	helper.redirect('../form.login/login.groovy')
	return
}

sql = new Sql(helper.connection)

//row = sql.firstRow(query1)

sql.close()

helper.attr('helper', helper)

helper.forward('backward.gsp')