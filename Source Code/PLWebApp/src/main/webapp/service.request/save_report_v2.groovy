import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import org.plweb.suite.common.xml.XmlFactory

helper = new CommonHelper(request, response, session)

// attr
is_legal	= helper.attr('is_legal')
user_id		= helper.attr('user_id')
class_id	= helper.attr('class_id')
course_id	= helper.attr('course_id')
lesson_id	= helper.attr('lesson_id')
mode		= helper.attr('mode')

// illegal?
if (is_legal != true) {
	println "illegal access"
	return
}

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
report_msg		= helper.fetch('report_msg')

// generate
time_passed		= new Date().time

sql = new Sql(helper.connection)

// check report exists
def isReported() {
	return sql.firstRow("""
		select count(*) as cc
		from ST_REPORT
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
		'SESSION_ID':	session_id,
		'TICKET_NO':	ticket_no,
		'TEST_OK':		test_ok,
		'TEST_ERROR':	test_error,
		'TEST_RESULT':	test_result,
		'REPORT_STATE':	report_state,
		'REPORT_MSG':	report_msg,
		'TIME_RUN':		time_run,
		'TIME_USED':	time_used,
		'TIME_CLI':		time_cli,
		'TIME_UPDATED':	new Date().time,
		'IS_PASSED':	(test_ok.toInteger() > 0 && test_error.toInteger() == 0)?'y':'n'
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
	]).DUEDATE
	
	// before CLASS.LESSON.DUEDATE, allow update TIME_PASSED
	if (duedate && time_passed <= duedate) {
		if (test_ok > 0 && test_error == 0) {
			data['TIME_PASSED'] = time_passed
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
		helper.simpleSqlUpdate 'ST_REPORT', data, where, sql
	}
	else {
		// insert
		helper.simpleSqlInsert 'ST_REPORT', data+where, sql
	}
	
	// write ST_REPORT_LOG
	helper.simpleSqlInsert 'ST_REPORT_LOG', data+where, sql
}
catch (e) {
	println e.message
}

sql.close()