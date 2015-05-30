import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid		= session.get('uid')
def uname	= session.get('uname')
def utype	= session.get('utype')

if (utype != 'M') {
	response.sendRedirect('permission_denied.groovy')
	return;
}

user_id = request.getParameter('user_id')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

sql.execute("update ST_USER set TYPE='S' where USER_ID=?", [user_id])

sql.close()

response.sendRedirect("teacher_admin.groovy")
