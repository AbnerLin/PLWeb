import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XProject
import org.plweb.suite.common.xml.XmlFactory

def course_id = request.getParameter('course_id')
def lesson_id = request.getParameter('lesson_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select TEXT_XML
from COURSE_FILE
where course_id=?
and lesson_id=?
"""

// 從資料庫讀取XML
text_xml = sql.firstRow(query1, [course_id, lesson_id]).text_xml
ByteArrayInputStream is = new ByteArrayInputStream(text_xml.getBytes("UTF-8"))
XProject project = XmlFactory.readProject(is);

dataPath = request.getRealPath('data')

project.setRootPath(new File(dataPath, "${course_id}/${lesson_id}"))
project.writeToDisk()

href_menu = "lesson_menu.groovy?course_id=${course_id}&lesson_id=${lesson_id}";
href_load = "lesson_loading.groovy"

href_project = project.getPropertyEx("file.html");
if (href_project == null ) {
	href_project = "lesson_null.groovy"
}
else {
	if (!href_project.startsWith("http://") && !href_project.startsWith("https://")) {
		href_project = "data/${course_id}/${lesson_id}/${href_project}"
	}
}

html.setDoubleQuotes(true)
html.html {
	head {
		script (type: 'text/javascript', src: 'lesson_menu.js', '')
	}
	body (onload: "window.top.lessonLoad.location.href='${href_project}';") {
		a (href: href_project, target: 'lessonLoad', project.title)
		project.tasks.each {
			task ->
			ul {
				li {
					href_task = project.getTaskPropertyEx(task, "file.html")
					if (!href_task.startsWith("http://") && !href_task.startsWith("https://")) {
						href_task = "data/${course_id}/${lesson_id}/${href_task}"
					}
					a (href: href_task, target: 'lessonLoad', task.title)
				}
			}
		}
	}
}

sql.close()
