import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

try {
	username = request.getParameter('username')
	password = request.getParameter('password')
	
	def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
	def sql = new Sql(ds.connection)
	
	row = sql.firstRow('select * from ST_USER where EMAIL=? and PASSWORD=?', [username, password])
	
	isLogin = false
	
	if (row) {		
		session = session?session:request.getSession(true)
		request.session.setAttribute('uid', row.USER_ID.toString())
		request.session.setAttribute('uemail', row.EMAIL)
		request.session.setAttribute('uname', row.NAME)
		request.session.setAttribute('utype', row.TYPE)
		
		isLogin = true
	}
	
	sql.close()
	
	jscode = isLogin?"window.top.location.href='main.groovy'":"alert('Login failed!!!');"
	
	html.setDoubleQuotes(true)
	html.html {
		head {
			link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
			script (type:'text/javascript', src:'login.js', '')
			script (type:'text/javascript', jscode)
			
			if (!isLogin) {
				meta('http-equiv':'refresh', content:'3; url=login.html')
			}
		}
		body {
			if (isLogin) {
				p {
					span ("You're logined. ")
					a (href:'main.groovy', target:'_top', 'Main Page')
				}
			}
			else {
				p {
					span ('Login failed!!! Waiting 3 seconds and ')
					a (href:'login.html', 're-login')
					span(' again.')
				}
			}
		}
	}
}
catch (e) {
	println e.message
}
