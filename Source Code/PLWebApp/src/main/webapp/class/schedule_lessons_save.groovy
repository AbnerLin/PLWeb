import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import javax.naming.InitialContext

def class_id = request.getParameter('class_id')
def checks  = request.getParameterValues('checks[]')

def ds  = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
insert into CLASS_COURSE (CLASS_ID, COURSE_ID, LESSON_ID, TITLE, SEQNUM)
select '${class_id}',
	COURSE_ID,
	LESSON_ID,
	TITLE,
	(select count(*)
		from CLASS_COURSE
		where CLASS_COURSE.CLASS_ID='${class_id}')
from COURSE_FILE
where COURSE_ID=?
and LESSON_ID=?
"""


if (checks != null) {
	checks.each {
		a = it.split(';')
		course_id = a[0]
		lesson_id = a[1]
		
		sql.executeInsert(query1, [course_id, lesson_id])
	}
}

sql.close()

response.sendRedirect("schedule.groovy?id=${class_id}")
