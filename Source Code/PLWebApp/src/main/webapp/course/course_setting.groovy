import groovy.sql.Sql
import javax.naming.InitialContext

action    = request.getParameter('action')
value     = request.getParameter('value')
course_id = request.getParameter('course_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.getConnection())

try {
	switch (action) {
	case 'is_share':
		sql.execute("update COURSE set IS_SHARE=? where COURSE_ID=?", [value, course_id])
		break;
	}
}
catch (e) {
}

response.sendRedirect("lesson_list.groovy?course_id=$course_id")

sql.close()