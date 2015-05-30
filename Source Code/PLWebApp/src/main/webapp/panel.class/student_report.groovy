import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

helper = new CommonHelper(request, response, session)

def uid   = session.get('uid')
def uname = session.get('uname')

def class_id  = request.getParameter('class_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

// 查詢LESSON XML資料
query1 = """
select COURSE_ID,
	LESSON_ID,
	TITLE,
	(select TEXT_XML
		from COURSE_FILE
		where COURSE_FILE.COURSE_ID=CLASS_COURSE.COURSE_ID
		and COURSE_FILE.LESSON_ID=CLASS_COURSE.LESSON_ID) as TEXT_XML
from CLASS_COURSE
where CLASS_ID=?
order by SEQNUM
"""

// 如果指定USER_ID，則重新設定報表學生資料
user_id = request.getParameter('user_id')
if (user_id) {
	user_row = sql.firstRow('select * from ST_USER where USER_ID=?', [user_id])
	uid = user_row.user_id
	uname = user_row.name
}

def class_name = sql.firstRow("SELECT CLASS_NAME from ST_CLASS WHERE CLASS_ID=?", [class_id]).CLASS_NAME.toString()
//def rows = []
//sql.eachRow("select * from course order by course_id") {
//	rows.add([id:it.course_id, name:it.course_name, title:it.course_title])
//}
//sql.close()

queryTime = 0
SQLDATA = new HashMap()
try {
	queryTime = new Date().time
	
	sqlss = """
select 
	COURSE_ID,
	LESSON_ID,
	QUESTION_ID,
	IS_PASSED,
	TYPE,
	TIME_PASSED,
	TIME_USED,
	IS_REVIEWED
from ST_REPORTS
where CLASS_ID=?
and USER_ID=?
order by COURSE_ID, LESSON_ID, QUESTION_ID
"""

	sql.eachRow(sqlss, [class_id, uid]) {
		if (!SQLDATA.containsKey(it.COURSE_ID.toInteger())) {
			SQLDATA.put(it.COURSE_ID.toInteger(), new HashMap())
		}
		COURSEDATA = SQLDATA.get(it.COURSE_ID.toInteger())
		if (!COURSEDATA.containsKey(it.LESSON_ID.toInteger())) {
			COURSEDATA.put(it.LESSON_ID.toInteger(), new HashMap())
		}
		COURSEDATA.get(it.LESSON_ID.toInteger()).put(it.QUESTION_ID.toInteger(), [TYPE: it.TYPE, IS_PASSED: it.IS_PASSED, TIME_PASSED: it.TIME_PASSED, TIME_USED: it.TIME_USED, IS_REVIEWED: it.IS_REVIEWED])
	}

	queryTime = new Date().time - queryTime

}
catch(e) {
	print e.message
}

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb Student Report')
		base(href: helper.basehref())
		script(type: 'text/javascript', src: 'view_lamp.js', '')
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {
  		input(type: "button", id:"refresh", value:"Refresh Now", class:"button", onclick:'javascript:refresh()')
		h2('Learning Status Report')


		div {
			span("Student: ${uname} (${uid})")
			br()
			span("Class: ${class_name} (${class_id})")
		}

		br()

		div (class: 'light-color') {
			img(src:'light/00FF00.png')
			span('on time')
			img(src:'light/556B2F.png')
			span('late')
			img(src:'light/FFFF00.png')
			span('editing')
			img(src:'light/FF0000.png')
			span('error')
			img(src:'light/AAAAAA.png')
			span('havn\'t tried')
		}
		
		total = 0
		test = 0
		done = 0
		ok = 0

		table (width: '100%') {
			tr {
				th (width:250, 'Lesson Name')
				th ('Status')
			}
			sql.eachRow(query1, [class_id]) {
				row ->
				course_id   = row.course_id.toInteger()
				lesson_id   = row.lesson_id.toInteger()
				lesson_text = row.title
				xml = new XmlParser().parseText(row.text_xml)
				
				duedate = sql.firstRow('select DUEDATE from CLASS_COURSE where CLASS_ID=? and COURSE_ID=? and LESSON_ID=?', [class_id, course_id, lesson_id]).duedate
				
				//task id cache
				task_id_list = []
				xml.task.each {
					task ->
					task_id_list << task.id[0].text().toInteger()
				}
				
				tr {
					td("${lesson_text}")

					td {
						task_id_list.each {
							qid ->

							font_color = '000000'

							if (!SQLDATA.get(course_id)) {
								font_color = 'AAAAAA'
							}
							else if (!SQLDATA.get(course_id).get(lesson_id)) {
								font_color = 'AAAAAA'
							}
							else if (!(q = SQLDATA.get(course_id).get(lesson_id).get(qid))) {
								font_color = 'AAAAAA'
							}
							else if ('test_ok'.equalsIgnoreCase(q.TYPE)) {
								if (q.TIME_PASSED <= duedate) {
									font_color = '00FF00'
									ok ++
								}
								else {
									font_color = '556B2F'
								}
								done ++
								test ++
							}
							else {
								font_color = 'FF0000'
								test ++
							}
							total ++
							
							img(src:"light/${font_color}.png", border: 0)
						}
						/*
						br()
						
						task_id_list.each {
							qid ->
							
							review = 0
							
							if (!SQLDATA.get(course_id) ||
								!SQLDATA.get(course_id).get(lesson_id) ||
								!(q = SQLDATA.get(course_id).get(lesson_id).get(qid))) {
								review = 1
							}
							else if ('y'.equalsIgnoreCase(q.IS_PASSED)) {
								if (q.IS_REVIEWED == null) {
									review = 0;
								}
								else {
									//review = q.REVIEW
									review = 0
								}
							}
							else {
								review = 1
							}
							
							next_review = review.toInteger() + 1
							if (next_review > 2) next_review = 0						
							img (src: "light/review-${review}.png", border: 0)
						}
						*/
					}
				}
			}
		}
		
		h3('Report')
		table (width: 400) {
			tr {
				th ('Correct Rate')
				td ("${done} / ${total}")
				td (Math.floor(done/total*100).toInteger() + ' %')
			}
			tr {
				th ('On Time Rate')
				td ("${ok} / ${total}")
				td (Math.floor(ok/total*100).toInteger() + ' %')
			}
		}
		
		span ("Query Time: ${queryTime} ms")
	}
}

sql.close()