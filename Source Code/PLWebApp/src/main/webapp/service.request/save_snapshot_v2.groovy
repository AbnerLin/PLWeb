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
time_begin		= helper.fetch('time_begin')
time_cli		= helper.fetch('time_cli')
code			= helper.fetch('code')
keystroke		= helper.fetch('keystroke')

// generate
time_updated	= new Date().time

sql = new Sql(helper.connection)

// save [ST_SNAPSHOT]
try {
	data = [
		'SESSION_ID':	session_id,
		'TICKET_NO':	ticket_no,
		'USER_ID':		user_id,
		'CLASS_ID':		class_id,
		'COURSE_ID':	course_id,
		'LESSON_ID':	lesson_id,
		'QUESTION_ID':	question_id,
		'TIME_BEGIN':	time_begin,
		'TIME_CLI':		time_cli,
		'TIME_UPDATED':	time_updated,
		'CODE':			code,
		'KEYSTROKE':	keystroke
	]

	// write ST_SNAPSHOT
	// insert
	helper.simpleSqlInsert 'ST_SNAPSHOT', data, sql
}
catch (e) {
	println e.message
}

sql.close()