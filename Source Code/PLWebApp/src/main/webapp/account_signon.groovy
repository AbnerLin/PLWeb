import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid = session.get('uid')

def class_id = request.getParameter('class_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
values (?, ?, ?)
"""

sql.execute(query1, [uid, class_id, 'n'])

sql.close()

response.sendRedirect('account.groovy')