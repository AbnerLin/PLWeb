import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid = session.get('uid')

def password  = request.getParameter('password')
def password2 = request.getParameter('password2')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

query1 = """
update ST_USER
set PASSWORD=?
where USER_ID=?
"""

try {
	if (!password) {
		throw new Exception('Empty password not allow.')
	}
	
	if (password != password2) {
		throw new Exception('Two password must equal.')
	}
	
	sql.execute(query1, [password, uid])

	session.setAttribute('alert_message', 'Password changed.')
}
catch (e) {
	session.setAttribute('error_message', e.message)
}

sql.close()

response.sendRedirect('account.groovy')