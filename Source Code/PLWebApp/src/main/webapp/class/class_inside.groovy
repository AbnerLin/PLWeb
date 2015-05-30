import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import groovy.sql.Sql

def uid = session.get('uid')
def utype = session.get('utype')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

class_id = request.getParameter('id')

sql.executeUpdate('update ST_USER set LAST_CLASS=? where USER_ID=?', [class_id, uid])

query1 = """
select CLASS_COURSE.*, COURSE.COURSE_TITLE
from CLASS_COURSE
inner join COURSE
	on CLASS_COURSE.COURSE_ID=COURSE.COURSE_ID
where CLASS_COURSE.CLASS_ID=?
order by CLASS_COURSE.SEQNUM
"""

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Class Menu')
		link(rel: 'stylesheet', type: 'text/css', href: 'default.css')
	}
	body {
		div(class: 'left-menu') {
			ul {
				sql.eachRow(query1, [class_id]) {
					row ->
					
					li {
						a(href: "lesson_view.groovy?class_id=${row.class_id}&course_id=${row.course_id}&lesson_id=${row.lesson_id}", target: 'mainFrame') {
							img (src:'icons/book.png', border:0)
							span (row.title)
						}
						br ()
						span (" ${row.course_title}")
					}
				}
			}
		}
		p {
			a(href: "view_lamp.groovy?class_id=${class_id}", target: 'mainFrame') {
				img (src:'icons/chart_bar.png', border:0)
				span ('Status')
			}
			br()
			a(href: "class_date.groovy?class_id=${class_id}", target: 'mainFrame') {
				img (src:'icons/calendar.png', border:0)
				span ('Due Dates')
			}
			br()
			if (utype=='T') {
				a(href:"schedule.groovy?id=${class_id}", target:"mainFrame") {
				//a(href: '#', target:"mainFrame", onclick: "javascript:window.open('form.schedule/schedule.groovy?class_id=${class_id}', 'plweb_window_schedule', 'width=800, height=600, toolbar=no, menubar=no, resizable=yes, location=no, status=no');") {
					img (src:'icons/calendar.png', border:0)
					span ('Schedule')
				}
				br()
			}
			a(href:"javascript:location.reload()") {
				img (src:'icons/arrow_refresh.png', border:0)
				span ('Reload')
			}
			br()
			a(href:"class.groovy") {
				img (src:'icons/arrow_undo.png', border:0)
				span ('Return')
			}
		}
	}
}

sql.close()
