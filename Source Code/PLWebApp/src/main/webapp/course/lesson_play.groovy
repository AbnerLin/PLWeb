import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XmlFactory

def course_id = request.getParameter('course_id')
def lesson_id = request.getParameter('lesson_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select COURSE_TITLE
from COURSE
where course_id=?
"""

query2 = """
select TITLE
from COURSE_FILE
where course_id=?
and lesson_id=?
"""




// 從資料庫讀取XML
course_title = sql.firstRow(query1, [course_id]).course_title
lesson_title = sql.firstRow(query2, [course_id, lesson_id]).title

href_menu = "lesson_menu.groovy?course_id=${course_id}&lesson_id=${lesson_id}";
href_load = "lesson_loading.groovy"

html.setDoubleQuotes(true)
html.html {
	head {
		title ("Lesson Player: ${course_title} - 「${lesson_title}」")
	}
	frameset (rows: '*', cols: '30%,*') {
		frame (name: 'lessonMenu', src: href_menu)
		frame (name: 'lessonLoad', src: href_load)
	}
}

sql.close()
