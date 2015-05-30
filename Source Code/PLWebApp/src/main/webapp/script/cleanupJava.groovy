import org.plweb.suite.common.xml.XmlFactory
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

/*
content = new String(request.getParameter('content').getBytes('ISO8859-1'), 'UTF-8')

ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes('UTF-8'))
project = XmlFactory.readProject(is);
//project.setId(lesson_id.toString())

StringWriter writer = new StringWriter()
XmlFactory.saveProject(project, writer)

title     = project.title
text_xml  = writer.toString()
text_size = text_xml.getBytes('UTF-8').length
tasknum   = project.tasks.size()
updated   = new Date().time
*/

response.contentType = 'text/plain'

println "cleanup java"

// 讀取 Java Template

def csid = sql.firstRow('select * from COURSE where COURSE_NAME=? limit 1', ['TQC+Java6']).COURSE_ID

println "process (${csid})"

sql.eachRow('select * from COURSE_FILE where COURSE_ID=?', [csid]) {
	row->
	if (row.TEXT_XML) {
		def content = row.TEXT_XML
		def stream = new ByteArrayInputStream(content.getBytes('UTF-8'))
		def project = XmlFactory.readProject(stream)
		println project.title
		def remove_list = []
		project.files.each {
			file->
			if (file.path.endsWith('.class') || file.path.endsWith('.dump')) {
				println "found ${file.path}"
				remove_list << file
			}
		}
        //移除 .class
		remove_list.each {
			project.files.remove(it)
		}
        
        //更新指令集
        //....

		def writer = new StringWriter()
		XmlFactory.saveProject(project, writer)

		def text_xml  = writer.toString()
		//println text_xml

        title     = project.title
		text_size = text_xml.getBytes('UTF-8').length
		tasknum   = project.tasks.size()
		updated   = new Date().time

		q1 = """
update COURSE_FILE
set TITLE=?,
	TEXT_XML=?,
	TEXT_SIZE=?,
	TASKNUM=?,
    UPDATED=?
where COURSE_ID=?
and LESSON_ID=?
"""
		try {
            println "Update ${title} ${text_size} ${tasknum} ${csid} ${row.LESSON_ID}"
			sql.executeUpdate(q1, [title, text_xml, text_size, tasknum, updated, csid, row.LESSON_ID])
		}
		catch (e) {
			println e.message
		}
	}
}

println "-- end --"

sql.close()
