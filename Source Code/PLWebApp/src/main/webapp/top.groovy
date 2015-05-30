import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid		= session.get('uid')
uemail	= session.get('uemail')
utype	= session.get('utype')

if (!uid) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select NAME
from ST_USER
where USER_ID=?
"""

uname = sql.firstRow(query1, [uid]).name

sql.close()

html.setDoubleQuotes(true)
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=UTF-8')
		link (href: 'default.css', rel: 'stylesheet', type: 'text/css')
	}
	body (class: 'top') {
		div (class: 'top-back') {
			div (class: 'top-logo') {
				div (class: 'navbar') {
					a (href: 'manual.html', target: 'mainFrame') {
						img (src: 'icons/book.png', border: 0)
						span('Manual')
					}
					a (href: 'java_runtime.html', target: 'mainFrame') {
						img (src: 'icons/java_icon.gif', border: 0)
						span('Java Runtime')
					}
					
					if (utype == 'M') {
						a (href: 'system_admin.groovy', target: 'mainFrame') {
							img (src:'icons/server.png', border:0)
							span ('Administration')
						}
					}
					if (utype == 'M' || utype == 'T') {
						a (href: 'course_center.groovy', target: 'mainFrame') {
							img (src:'icons/book.png', border:0)
							span ('Materials')
						}
						a (href: 'class_center.groovy', target: 'mainFrame') {
							img (src:'icons/group.png', border:0)
							span ('Classes')
						}
	
					}

					a (href: 'account.groovy', target: 'mainFrame') {
						img (src:'icons/user.png', border:0)
						span ('Account')
					}
					a (href: 'logout.groovy', target: '_top', onclick: "return confirm('Are you sure?');") {
						img (src:'icons/door_out.png', border:0)
						span ('Logout')
					}
				}
				div (class: 'welcome', "${uname} (${uemail})")
			}
		}
	}
}
