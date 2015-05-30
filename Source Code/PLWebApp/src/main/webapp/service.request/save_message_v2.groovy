import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import org.plweb.suite.common.xml.XmlFactory

helper = new CommonHelper(request, response, session)

session_id	= helper.fetch('s')	// session_id
ticket_no	= helper.fetch('t')	// ticket_no

is_legal	= helper.attr('is_legal')
user_id		= helper.attr('user_id')
class_id	= helper.attr('class_id')
course_id	= helper.attr('course_id')
lesson_id	= helper.attr('lesson_id')
mode		= helper.attr('mode')

time_seq		= new Date().time

if (is_legal != true) {
	println "illegal access"
	return
}

question_id		= helper.fetch('question_id')
time_cli		= helper.fetch('time_cli')
time_grp		= helper.fetch('time_grp')
time_run		= helper.fetch('time_run')
time_seg		= helper.fetch('time_seg')
type			= helper.fetch('type')
result			= helper.fetch('result')
event			= helper.fetch('event')
code			= helper.fetch('code')
msg				= helper.fetch('msg')
msg_error		= helper.fetch('msg_error')
msg_exit		= helper.fetch('msg_exit')
keystroke		= helper.fetch('keystroke')

q1 = """
	select count(*) as cc
	from ST_MESSAGE
	where CLASS_ID=?
	and USER_ID=?
	and COURSE_ID=?
	and LESSON_ID=?
	and QUESTION_ID=?
	and TYPE='test_ok'
"""

q2 = """
	update ST_MESSAGE
	set IS_LAST='n'
	where USER_ID=?
	and CLASS_ID=?
	and COURSE_ID=?
	and LESSON_ID=?
	and QUESTION_ID=?
"""

q3 = """
	INSERT INTO ST_MESSAGE (
		SESSION_ID,
		TICKET_NO,
		USER_ID,
		CLASS_ID,
		COURSE_ID,
		LESSON_ID,
		QUESTION_ID,
		TIME_SEQ,
		TIME_CLI,
		TIME_GRP,
		TIME_RUN,
		TIME_SEG,
		TYPE,
		RESULT,
		EVENT,
		CODE,
		MSG,
		MSG_ERROR,
		MSG_EXIT,
		KEYSTROKE,
		IS_LAST
	)
	VALUES (?,?,?,? ,?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, 'y')
"""

sql = new Sql(helper.connection)

try {
	// reset IS_LAST
//	sql.executeUpdate(q2, [
//		user_id,
//		class_id,
//		course_id,
//		lesson_id,
//		question_id
//	])
	
	//add message
	sql.executeInsert(q3, [
		session_id,
		ticket_no,
		
		user_id,
		class_id,
		course_id,
		lesson_id,
		question_id,
		
		time_seq,
		time_cli,
		time_grp,
		time_run,
		time_seg,
		
		type,
		result,
		event,
		code,
		
		msg,
		msg_error,
		msg_exit,
		keystroke
	])
	
	
//	// save [ST_REPORT]
//	if ('done'.equalsIgnoreCase(type)) {
//		reported = sql.firstRow("""
//			select count(*) as cc
//			from ST_REPORT
//			where USER_ID=?
//			and CLASS_ID=?
//			and COURSE_ID=?
//			and LESSON_ID=?
//			and QUESTION_ID=?
//		""", [
//			user_id,
//			class_id,
//			course_id,
//			lesson_id,
//			question_id
//		]).cc > 0
//	
//		//count time_passed
//		//count test_ok, test_error
//		test_ok = sql.firstRow("""
//			select count(*) as cc
//			from ST_MESSAGE
//			where USER_ID=?
//			and CLASS_ID=?
//			and COURSE_ID=?
//			and LESSON_ID=?
//			and QUESTION_ID=?
//			and TIME_RUN=?
//			and RESULT='test_ok'
//		""", [
//			user_id,
//			class_id,
//			course_id,
//			lesson_id,
//			question_id,
//			time_run
//		]).cc
//	
//		test_error = sql.firstRow("""
//			select count(*) as cc
//			from ST_MESSAGE
//			where USER_ID=?
//			and CLASS_ID=?
//			and COURSE_ID=?
//			and LESSON_ID=?
//			and QUESTION_ID=?
//			and TIME_RUN=?
//			and RESULT='test_error'
//		""", [
//			user_id,
//			class_id,
//			course_id,
//			lesson_id,
//			question_id,
//			time_run
//		]).cc
//		
//		data = [
//			'MSG':			msg,
//			'CODE':			code,
//			'TIME_UPDATED':	new Date().time,
//			'TIME_USED':	time_seg,
//			'TIME_RUN':		time_run,
//			'TEST_OK':		test_ok,
//			'TEST_ERROR':	test_error,
//			'REMOTE_IP': 	helper.remoteAddr
//		]
//		
//		if (test_ok > 0 && test_error == 0) {
//			data['TIME_PASSED'] = new Date().time
//		}
//
//		where = [
//			'USER_ID':		user_id,
//			'CLASS_ID':		class_id,
//			'COURSE_ID':	course_id,
//			'LESSON_ID':	lesson_id,
//			'QUESTION_ID':	question_id
//		]
//	
//		if (reported) {
//			// update
//			
//			l1 = []
//			l2 = []
//			l3 = []
//			
//			data.each {
//				k,v->
//				l1 << "${k}=?"
//				l2 << v
//			}
//			
//			where.each {
//				k,v->
//				l3 << "${k}=?"
//				l2 << v
//			}
//			
//			sqlss = 'update ST_REPORT set '+l1.join(', ')+' where '+l3.join(' and ')
//			
//			sql.executeUpdate(sqlss, l2)
//		}
//		else {
//			// insert
//			
//			l1 = []
//			l2 = []
//			l3 = []
//			
//			data.each {
//				k,v->
//				l1 << k
//				l2 << v
//				l3 << '?'
//			}
//			
//			where.each {
//				k,v->
//				l1 << k
//				l2 << v
//				l3 << '?'
//			}
//			
//			sqlss = 'insert into ST_REPORT ('+l1.join(', ')+') values('+l3.join(',')+')'
//			
//			sql.executeUpdate(sqlss, l2)
//		}
}
catch (e) {
	println e.message
}

sql.close()