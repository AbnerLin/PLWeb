import groovy.sql.Sql
import javax.naming.InitialContext

course_id    = request.getParameter("course_id")
user_id    = request.getParameter("user_id")

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.getConnection())

try {
	sql.execute("delete from USER_COURSE where COURSE_ID=? and USER_ID=?", [course_id, user_id])
}
catch (e) {
}

response.sendRedirect("lesson_list.groovy?course_id=$course_id")

sql.close()