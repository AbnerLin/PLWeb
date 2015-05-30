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

def is_admin = session.get('is_admin')

if (!is_admin) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

q = """
select COURSE.*,
(select COUNT(*) from COURSE_FILE where VISIBLED='y' and COURSE_FILE.COURSE_ID=COURSE.COURSE_ID) as LESSON_COUNT,
(select SUM(TASKNUM) from COURSE_FILE where VISIBLED='y' and COURSE_FILE.COURSE_ID=COURSE.COURSE_ID) as EXERCISE_COUNT,
(select COUNT(*) from CLASS_COURSE where CLASS_COURSE.COURSE_ID=COURSE.COURSE_ID) as USAGE_COUNT,
(select COUNT(*) from ST_REPORTS where ST_REPORTS.COURSE_ID=COURSE.COURSE_ID) as REPORT_COUNT
from COURSE
where VISIBLED='y'
order by COURSE.COURSE_ID
"""

query_author = """
select ST_USER.NAME
from USER_COURSE
inner join ST_USER
on USER_COURSE.USER_ID=ST_USER.USER_ID
where USER_COURSE.COURSE_ID=?
order by ST_USER.NAME
"""

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title("PLWeb - Teaching Materials Administration")
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body (class: 'page') {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h2("Teaching Materials Administration")
		
		hr()

		h3("Current Books")
		table(width:"100%") {
			tr {
				th(width: 40, '#')
				th ('Book ID / ISBN')
				th ('Book Name')
				th ('Author(s)')
				th (class: 'small', width: 60, 'Lessons Count')
				th (class: 'small', width: 60, 'Exercises Count')
				th (class: 'small', width: 60, 'Usages')
				th (class: 'small', width: 60, 'Reports')
				th (class: 'small', width: 50, 'Members')
				th (class: 'small', width: 50, 'Update')
			}
			c = 0
			sql.eachRow(q) {
				row ->
				href_member = "course_admin_member.groovy?course_id=${row.course_id}"
				tr (class: c%2==0?'even':'odd') {
					td (align:'center', ++c)
					td (row.course_name)
					td (row.course_title)
					td {
						sql.eachRow(query_author, [row.course_id]) {
							row2->
							div(row2.name)
						}
					}
					td (row.lesson_count)
					td (row.exercise_count)
					td (row.usage_count)
					td (row.report_count)
					td (align:'center') {
						a(class: 'icon', href:href_member) {
							img (src:'../icons/group_edit.png', border:0)
						}
					}
					td (align:'center') {
						href_update = "course_admin_update.groovy?course_id=${row.course_id}"
						a (class: 'icon', href: href_update) {
							img (src: '../icons/arrow_refresh.png', border: 0)
						}
					}
				}
			}
		}
	}
}

sql.close()
