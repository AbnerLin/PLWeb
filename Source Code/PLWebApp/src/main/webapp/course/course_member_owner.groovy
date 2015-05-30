import groovy.sql.Sql
import javax.naming.InitialContext

action    = request.getParameter('action')
course_id = request.getParameter('course_id')
user_id   = request.getParameter('user_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.getConnection())

try {
	switch (action) {
	case 'set':
		sql.execute("update USER_COURSE set IS_OWNER=? where COURSE_ID=? and USER_ID=?", ['y', course_id, user_id])
		break;
	case 'unset':
		sql.execute("update USER_COURSE set IS_OWNER=? where COURSE_ID=? and USER_ID=?", ['n', course_id, user_id])
		break;
	}
}
catch (e) {
}

response.sendRedirect("lesson_list.groovy?course_id=$course_id")

sql.close()