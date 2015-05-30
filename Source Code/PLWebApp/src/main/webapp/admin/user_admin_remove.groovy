import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid		= session.get('uid')
uname	= session.get('uname')
utype	= session.get('utype')

if (utype != 'M') {
	response.sendRedirect('permission_denied.groovy')
	return;
}

user_id = request.getParameter('user_id')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

sql.execute("delete from ST_USER where USER_ID=?", [user_id])

sql.close()

response.sendRedirect("user_admin.groovy")
