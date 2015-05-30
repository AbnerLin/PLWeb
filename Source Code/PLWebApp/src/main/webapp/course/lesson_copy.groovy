import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XmlFactory

def course_id = request.getParameter('course_id')
def lesson_id = request.getParameter('lesson_id')

def ds	= new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql	= new Sql(ds.connection)

query1 = """
select *
from COURSE_FILE
where COURSE_ID=?
and LESSON_ID=? 
"""

query2 = """
select max(LESSON_ID)+1 as LESSON_ID, max(SEQNUM)+1 as SEQNUM
from COURSE_FILE
where COURSE_ID=?
"""

query3 = """
insert into COURSE_FILE (COURSE_ID, LESSON_ID, SEQNUM, VISIBLED, CREATED, UPDATED, TITLE, TEXT_SIZE, TEXT_XML)
values (?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

row = sql.firstRow(query1, [course_id, lesson_id])

title = row.title
text_size = row.text_size
text_xml = row.text_xml

row = sql.firstRow(query2, [course_id])
lesson_id	= row.lesson_id==null?1:row.lesson_id
seqnum		= row.seqnum==null?0:row.seqnum

created = new Date().time
updated = new Date().time

sql.executeInsert(query3, [course_id, lesson_id, seqnum, 'y', created, updated, title, text_size, text_xml])

sql.close()

response.sendRedirect("lesson_list.groovy?course_id=${course_id}")