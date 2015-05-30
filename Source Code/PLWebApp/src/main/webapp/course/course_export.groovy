import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XmlFactory
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

course_id = request.getParameter('course_id')

def ds	= new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql	= new Sql(ds.connection)

query1 = """
select *
from COURSE_FILE
where COURSE_ID=?
and VISIBLED='y'
order by SEQNUM
"""

row = sql.firstRow("select COURSE_NAME, COURSE_TITLE from COURSE where COURSE_ID=?", [course_id])
course_name  = row.course_name
course_title = row.course_title

response.setContentType("application/zip")
response.setHeader("Content-disposition", "attachment; filename=${course_name}.zip")

zout  = new ZipOutputStream(response.outputStream)

c = 0

sql.eachRow(query1, [course_id]) {
	row ->
	
	zout.putNextEntry(new ZipEntry("${course_id}/lesson${++c}.xml"))
	
	zout.write(row.text_xml.bytes)
}

zout.close()

sql.close()
