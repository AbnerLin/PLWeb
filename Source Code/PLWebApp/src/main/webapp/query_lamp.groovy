import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

class_id  = request.getParameter('class_id')
email     = request.getParameter('email')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

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

nowTime = new Date().time

// 如果指定USER_ID，則重新設定報表學生資料

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - 測驗成績查詢')
		script (type: 'text/javascript', src: 'lesson_play.js', '')
		script (type: 'text/javascript', src: 'view_lamp.js', '')
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {

		h2('測驗成績查詢')
		
		form (action: 'query_lamp.groovy', method: 'post') {
			table {
				tr {
					td {
						span ('帳號')
					}
					td {
						input (name: 'email', value: email)
						span ('請填入E-Mail或使用者編號')
					}
				}
				tr {
					td {
						span ('測驗代號')
					}
					td {
						input (name: 'class_id', value: class_id)
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						input (type: 'submit', value: '查詢')
					}
				}
			}
		}

		user_id = null
		
		//Check USER_ID, CLASS_ID
		user_row = sql.firstRow('select * from ST_USER where EMAIL=? or USER_ID=?', [email, email])
		class_row = sql.firstRow('select * from ST_CLASS where CLASS_ID=?', [class_id])
		
		if (user_row && class_row) {
			user_id    = user_row.user_id
			user_name  = user_row.name
			user_email = user_row.email
			class_name = class_row.class_name
		}
		
		if (!user_id) {
			if (email) {
				p ('查無資料')
			}
			p ('請輸入正確的使用者及測驗代號')
		}
		
		if (user_id) {
			
		
			
			
			queryTime = 0
			SQLDATA = new HashMap()
			try {
				queryTime = new Date().time
				
				sqlss = """
			select MESSAGE_ID,
				COURSE_ID,
				LESSON_ID,
				QUESTION_ID,
				TYPE,
				TIME_SEQ,
				TIME_USED,
				REVIEW
			from ST_CODE
			where CLASS_ID=?
			and USER_ID=?
			order by COURSE_ID, LESSON_ID, QUESTION_ID
			"""
			
				sql.eachRow(sqlss, [class_id, user_id]) {
					if (!SQLDATA.containsKey(it.COURSE_ID.toInteger())) {
						SQLDATA.put(it.COURSE_ID.toInteger(), new HashMap())
					}
					COURSEDATA = SQLDATA.get(it.COURSE_ID.toInteger())
					if (!COURSEDATA.containsKey(it.LESSON_ID.toInteger())) {
						COURSEDATA.put(it.LESSON_ID.toInteger(), new HashMap())
					}
					COURSEDATA.get(it.LESSON_ID.toInteger()).put(it.QUESTION_ID.toInteger(), [ID: it.MESSAGE_ID, TYPE: it.TYPE, TIME_SEQ: it.TIME_SEQ, TIME_USED: it.TIME_USED, REVIEW: it.REVIEW])
				}
			
				queryTime = new Date().time - queryTime
			
			}
			catch(e) {
				print e.message
			}
	
	
			table {
				tr {
					td {
						span ('受測者姓名')
					}
					td {
						span (user_name)
					}
				}
				tr {
					td {
						span ('帳號')
					}
					td {
						span (user_email)
						span ("(編號 $user_id)")
					}
				}
				tr {
					td {
						span ('測驗代號')
					}
					td {
						span (class_id)
					}
				}
				tr {
					td {
						span ('測驗名稱')
					}
					td {
						span (class_name)
					}
				}
			}
	
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
					th (width:200, 'Lesson Name')
					th ('Status')
				}
				sql.eachRow(query1, [class_id]) {
					row ->
					course_id   = row.course_id.toInteger()
					lesson_id   = row.lesson_id.toInteger()
					lesson_text = row.title
					xml = new XmlParser().parseText(row.text_xml)
					
					duedate = sql.firstRow('select DUEDATE from CLASS_COURSE where CLASS_ID=? and COURSE_ID=? and LESSON_ID=?', [class_id, course_id, lesson_id]).duedate

					// 判斷是否超過測驗後三天(超過三天，開放查詢；否則，踢開)
					if (nowTime < (duedate + 259200000)) {
						print "<script>alert('尚未開放，目前未超過測驗三天!');location.href='permission_denied.groovy';</script>"
						//response.sendRedirect('permission_denied.groovy')
						//return;
					}
					
					//task id cache
					task_id_list = []
					xml.task.each {
						task ->
						task_id_list << task.id[0].text().toInteger()
					}
					
					tr {
						td {
							a (href: "javascript: lessonPlay(${row.course_id}, ${row.lesson_id});", title: 'Play Lesson Content', "${lesson_text}")
						}
	
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
								else if (q.TYPE == 'test_ok') {
									if (q.TIME_SEQ <= duedate) {
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
								
								url_pattern = 'query_code.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s'
								href_code = sprintf(
									url_pattern,
									class_id,
									course_id,
									lesson_id,
									qid,
									user_id
								)
										
								a (href: href_code, class: 'icon', target: '_blank') {
									img(src: "light/${font_color}.png", border: 0)
								}
							}
							
							br()
							
							task_id_list.each {
								qid ->
								
								review = 0
								
								if (!SQLDATA.get(course_id) ||
									!SQLDATA.get(course_id).get(lesson_id) ||
									!(q = SQLDATA.get(course_id).get(lesson_id).get(qid))) {
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
								a (href: '#', style: 'text-decoration: none') {
									img (src: "light/review-${review}.png", border: 0)
								}
							}
						}
					}
				}
			}
			
			uc_row = sql.firstRow('select * from USER_CLASS where USER_ID=? and CLASS_ID=?', [user_id, class_id]);
			
			h3('測驗結果')
			if (uc_row) {
				p (uc_row.result)
			}
			else {
				p ('未參加測驗')
			}
		}
	}
}

sql.close()