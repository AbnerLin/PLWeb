import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def course_id = request.getParameter('course_id')
def lesson_id = request.getParameter('lesson_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select LESSON_ID
from COURSE_FILE
where COURSE_ID=?
order by SEQNUM
"""

query2 = """
update COURSE_FILE
set SEQNUM=?
where COURSE_ID=?
and LESSON_ID=?
"""

list = new ArrayList()

m = -1
c = 0
sql.eachRow(query1, [course_id]) {
	row->
	list.add(row.LESSON_ID)
	
	if (row.LESSON_ID.toLong()==lesson_id.toLong()) {
		m = c
	}
	c++
}

if (m > -1) {
	list.add(m+1, list.get(m))
	list.remove(m+2)
}

c = 0
list.each {
	sql.executeUpdate(query2, [c++, course_id, it])
}

sql.close()

response.sendRedirect("lesson_list.groovy?course_id=${course_id}")
