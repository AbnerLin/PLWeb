import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XmlFactory

course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

def ds	= new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql	= new Sql(ds.connection)

query1 = """
select *
from COURSE_FILE
where COURSE_ID=?
and LESSON_ID=? 
"""

row = sql.firstRow(query1, [course_id, lesson_id])

response.setContentType('text/xml; charset=UTF-8')
response.setHeader("Content-disposition", "attachment; filename=lesson${lesson_id}.xml")
print row.text_xml

sql.close()