import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

html.setDoubleQuotes(true)
html.html {
	head {
		title("PLWeb - Teaching Materials")
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
		script(type:'text/javascript', src:'permission_denied.js', '')
	}
	body {
		p("Error: You can't access this page because permission denied!!!")
		
		p {
			a(href:'index.html', target:'_top', 'Re-login')
		}
	}
}
