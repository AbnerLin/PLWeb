import groovy.sql.Sql
import javax.naming.InitialContext

user_id    = request.getParameter("user_id")
course_id    = request.getParameter("course_id")

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.getConnection())

try {
	
	row = sql.firstRow('select USER_ID from ST_USER where USER_ID=? or EMAIL=?', [user_id, user_id])
	
	if (row) {
	
		user_id = row.user_id
		
		sql.execute('insert into USER_COURSE (COURSE_ID, USER_ID, IS_OWNER) values (?,?,?)', [course_id, user_id, 'n'])
	}
	
	response.sendRedirect("lesson_list.groovy?course_id=$course_id")
}
catch (e) {
	print e
}

sql.close()