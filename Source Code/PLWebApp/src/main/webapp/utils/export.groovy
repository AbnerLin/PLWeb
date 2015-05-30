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
	
	c++
	
	StringWriter sw = new StringWriter()
	PrintWriter pw = new PrintWriter(sw)
	
	is = new ByteArrayInputStream(row.TEXT_XML.getBytes('UTF-8'))
	project = XmlFactory.readProject(is)
	
	//pw.print("<h1>${project.title}</h1>")
	pw.println("${project.title}")
	pw.println()
	
	n = 0
	
	project.tasks.each {
		task ->
		
		n++
		
		pw.println("第${n}題")
		pw.println()
		
		file_html = project.getFile(project.getTaskPropertyEx(task, 'file.html'))
		file_main = project.getFile(project.getTaskPropertyEx(task, 'file.main'))
		file_dump = project.getFile(project.getTaskPropertyEx(task, 'file.view.0'))
		
		html_text = new String(file_html.decodedContent)
		if (html_text.length() > 0) {
			s = html_text.indexOf('<body>')
			e = html_text.indexOf('</body>')
			if (s >= 0 && e >= 0)
			html_text = html_text.substring(s+6, e)
		}
		
		//pw.print('<h3>題目描述</h3>')
		//pw.print(html_text)
		pw.println(new String(file_html.decodedContent))
		//pw.print('<h3>程式碼</h3>')
		//pw.print('<pre><code>')
		pw.println(new String(file_main.decodedContent))
		//pw.print('</pre></code>')
		//pw.print('<h3>輸出</h3>')
		//pw.print('<pre><code>')
		//pw.print(new String(file_dump.decodedContent, 'MS950'))
		//pw.print('</pre></code>')
		
		pw.println()
		pw.println('------------------------------------')
		pw.println()
	}
	
	pw.flush()
	pw.close()
	sw.flush()
	result = sw.toString()
	sw.close()
	
	zout.putNextEntry(new ZipEntry("${course_id}/lesson${c}.txt"))	
	zout.write(result.bytes)
}

zout.close()

sql.close()
