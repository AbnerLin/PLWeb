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

def course_id = request.getParameter('course_id')
def user_id = request.getParameterValues('user_id[]')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

user_id.each {
	println it
	sql.execute('delete from USER_COURSE where COURSE_ID=? and USER_ID=?', [course_id, it])
}

sql.close()

response.sendRedirect("course_admin_member.groovy?course_id=${course_id}")
