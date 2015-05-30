import org.plweb.suite.common.xml.XmlFactory
import groovy.sql.Sql
import javax.naming.InitialContext

course_id    = request.getParameter('course_id')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.getConnection())

try {
	
	sql.eachRow('select * from COURSE_FILE where COURSE_ID=?', [course_id]) {
		row->
		ByteArrayInputStream is = new ByteArrayInputStream(row.text_xml.getBytes("UTF-8"))
		project = XmlFactory.readProject(is);

		lesson_id = row.lesson_id
		tasknum = project.tasks.size()
		sql.execute('update COURSE_FILE set TASKNUM=? where COURSE_ID=? and LESSON_ID=?', [tasknum, course_id, lesson_id])
	}
	
}
catch (e) {
}

sql.close()

response.sendRedirect("lesson_list.groovy?course_id=${course_id}")