/**
 * ReportServices
 * @table ST_REPORTS
 */

import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import org.plweb.suite.common.xml.XmlFactory

helper = new CommonHelper(request, response, session)

// attr
user_id		= helper.fetch('user_id')
class_id	= helper.fetch('class_id')
course_id	= helper.fetch('course_id')
lesson_id	= helper.fetch('lesson_id')
mode		= helper.fetch('mode')

// fetch
session_id		= helper.fetch('s')	// session_id
ticket_no		= helper.fetch('t')	// ticket_no
question_id		= helper.fetch('question_id')
test_ok			= helper.fetch('test_ok')
test_error		= helper.fetch('test_error')
test_result		= helper.fetch('test_result')
time_run		= helper.fetch('time_run')
time_used		= helper.fetch('time_used')
time_cli		= helper.fetch('time_cli')
report_state	= helper.fetch('report_state')
report_code		= helper.fetch('report_code')
report_msg		= helper.fetch('report_msg')

// generate
time_passed		= new Date().time

sql = new Sql(helper.connection)

// check report exists
def isReported() {
	return sql.firstRow("""
		select count(*) as cc
		from ST_REPORTS
		where USER_ID=?
		and CLASS_ID=?
		and COURSE_ID=?
		and LESSON_ID=?
		and QUESTION_ID=?
	""", [
		user_id,
		class_id,
		course_id,
		lesson_id,
		question_id
	]).cc > 0
}

// save [ST_REPORT]
try {
	reported = isReported()
	
	data = [
		'TIME_UPDATED':	new Date().time,
		'TIME_USED':	time_used,
		'TYPE':			report_state,
		'CODE':			report_code,
		'MESSAGE':		report_msg,
		'TEST_OK':		test_ok,
		'TEST_ERROR':	test_error,
		'TEST_NUM':		(Integer.valueOf(test_ok)+Integer.valueOf(test_error))
	]
	
	duedate = sql.firstRow("""
		select BEGINDATE, DUEDATE
		from CLASS_COURSE
		where CLASS_ID=?
		and COURSE_ID=?
		and LESSON_ID=?
	""", [
		class_id,
		course_id,
		lesson_id
	])?.DUEDATE
	
	// before CLASS.LESSON.DUEDATE, allow update TIME_PASSED
	if (duedate && time_passed <= duedate) {
		if (test_ok > 0 && test_error == 0) {
			data['TIME_PASSED'] = time_passed
			data['IS_PASSED']	= 'y'
		}
	}

	where = [
		'USER_ID':		user_id,
		'CLASS_ID':		class_id,
		'COURSE_ID':	course_id,
		'LESSON_ID':	lesson_id,
		'QUESTION_ID':	question_id
	]

	// write ST_REPORT
	if (reported) {
		// update
		helper.simpleSqlUpdate 'ST_REPORTS', data, where, sql
	}
	else {
		// insert
		data['TIME_CREATED']	= new Date().time
		data['IS_REVIEWED']		= 'n'
		data['SCORE']			= 0
		
		helper.simpleSqlInsert 'ST_REPORTS', data+where, sql
	}
    println 'processed'
}
catch (e) {
	println e.message
}

sql.close()
