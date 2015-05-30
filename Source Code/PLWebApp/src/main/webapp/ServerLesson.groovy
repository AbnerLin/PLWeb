import org.plweb.suite.common.xml.XmlFactory
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def s = request.getParameter('s') //session_id
def t = request.getParameter('t') //ticket_no

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select WS_TICKET.*, COURSE.COURSE_NAME
from WS_TICKET
inner join COURSE
	on WS_TICKET.COURSE_ID=COURSE.COURSE_ID
where SESSION_ID=?
and TICKET_NO=?
"""

query2 = """
select TEXT_XML
from COURSE_FILE
where course_id=?
and lesson_id=?
"""

row = sql.firstRow(query1, [s, t])

user_id   = row.user_id
class_id  = row.class_id
course_id = row.course_id
lesson_id = row.lesson_id
mode      = row.lesson_mode

text_xml = sql.firstRow(query2, [course_id, lesson_id]).text_xml

// 轉換XML物件
ByteArrayInputStream is = new ByteArrayInputStream(text_xml.getBytes("UTF-8"))
project = XmlFactory.readProject(is);

project.setId(lesson_id.toString())

switch (mode) {
case 'answer':
	break;
case 'author':
	break;
case 'student':
case 'teacher':
	fileMap = new HashMap()
	
	project.files.each {
		fileMap.put(it.path, it)
	}
	
	q1 = """
select CONTENT
from USER_UPLOAD
where USER_ID=?
and CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
and PATH=?
"""
	
	project.tasks.each {
		task->
		
		file_main = task.getPropertyEx('file.main')
		file_part = task.getPropertyEx('file.part')
		
		fileMain = fileMap.get(file_main)
		filePart = fileMap.get(file_part)

		if (file_main && fileMain) {

			row = sql.firstRow(q1, [user_id, class_id, course_id, lesson_id, file_main])

			if (row != null) {
				//如果檔案已經上傳, 則讀取資料庫
				fileMain.setEncodedContent(row.content)
			}
			else if (filePart) {
				fileMain.setEncodedContent(filePart.encodedContent)
			}
			else {
				fileMain.setEncodedContent('')
			}
		}
		
		task.properties.keySet().each {
			key ->
			
			if (key.startsWith('file.attach')) {
				file_main = task.getPropertyEx(key)
				file_part = task.getPropertyEx('file.replace'+key.replace('file.attach', ''))
				
				fileMain = fileMap.get(file_main)
				filePart = fileMap.get(file_part)
				
				if (file_main && fileMain) {
	
					row = sql.firstRow(q1, [user_id, class_id, course_id, lesson_id, file_main])
	
					if (row != null) {
						//如果檔案已經上傳, 則讀取資料庫
						fileMain.setEncodedContent(row.content)
					}
					else if (file_part && filePart) {
						fileMain.setEncodedContent(filePart.encodedContent)
					}
				}
			}
		}
	}
	
	break;
}

sql.close()

response.setContentType('text/xml');
// 輸出XML
StringWriter writer = new StringWriter()
XmlFactory.saveProject(project, writer)
print writer.toString()