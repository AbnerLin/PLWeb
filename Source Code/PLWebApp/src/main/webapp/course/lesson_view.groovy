import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.suite.common.xml.XmlFactory

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

class_id = request.getParameter('class_id')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

uid   = session.get('uid')
utype = session.get('utype')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

varExamOnly = new File(request.getRealPath('config/ExamOnly')).text.trim().equals('y')

query1 = """
select TEXT_XML
from COURSE_FILE
where course_id=?
and lesson_id=?
"""

query2 = """
select BEGINDATE, DUEDATE
from CLASS_COURSE
where class_id=?
and course_id=?
and lesson_id=?
"""

query3 = """
select *
from ST_CLASS
where class_id=?
"""
// 從資料庫讀取XML
/*
text_xml = sql.firstRow(query1, [course_id, lesson_id]).text_xml
ByteArrayInputStream is = new ByteArrayInputStream(text_xml.getBytes("UTF-8"))
project = XmlFactory.readProject(is);
*/

// 日期格式, 讀取DUEDATE設定
sdf = new java.text.SimpleDateFormat('yyyy/MM/dd hh:mm:ss')
row = sql.firstRow(query2, [class_id, course_id, lesson_id])
duedate   = row.duedate
begindate = row.begindate

if (begindate != null) {
	begindate = new Date(begindate.toLong())
}

if (duedate != null) {
	duedate = new Date(duedate.toLong())
}


row = sql.firstRow(query3, [class_id])

isExamType = (row.semester.toInteger() == 5)

// 資料快取路徑設定
lesson_url = "/data/${course_id}/${lesson_id}/"

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Content Editing')
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
		link (rel:'stylesheet', type:'text/css', href:'lesson_view.css', media:'all')
		script (type:'text/javascript', src:'lesson_view.js', '')
		script (type:'text/javascript', src:'lesson_play.js', '')
	}
	body {
		div {
		
			a (href:'javascript:location.reload();', class: 'item') {
				img (src:'/icons/arrow_refresh.png', border:0)
				span ('Reload')
			}
			br()
			
			if (utype == 'T') {
				a (href: "lamp.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}", class:'item') {
					img (src:'/icons/chart_bar.png', border:0)
					span ('Classroom Report')
				}
				br()
			}

			a (href: "class_date.groovy?class_id=${class_id}", class: 'item') {
				img (src:'/icons/calendar.png', border:0)
				span ("Current Date/Time "+sdf.format(new Date()))
			}
			br()
			a (href: "class_date.groovy?class_id=${class_id}", class: 'item') {
				img (src:'/icons/calendar.png', border:0)
				if (begindate != null) {
					span ("Begin Date/Time "+sdf.format(begindate))
				}
				else {
					span ("Begin Date/Time Not Set")
				}
			}
			br()
			a (href: "class_date.groovy?class_id=${class_id}", class: 'item') {
				img (src:'/icons/calendar.png', border:0)
				if (duedate != null) {
					span ("Due Date/Time "+sdf.format(duedate))
				}
				else {
					span ("Due Date/Time Not Set")
				}
			}
			br()
		
			hr()

			if (isExamType) {
				if (varExamOnly) {
					if ((begindate != null && begindate <= new Date()) && (duedate != null && duedate >= new Date())) {
						href_editor = "webstart.groovy?mode=student&course_id=${course_id}&lesson_id=${lesson_id}&class_id=${class_id}"
						a (href: href_editor, class: 'item') {
							img (src:'/icons/plugin_edit.png', border:0)
							span ('開始測驗')
						}
						br()
					}
					else {
						p('測驗尚未開始，請於測驗時間開始時，按Reload連結')
					}
				}
			}
			else {
				href_play = "javascript: lessonPlay('${course_id}', '${lesson_id}');"
				a (href: href_play, class: 'item') {
					img (src:'/icons/plugin_edit.png', border:0)
					span ('閱讀課程內容')
				}
				br()
				
				if (begindate != null && begindate <= new Date()) {
					href_editor = "webstart.groovy?mode=student&course_id=${course_id}&lesson_id=${lesson_id}&class_id=${class_id}"
					a (href: href_editor, class: 'item') {
						img (src:'/icons/plugin_edit.png', border:0)
						span ('開始練習')
					}
					br()
				}
			
				if (duedate != null && duedate <= new Date()) {
					href_editor = "webstart.groovy?mode=answer&course_id=${course_id}&lesson_id=${lesson_id}&class_id=${class_id}"
					a (href: href_editor, class: 'item') {
						img (src:'/icons/plugin_edit.png', border:0)
						span ('觀看解答')
					}
					br()
				}
			}
			
			
			div {
				/*
				a (href:'javascript:goPre();') {
					img (src:'/icons/control_rewind.png', border:0)
				}
				select (id:'urlbox', onchange:"view();", style:'width:240px;font-size:13px') {
					file_html = project.getProperty('file.html')
					urlstr = (file_html&&file_html.startsWith('http://'))?file_html:(lesson_url+file_html)
					option (value:urlstr, project.title)
					c = 0
					project.tasks.each {
						task ->
						file_html = task.getPropertyEx('file.html')
						urlstr = (file_html&&file_html.startsWith('http://'))?file_html:(lesson_url+task.getPropertyEx('file.html'))
						option (value: urlstr, "　- ${task.title}")
					}
				}
				a (href:'javascript:goNext();') {
					img (src:'/icons/control_fastforward.png', border:0)
				}
				*/
			}
			
		}
	}
}

sql.close()
