import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

sqlstr = request.getParameter('sqlstr')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)
try {
if (sqlstr) sqlout = sql.rows(sqlstr)
else sqlout = ''
}
catch(e) {
sqlout = e.message
}
sql.close()

if (!sqlstr) sqlstr = ''

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - SQL Command Executor')
	}
	body {
		h2("PLWeb - SQL")

		div (sqlout)
		hr ()
		form (action: '', method: 'post') {
			textarea (name: 'sqlstr', rows:10, cols:60, sqlstr)
			br ()
			input (type:'submit', value:'exec')
		}
	}
}
