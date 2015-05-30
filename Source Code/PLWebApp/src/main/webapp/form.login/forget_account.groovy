import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

name       = helper.fetch('name')
enrollment = helper.fetch('enrollment')

accounts = []

if (name || enrollment) {
	sql = new Sql(helper.connection)
	
	if (name) {
		query1 = 'select NAME, EMAIL, ENROLLMENT from ST_USER where name=?'
		
		sql.eachRow(query1, [name]) {
			row->
			accounts << [EMAIL: row.EMAIL.replace('@', ' * '), NAME: row.NAME, ENROLLMENT: row.ENROLLMENT]
		}
	}
	
	if (enrollment) {
		query1 = 'select NAME, EMAIL, ENROLLMENT from ST_USER where enrollment=?'
		
		sql.eachRow(query1, [enrollment]) {
			row->
			accounts << [EMAIL: row.EMAIL, NAME: row.NAME, ENROLLMENT: row.ENROLLMENT]
		}
	}
	


	sql.close()
}

helper.attr('helper', helper)
helper.attr('accounts', accounts)

helper.forward('forget_account.gsp')