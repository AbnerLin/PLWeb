import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

user_id = request.getParameter('user')
class_id = request.getParameter('class')
course_id = request.getParameter('course')
lesson_id = request.getParameter('lesson')


def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

uname = sql.firstRow("select NAME from ST_USER where USER_ID=?", [user_id]).name

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - My Account')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h2("程式設計學習履歷")
		h2("PLWeb e-Portfolio")
		h2("${uname} (${user_id})")

		hr()
		h3('課程紀錄')
		
		table {
			tr {
				th ('#')
				th ('Class Name')
				th ('School')
				th ('Department')
				th ('Semester')
				th ('Type')
				th ('View')
			}
			query_class = """
select USER_CLASS.IS_TEACHER,ST_CLASS.CLASS_ID,ST_CLASS.CLASS_NAME,ST_CLASS.SCHOOL,ST_CLASS.DEPARTMENT,ST_CLASS.YEARS,ST_CLASS.SEMESTER from USER_CLASS
inner join ST_CLASS
on USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID
where USER_ID=?
and ST_CLASS.ALIVE='y'
order by YEARS,SEMESTER
"""
			c=0
			try {
			sql.eachRow(query_class, [user_id]) {
				row->
				tr (class: c%2==0?'even':'odd') {
					td (++c)
					td (row.class_name)
					td (row.school)
					td (row.department)
					td {
						span(row.years)
						span("/")
						switch (row.semester) {
						case '1':
							span('spring')
							break;
						case '2':
							span('summer')
							break;
						case '3':
							span('fall')
							break;
						case '4':
							span('winter')
							break;
						}
					}
					td {
						switch (row.is_teacher) {
							case 'y':
								span('Teacher')
								break;
							default:
								span('Student')
								break;
						}
					}
					td {
						href_class = "portfolio.groovy?user="+URLEncoder.encode(user_id)+"&class="+URLEncoder.encode(row.class_id.toString())
						a (href:href_class) {
							span('View')
						}
					}
				}
			}
			}catch(e){print e}
		}
		
		if (class_id) {		
			hr()
			h3('課程內容')
			
			query_lesson = """
select *
from CLASS_COURSE
where CLASS_ID=?
order by SEQNUM
"""

			table {
				tr {
					th ('#')
					th ('Lesson Name')
					th ('View')
				}
				c = 0
				sql.eachRow(query_lesson, [class_id]) {
					row->
					tr (class: c%2==0?'even':'odd') {
						td (++c)
						td (row.title)
						td {
							href_lesson = "portfolio.groovy?user="+URLEncoder.encode(user_id)+"&class="+row.class_id+"&course="+row.course_id+"&lesson="+row.lesson_id
							a (href:href_lesson) {
								span('View')
							}
						}
					}
				}
			}
		}
		
		if (course_id && lesson_id) {
			hr()
			h3('練習紀錄')
			
			query_lesson = """
select TITLE
from CLASS_COURSE
where CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
"""

			title = sql.firstRow(query_lesson, [class_id, course_id, lesson_id]).title
			
			h3(title)

			table {
				tr {
					th ('#')
					th ('Lesson Name')
					th ('View')
				}
				c = 0
				sql.eachRow(query_lesson, [class_id]) {
					row->
					tr (class: c%2==0?'even':'odd') {
						td (++c)
						td (row.title)
						td {
							href_lesson = "portfolio.groovy?user="+URLEncoder.encode(user_id)+"&class="+row.class_id+"&course="+row.course_id+"&lesson="+row.lesson_id
							a (href:href_lesson) {
								span('View')
							}
						}
					}
				}
			}
		}
	}
}

sql.close()