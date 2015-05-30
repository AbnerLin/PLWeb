import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid		= session.get('uid')
def utype	= session.get('utype')

if (!uid) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

varExamOnly = new File(request.getRealPath('config/ExamOnly')).text.trim().equals('y')

ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
sql = new Sql(ds.connection)

query1 = """
select ST_CLASS.*
from USER_CLASS
inner join ST_CLASS
on USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID
where USER_CLASS.USER_ID=?
and ST_CLASS.ALIVE=?
and ST_CLASS.SEMESTER<5
order by ST_CLASS.CLASS_ID
"""

//exam query
query2 = """
select ST_CLASS.*
from USER_CLASS
inner join ST_CLASS
on USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID
where USER_CLASS.USER_ID=?
and ST_CLASS.ALIVE=?
and ST_CLASS.SEMESTER=5
order by ST_CLASS.CLASS_ID
"""

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Class Menu')
		link(rel: 'stylesheet', type: 'text/css', href: 'default.css')
	}
	body {
		div(class: 'left-menu') {
			if (!varExamOnly) {
				h3('Class')
				ul {
					sql.eachRow(query1, [uid, 'Y']) {
						row ->
						href_class = "class_inside.groovy?id=${row.class_id}"
						
						li {
							a(href: href_class, "${row.class_name}")
							span("${row.class_id}")
							br()
							span("${row.school} / ${row.department}(${row.years}, ${row.semester})")
						}
					}
				}
			}
			
			h3('Exam')
			ul {
				sql.eachRow(query2, [uid, 'Y']) {
					row ->
					href_class = "class_inside.groovy?id=${row.class_id}"
					
					li {
						a(href: href_class, "${row.class_name}")
						span("${row.class_id}")
						br()
						span("${row.school} / ${row.department}(${row.years}, ${row.semester})")
					}
				}
			}
		}
		br()
		a(href:"javascript:location.reload()") {
			img (src:'icons/arrow_refresh.png', border:0)
			span ('Reload')
		}
	}
}

sql.close()
