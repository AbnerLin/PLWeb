import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid   = session.get('uid')
uname = session.get('uname')

user_id   = request.getParameter('user_id')
class_id  = request.getParameter('class_id')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')
task_id   = request.getParameter('task_id')
review    = request.getParameter('review')
time      = request.getParameter('time')

query1 = """
update ST_CODE
set REVIEW=?
where USER_ID=?
and CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
and QUESTION_ID=?
"""

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

sql.executeUpdate(query1, [review, user_id, class_id, course_id, lesson_id, task_id])

sql.close()

response.sendRedirect("lamp.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}&time=${time}")
