import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def course_id	= request.getParameter('course_id')
def lesson_id	= request.getParameter('lesson_id')

def ds	= new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql	= new Sql(ds.connection)

query1 = """
update COURSE_FILE
set VISIBLED=?
where COURSE_ID=?
and LESSON_ID=?
"""

sql.executeUpdate(query1, ['n', course_id, lesson_id])

sql.close()

response.sendRedirect("lesson_list.groovy?course_id=${course_id}")
