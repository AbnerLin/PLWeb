import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

user_id_list = request.getParameterValues('check_id_list[]')

if (user_id_list != null && user_id_list.size() > 0) {
	def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
	def sql = new Sql(ds.connection)

	//ql.execute("update ST_USER set TYPE='T' where USER_ID=?", [user_id])
	user_id_list.each {
		user_id->
		sql.execute("update ST_USER set TYPE='T' where USER_ID=?", [user_id])
	}

	sql.close()
}

response.sendRedirect('latest_signup_users.groovy')
