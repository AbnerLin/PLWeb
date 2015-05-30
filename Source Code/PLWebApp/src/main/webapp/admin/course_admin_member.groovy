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

def course_id = request.getParameter('course_id')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)
def rows = []

row = sql.firstRow('select * from COURSE where COURSE_ID=?', [course_id])

def course_name = row.course_name


q = """
select *
from USER_COURSE
inner join ST_USER on USER_COURSE.USER_ID=ST_USER.USER_ID
where USER_COURSE.COURSE_ID=? order by USER_COURSE.USER_ID
"""

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title("PLWeb - Teaching Materials")
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		a(href:"course_admin.groovy") {
			img (src:'icons/arrow_undo.png', border:0)
			span ('Back')
		}
		
		h2("Teaching Materials Administration")
		
		hr()

		h3("Book: ${course_name} (${course_id})")
		
		form (action:'course_admin_member_del.groovy', method:'post') {
			input(type:'hidden', name:'course_id', value:course_id)
			
			table(width:"100%") {
				tr {
					th('#')
					th("User ID")
					th("User Name")
					th("Is Owner")
					th(width:30, "Del")
				}
				c = 0
				
				sql.eachRow(q, [course_id]) {
					row ->
					//href_member   = "course_admin_member.groovy?id=${row.id}"
					tr {
						td (align:'center', ++c)
						td (row.user_id)
						td (row.name)
						td (row.is_owner)
						td {
							input (type: 'checkbox', name: 'user_id[]', value:row.user_id)
						}
					}
				}
			}
			
			div (align:'right') {
				button(type:'submit', 'Delete Selected')
			}
		}
		
		hr()
		
		h3('Add User')
		
		form (action: 'course_admin_member_save.groovy', method:'post') {
			input(type:'hidden', name:'course_id', value:course_id)
			
			span('User ID:')
			input(type:'text', name:'user_id')
			
			br()
			
			span('Is Owner:')
			input(type:'checkbox', name:'is_owner')
			
			br()
			
			button(type:'submit', 'Confirm')
		}
	}
}

sql.close()

