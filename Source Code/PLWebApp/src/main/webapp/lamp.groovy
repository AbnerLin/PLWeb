import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid   = session.get('uid')
uname = session.get('uname')
utype	= session.get('utype')

class_id  = request.getParameter('class_id')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

time = request.getParameter('time')
showTimeCol = (request.getParameter('time')=='col')
showTimeRow = (request.getParameter('time')=='row')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

query1 = """
select TEXT_XML
from COURSE_FILE
where COURSE_ID=?
and LESSON_ID=?
"""

query2 = """
select U.USER_ID, U.NAME, U.ENROLLMENT
from ST_USER U
inner join USER_CLASS UC
on U.USER_ID=UC.USER_ID
where CLASS_ID=?
and UC.IS_TEACHER=?
order by U.ENROLLMENT
"""

text_xml = sql.firstRow(query1, [course_id, lesson_id]).text_xml

dataXml = new XmlParser().parseText(text_xml)

course_title = dataXml.title[0].text()

class_name = sql.firstRow('SELECT CLASS_NAME from ST_CLASS WHERE CLASS_ID=?', [class_id]).CLASS_NAME.toString()

duedate = sql.firstRow('select DUEDATE from CLASS_COURSE where CLASS_ID=? and COURSE_ID=? and LESSON_ID=?', [class_id, course_id, lesson_id]).duedate

STUDENTLIST = []
sql.eachRow(query2, [class_id, 'n']) {
	STUDENTLIST << [id:it.USER_ID, name:it.NAME, enrollment:it.ENROLLMENT]
}

QUERYTIME = 0
SQLDATA = new HashMap()
L_TIMEDATA = new HashMap()
Q_TIMEDATA = new HashMap()
TOTAL_TIME_USED = 0
MAX_TIME_USED = 0
AVG_TIME_USED = 0

try {
	QUERYTIME = new Date().time
	
	sqlss = """
select a.USER_ID,
	a.QUESTION_ID,
	a.TYPE,
	a.TIME_SEQ,
	a.TIME_USED,
	a.REVIEW
from ST_CODE a
where a.CLASS_ID=?
and a.COURSE_ID=?
and a.LESSON_ID=?
order by a.USER_ID, a.QUESTION_ID
"""

	sql.eachRow(sqlss, [class_id, course_id, lesson_id]) {
		if (!SQLDATA.containsKey(it.USER_ID)) {
			SQLDATA.put(it.USER_ID, new HashMap())
		}
		
		qid = it.QUESTION_ID.toInteger()
		
		COURSEDATA = SQLDATA.get(it.USER_ID)
		COURSEDATA.put(qid, [TYPE: it.TYPE, TIME_SEQ: it.TIME_SEQ, TIME_USED: it.TIME_USED, REVIEW: it.REVIEW])
		
		TIME_USED = it.TIME_USED?it.TIME_USED.toInteger():0
		
		SPEC_TIME_USED = TIME_USED
		
		if (!L_TIMEDATA.containsKey(it.USER_ID)) {
			L_TIMEDATA.put(it.USER_ID, TIME_USED)
		}
		else {
			SPEC_TIME_USED = L_TIMEDATA.get(it.USER_ID) + TIME_USED
			L_TIMEDATA.put(it.USER_ID, SPEC_TIME_USED)
		}
		
		if (SPEC_TIME_USED > MAX_TIME_USED) {
			MAX_TIME_USED = SPEC_TIME_USED
		}
		
		TOTAL_TIME_USED += TIME_USED
		
		if (!Q_TIMEDATA.containsKey(qid)) {
			Q_TIMEDATA.put(qid, TIME_USED)
		}
		else {
			if (TIME_USED > Q_TIMEDATA.get(qid)) {
				Q_TIMEDATA.put(qid, TIME_USED)
			}
		}
	}

	QUERYTIME = new Date().time - QUERYTIME
}
catch(e) {
	print e.message
}

if (TOTAL_TIME_USED > 0 && L_TIMEDATA.size() > 0) {
	AVG_TIME_USED = (TOTAL_TIME_USED/L_TIMEDATA.size()).toInteger()
}
else {
	AVG_TIME_USED = 0
}

html.setDoubleQuotes(true)
html.html {
	head {
		title('查看狀態')
		script(type: 'text/javascript', src: 'view_lamp.js', '')
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {
		if (utype == 'T') {
  		input(type: "button", id:"refresh", value:"Refresh Now", class:"button", onclick:'javascript:refresh()')
  		
		h2('Classroom Learning Report')
		
		div {
			span("${class_name} (${class_id})")
			br()
		}

  		div {
  			href_time_col = "lamp.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}&time=col"
  			href_time_row = "lamp.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}&time=row"
  			href_time_none = "lamp.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}"
  					
  			span ('Time Detail: ')
  			span ('[')
  			if (showTimeCol) {
  				Strong ('Column')	  			
  			}
  			else {
  				a (href: href_time_col) {
	  				span ('Column')
	  			}
  			}
  			span (' | ')
  			if (showTimeRow) {
  				Strong ('Row')	  			
  			}
  			else {
  				a (href: href_time_row) {
	  				span ('Row')
	  			}
  			}
  			span (' | ')
  			if (showTimeCol) {
  	  			a (href: href_time_none) {
  	  				span ('None')
  	  			}  				
  			}
  			else {
  				strong ('None')
  			}
  			span (']')
  		}
  		
  		h3("${course_title}")

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

		table (class: 'classroom-report', width: '100%') {
			tr {
				th (class: 'small', width: 20, '#')
				th (class: 'small', width: 90, 'Enrollment ID')
				th (class: 'small', width: 50, 'Student Name')
				th (class: 'small', width: 90, 'User ID')
				th (class: 'small', width: 40, 'Edit')
				th ('Status Report')
				th (width: 110, 'Time')
				/*
				th (width:60, '#total')
				th (width:60, '#tested')
				th (width:60, '#test_ok')
				*/
			}
			
			//task id cache
			task_id_list = []
			dataXml.task.each {
				task ->
				task_id_list << task.id[0].text().toInteger()
			}
			
			c = 0
			STUDENTLIST.each {
				student ->
				
				href_view = "view_lamp.groovy?class_id=${class_id}&user_id=${student.id}"
				href_edit = "webstart.groovy?mode=teacher&course_id=${course_id}&lesson_id=${lesson_id}&class_id=${class_id}&user_id=${student.id}"
				
				c++
				tr {
					td(align: 'center', "${c}")
					td(align: 'center', "${student.enrollment}")
					td {
						a (href: href_view, student.name)
					}
					td(align: 'center', "${student.id}")
					td (align: 'center') {
						a (href: href_edit, class: 'icon') {
							img (src: 'icons/plugin_edit.png', border: 0)
						}
					}

					total = 0
					test = 0
					ok = 0

					td {
						task_id_list.each {
							qid ->
							
							font_color = '000000'
							
							height = 0
							
							time_used = 0

							if (!SQLDATA.get(student.id) ||
								!(q = SQLDATA.get(student.id).get(qid))) {
								font_color = 'AAAAAA'
							}
							else if (q.TYPE == 'test_ok') {
								if (q.TIME_SEQ <= duedate) {
									font_color = '00FF00'
								}
								else {
									font_color = '556B2F'
								}
								ok ++
								test ++
								
								time_used = q.TIME_USED
							}
							else {
								font_color = 'FF0000'
								test ++
								
								time_used = q.TIME_USED
							}
							
							href_player = sprintf('keystroke_player.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s',
									class_id,
									course_id,
									lesson_id,
									qid,
									student.id)
									
							a (href: href_player, target: '_blank', style: 'text-decoration: none') {
								
								if (showTimeCol) {
									if (!time_used || !Q_TIMEDATA.get(qid)) {
										pect = 0
									}
									else {
										pect = ((time_used/Q_TIMEDATA.get(qid))*100).toInteger()
									}
									img (src: "light/${font_color}_${pect}.png", border: 0)
								}
								else if (showTimeRow) {
									if (!time_used || !L_TIMEDATA.get(student.id)) {
										pect = 0
									}
									else {
										pect = ((time_used/L_TIMEDATA.get(student.id))*100).toInteger()
									}
									img (src: "light/${font_color}_${pect}.png", border: 0)
								}
								else {
									img(src:"light/${font_color}.png", border:0)
								}
							}
							
							total ++
						}
						
						br()
						
						task_id_list.each {
							qid ->
							
							review = 0;
							
							if (!SQLDATA.get(student.id) ||
								!(q = SQLDATA.get(student.id).get(qid))) {
								review = 1
							}
							else if (q.TYPE == 'test_ok') {
								if (q.REVIEW == null) {
									review = 0;
								}
								else {
									review = q.REVIEW
								}
							}
							else {
								review = 1
							}
							
							next_review = review.toInteger() + 1
							if (next_review > 2) next_review = 0
							href_review = sprintf('lamp_review.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s&time=%s&review=%s',
								class_id,
								course_id,
								lesson_id,
								qid,
								student.id,
								time,
								next_review)
							a (href: href_review, style: 'text-decoration: none') {
								img (src: "light/review-${review}.png", border: 0)
							}
						}
					}
					td {
						if (!L_TIMEDATA.get(student.id) || !MAX_TIME_USED || !AVG_TIME_USED) {
							pect = 0
						}
						else {
							avg_dist = L_TIMEDATA.get(student.id) - AVG_TIME_USED
							if (avg_dist == 0) {
								pect = 50
							}
							else if (avg_dist > 0) {
								pect = 50+((avg_dist/(MAX_TIME_USED-AVG_TIME_USED))*50).toInteger()
							}
							else /* < 0 */ {
								//pect = ((L_TIMEDATA.get(student.id)/AVG_TIME_USED)*50).toInteger()
								pect = 50+((avg_dist/AVG_TIME_USED)*50).toInteger()
							}
						}
						a (href: '#') {
							img (src: "light/SMALLTIME_${pect}.png", border: 0)
						}
					}
					/*
					td (align:'center', total)
					td (align:'center', test)
					td (align:'center', ok)
					*/
				}
			}
		}
		
		span ("Query Time: ${QUERYTIME} ms")
		}
		else {
			h2("※請勿作弊!!")
		}
	}
}
sql.close()
