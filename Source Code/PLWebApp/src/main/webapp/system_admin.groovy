import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid		= session.get('uid')
def uname	= session.get('uname')
def utype	= session.get('utype')

if (utype != 'M') {
	response.sendRedirect('permission_denied.groovy')
	return;
}

html.setDoubleQuotes(true)
html.html {
	head {
		meta ('http-equiv': "Content-Type", content: "text/html; charset=UTF-8")
		script (type: "text/javascript", '')
		link (href: "default.css", rel: "stylesheet", type: "text/css")
	}
	body {
		ul {
			li {
				a (href: 'user_admin.groovy', 'Users Administration')
			}
			
			li {
				a (href: 'teacher_admin.groovy', 'Teachers Administration')
			}
			
			li {
				a (href: 'course_admin.groovy', 'Teaching Materials Administration')
			}

			li {
				a (href: 'class_admin.groovy', 'Classes Administration')
			}
			
			li {
				a (href: 'sqlmon.groovy', 'Database Monitor')
			}
			
			li {
				a (href: 'announce.groovy', 'Announcement')
			}
		}
	}
}
