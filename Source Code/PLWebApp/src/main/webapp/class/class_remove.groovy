import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def id = request.getParameter('id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

sql.execute('update ST_CLASS set ALIVE=? where CLASS_ID=?', ['n', id])

sql.close()

session.setAttribute('alert_message', "課程 ${id} 已經移除！");

response.sendRedirect('index.groovy')
