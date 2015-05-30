import groovy.sql.Sql
import org.plweb.suite.common.xml.XmlFactory

def db = [
	url:'jdbc:mysql://plweb.org/plweb',
	user:'sta', password:'stastasta',
	driver:'com.mysql.jdbc.Driver'
]

def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)

sql.eachRow('select * from ST_USER limit 10') {
    println it
}


/*		content = new String(request.getParameter('content').getBytes('ISO8859-1'), 'UTF-8')
		
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

sql.close()
