import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

keyword = ''
if (request.getParameter('keyword')) {
	keyword = helper.fetch('keyword')
}

query1 = """
select count(*) as COUNT_USER
from ST_USER
where EMAIL like ?
or NAME like ?
or ENROLLMENT like ?
"""

query2 = """
select *
from ST_USER
where EMAIL like ?
or NAME like ?
or ENROLLMENT like ?
order by USER_ID
"""

keywords = ['%'+keyword+'%', '%'+keyword+'%', '%'+keyword+'%']

count_user = sql.firstRow(query1, keywords).count_user

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Users Administration')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
		script(type: 'text/javascript', src: 'class_admin.js', '')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		h2("Users Administration")
		form (action: 'user_admin.groovy', method: 'post') {
			span ('Keyword: ')
			input (name: 'keyword', value: keyword)
			input (type: 'submit', value: 'Query')
		}
		hr ()
		h3("List of Users")
		if (count_user <= 500) {
			table(width:"100%") {
				tr {
					th (width: 40, "#")
					th (width: 80, "User ID")
					th (width: 100, "E-Mail")
					th ("Name")
					th ("Enrollment ID")
					th ("Telephone")
					th ("Type")
					th (width:60, "Drop")
				}
				c = 0
				sql.eachRow(query2, keywords) {
					row ->
					tr (class: c%2==0?'odd':'even') {
						td (align:'center', ++c)
						td (row.user_id)
						td (row.email)
						td (row.name)
						td (row.enrollment)
						td (row.telephone)
						td {
							switch (row.type) {
							case 'S':
								span('Student')
								break;
							case 'T':
								span('Teacher')
								break;
							case 'M':
								span('Administrator')
								break;
							}
						}
						td (align: 'center') {
							href_remove = "user_admin_remove.groovy?user_id=${row.user_id}" 
							a (href: href_remove, onclick: "return confirm('Are you sure???');") {
								img (src: '../icons/user_delete.png', border: 0)
							}
						}
					}
				}
			}
		}
		else {
			p ("Query result contains too many data, ${count_user} > 500. Enter keywords and try again.")
		}
	}
}

sql.close()
