import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def id = request.getParameter('id')

try {
	def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')

	/*
	conn = ds.connection
	stmt = conn.createStatement()
	
	stmt.execute("drop table ES_MESSAGE_${id}")
	stmt.execute("drop table ST_CODE_${id}")
	stmt.execute("drop table ST_MESSAGE_${id}")
	stmt.execute("drop sequence CODE_${id}")
	stmt.execute("drop sequence ES_${id}")
	stmt.execute("drop sequence MSG_${id}")
	
	stmt.close()
	conn.close()
	*/
	
	def sql = new Sql(ds.connection)
	sql.execute('delete from user_class where class_id=?', [id])
	sql.execute('delete from ST_CLASS where class_id=?', [id])
	sql.close()

	//刪除樹狀選單XML檔案
	def tree_dir = request.getRealPath('/tree') 
	def obj_file = new File(tree_dir, "${id}.xml")
	if (obj_file.exists()) obj_file.delete()
	
	session.setAttribute('alert_message', 'Class removed!');
}
catch (e) {
	session.setAttribute('error_message', e.message);
}

response.sendRedirect('class_admin.groovy')
