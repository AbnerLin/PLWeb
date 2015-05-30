import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid   = session.get('uid')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
update ST_USER
set IS_LOGIN='n'
where USER_ID=?
"""

sql.executeUpdate(query1, [uid])

sql.close()

request.session.setAttribute('uid', null)
request.session.setAttribute('uname', null)
request.session.setAttribute('utype', null)

if (helper.sess('login_url') != null) {
	helper.redirect(helper.sess('login_url'))
}
else {
	response.sendRedirect('index.html')
}
