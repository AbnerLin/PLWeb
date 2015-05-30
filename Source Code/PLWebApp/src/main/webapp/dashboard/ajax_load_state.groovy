import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def uid		= helper.sess('uid')
def uname	= helper.sess('uname')

if (!session) {
	response.sendError 403
	return
}

class_id  = helper.fetch('class_id')
course_id = helper.fetch('course_id')
lesson_id = helper.fetch('lesson_id')

sql = new Sql(helper.connection)

class_name = sql.firstRow("SELECT CLASS_NAME from ST_CLASS WHERE CLASS_ID=?", [class_id]).CLASS_NAME
//def rows = []
//sql.eachRow("select * from course order by course_id") {
//	rows.add([id:it.course_id, name:it.course_name, title:it.course_title])
//}
//sql.close()

queryTime = 0
SQLDATA = new HashMap()
try {
	queryTime = new Date().time

	sql.eachRow("""
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
	""", [class_id, uid]) {
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
html.div {
	total = 0	
	tried = 0
	ok = 0		// test_ok before due
	due = 0		// test_ok after due
	
	table (width: '100%') {
		sql.eachRow("""
			select COURSE_ID,
				LESSON_ID,
				TITLE,
				(select TEXT_XML
					from COURSE_FILE
					where COURSE_FILE.COURSE_ID=CLASS_COURSE.COURSE_ID
					and COURSE_FILE.LESSON_ID=CLASS_COURSE.LESSON_ID) as TEXT_XML
			from CLASS_COURSE
			where CLASS_ID=?
			and COURSE_ID=?
			and LESSON_ID=?
			order by SEQNUM
			""", [class_id, course_id, lesson_id]
		) {
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
				td {
					task_id_list.each {
						qid ->

						font_color = '000000'

						if (
							!SQLDATA.get(course_id) ||
							!SQLDATA.get(course_id).get(lesson_id) ||
							!(q = SQLDATA.get(course_id).get(lesson_id).get(qid))
						) {
							font_color = 'AAAAAA'
						}
						else if ('test_ok'.equalsIgnoreCase(q.TYPE)) {
							if (q.TIME_PASSED <= duedate) {
								font_color = '00FF00'
								ok ++
							}
							else {
								font_color = '556B2F'
								due ++
							}
						}
						else {
							font_color = 'FF0000'
							tried ++
						}
						total ++
						
						
						img (src: "light/${font_color}.png", border: 0)
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
								review = q.IS_REVIEWED
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
	div {
		ch = ['通過測試':[ok, '00FF00'], '通過測試(逾期)':[due, '556B2F'], '練習中':[tried, 'FF0000'], '尚未練習':[total-ok-due-tried, 'AAAAAA']]
		
		chl  = []
		chd  = []
		chco = []
		chdl = []
		ch.each {
			key, val ->
			if (val[0]) {
				chl  << key
				chd  << val[0]
				chco << val[1]
				chdl << "${key}(${val[0]})"
			}
		}
		chl  = chl.join('|')
		chd  = chd.join(',')
		chco = chco.join('|')
		chdl = chdl.join('|')
		img (src: helper.make_url('http://chart.apis.google.com/chart', [cht: 'p3', 'chd': "t:${chd}", chs: '560x160', chl: "${chl}", chco: "${chco}", chdl: "${chdl}", chdls: "000000,16"]))
	}
}
