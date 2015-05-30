import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

field = helper.fetch('field')
value = helper.fetch('value')

switch (field) {
	/**
	 * Check E-Mail
	 */
	case 'email':
	
	sql = new Sql(helper.connection)
	query1 = 'select NAME from ST_USER where EMAIL=?'
	
	row = sql.firstRow(query1, [value])
	
	if (row) {
		print """符合 <font color="blue">${row.NAME}</font>"""
	}
	else {
		print """<font color="red">not exists</font>"""
	}
	
	sql.close()

	return;

	/**
	 * Check Password
	 */
	case 'password':
	return;
}