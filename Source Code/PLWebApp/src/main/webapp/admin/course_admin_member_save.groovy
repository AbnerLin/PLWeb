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
def user_id = request.getParameter('user_id')
def is_owner = request.getParameter('is_owner')=='on'?'y':'n'

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

sql.execute('insert into USER_COURSE(COURSE_ID,USER_ID,IS_OWNER) values (?,?,?)', [course_id, user_id, is_owner])

sql.close()

response.sendRedirect("course_admin_member.groovy?course_id=${course_id}")
