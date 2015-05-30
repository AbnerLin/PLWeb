import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XmlFactory
import org.apache.commons.fileupload.DiskFileUpload
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileUpload
import org.apache.commons.fileupload.FileUploadException

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select max(LESSON_ID)+1 as LESSON_ID, max(SEQNUM)+1 as SEQNUM
from COURSE_FILE
where COURSE_ID=?
"""

query2 = """
insert into COURSE_FILE
	(COURSE_ID, LESSON_ID, SEQNUM, VISIBLED, CREATED, UPDATED, TITLE, TEXT_SIZE, TEXT_XML)
values (?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

try {
	if(FileUpload.isMultipartContent(request)) {
		parser = new DiskFileUpload()
		items = parser.parseRequest(request)
		
		course_id = null
		
		for (i=0; i < items.size(); i++) {
			item = items.get(i)
			if (item.isFormField() && item.fieldName.equals('course_id')) {
				course_id = item.getString()
			}
		}
		if (course_id != null) {
			for (i=0; i < items.size(); i++) {
				item = items.get(i)
				itemName = item.getFieldName()
				if (!item.isFormField()) {
					text_xml  = item.getString('UTF-8')
					text_size = text_xml.getBytes('UTF-8').length
					
					is = new ByteArrayInputStream(text_xml.getBytes('UTF-8'))
					
					project = XmlFactory.readProject(is)
					
					title    = project.title
					visibled = 'y'
					created  = new Date().time
					updated  = new Date().time
					
					row = sql.firstRow(query1, [course_id])
					
					lesson_id = row.lesson_id==null?1:row.lesson_id
					seqnum    = row.seqnum==null?0:row.seqnum
					
					sql.executeInsert(query2, [course_id, lesson_id, seqnum, visibled, created, updated, title, text_size, text_xml])
				}
			}
		}
	}
}
catch (e) {
	session.setAttribute('error_message', e.message)
}

sql.close()

response.sendRedirect("lesson_list.groovy?course_id=${course_id}")