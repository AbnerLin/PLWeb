import org.plweb.suite.common.xml.XmlFactory
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

s = helper.fetch('s')	// session_id
t = helper.fetch('t')	// ticket_no

action = helper.fetch('action')		// action

sql = new Sql(helper.connection)

row = sql.firstRow("""
	select  WS_TICKET.USER_ID,
		WS_TICKET.CLASS_ID,
		WS_TICKET.COURSE_ID,
		WS_TICKET.LESSON_ID,
		WS_TICKET.LESSON_MODE,
		COURSE.COURSE_NAME
	from WS_TICKET
	inner join COURSE
		ON WS_TICKET.COURSE_ID=COURSE.COURSE_ID
	where SESSION_ID=?
	and TICKET_NO=?
""", [s, t])

user_id		= row.user_id
class_id	= row.class_id
course_id	= row.course_id
lesson_id	= row.lesson_id
mode		= row.lesson_mode

helper.attr 'is_legal',		true
helper.attr 'user_id',		user_id
helper.attr 'class_id',		class_id
helper.attr 'course_id',	course_id
helper.attr 'lesson_id',	lesson_id
helper.attr 'mode',			mode

sql.close()

/**
 * action dispatch
 */
if ('save_report_v3'.equalsIgnoreCase(action)) {
	helper.forward 'service.request/save_report_v3.groovy'
	return
}
else if ('save_message_v2'.equalsIgnoreCase(action)) {
	helper.forward 'service.request/save_message_v2.groovy'
	return
}
else if ('save_report_v2'.equalsIgnoreCase(action)) {
	helper.forward 'service.request/save_report_v2.groovy'
	return
}
else if ('save_snapshot_v2'.equalsIgnoreCase(action)) {
	helper.forward 'service.request/save_snapshot_v2.groovy'
	return
}

sql = new Sql(helper.connection)

switch (action) {
	case 'resetTask':
		task_id = request.getParameter('question_id')
		
		selectReportIsPassed = 'SELECT COUNT(*) as cc FROM ST_REPORTS WHERE USER_ID=? AND CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND QUESTION_ID=? AND TEST_OK=1 AND CURRENT=1'
		row = sql.firstRow(selectReportIsPassed, [user_id, class_id, course_id, lesson_id, task_id]);
		
		if (row.cc > 0) {
			updateTask = 'UPDATE ST_REPORTS SET CURRENT=-1 WHERE CURRENT=1 AND USER_ID=? AND CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND QUESTION_ID=?'
			
			try {
				sql.executeUpdate(updateTask, [user_id, class_id, course_id, lesson_id, task_id])
			} catch(e) {
				println e.message
			}
			
		}
		

	
		break;
	case 'updateConsoleText':
		task_id  = request.getParameter('taskId')
		consoleText = request.getParameter('consoleText')
		
		updateConsoleTextSql = 'UPDATE ST_REPORTS SET MESSAGE=? WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND QUESTION_ID=? AND USER_ID=?'
		
		try {
			sql.executeUpdate(updateConsoleTextSql, [consoleText, class_id, course_id, lesson_id, task_id, user_id])
		} catch(e) {
			println e.message
		}
		
		break;
//	case 'saveKeyStroke':
//		task_id = request.getParameter('task_id')
//		records = new String(request.getParameter('records').getBytes('ISO8859-1'), 'UTF-8')
//
//		try {
//			row = sql.firstRow("select count(*) as cc from ES_MESSAGE where CLASS_ID=? and USER_ID=? and COURSE_ID=? and LESSON_ID=? and QUESTION_ID=?", [class_id, user_id, course_id, lesson_id, task_id]);
//			if (row.cc>0) {
//				update
//				sql.executeUpdate("update ES_MESSAGE set TIME_SEQ=?, ES=CONCAT(ES, ?) where CLASS_ID=? and USER_ID=? and COURSE_ID=? and LESSON_ID=? and QUESTION_ID=?", [System.currentTimeMillis(), records, class_id, user_id, course_id, lesson_id, task_id])
//			}
//			else {
//				insert
//				sql.executeUpdate("insert into ES_MESSAGE (TIME_SEQ, ES, COURSE_ID, QUESTION_ID, USER_ID, LESSON_ID, CLASS_ID) values (?, ?, ?, ?, ?, ?, ?)", [System.currentTimeMillis(), records, course_id, task_id, user_id, lesson_id, class_id])
//			}
//		}
//		catch (e) {
//			println e.message
//		}
//		break;
	case 'saveMessage':
		task_id  = request.getParameter('task_id')
		type     = request.getParameter('type')
		time_seg = request.getParameter('time_seg')
		test_num = request.getParameter('test_num')
		test_ok  = request.getParameter('test_ok')
		mesg = new String(request.getParameter('mesg').getBytes('ISO8859-1'), 'UTF-8')
		code = new String(request.getParameter('code').getBytes('ISO8859-1'), 'UTF-8')
		es   = new String(request.getParameter('es').getBytes('ISO8859-1'), 'UTF-8')
		time_seq = new Date().time
		
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
where CLASS_ID=?
and USER_ID=?
and COURSE_ID=?
and LESSON_ID=?
and QUESTION_ID=?
"""

		q3 = """
INSERT INTO ST_MESSAGE
	(QUESTION_ID, USER_ID, TYPE, TIME_SEQ, MSG, CODE, LESSON_ID, COURSE_ID, CLASS_ID, TIME_SEG, ES, TEST_NUM, TEST_OK, IS_LAST)
VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,'y')
"""

		q4 = """
delete from ST_CODE
where CLASS_ID=?
and USER_ID=?
and COURSE_ID=?
and LESSON_ID=?
and QUESTION_ID=?
"""

		q5 = """
insert into ST_CODE
(COURSE_ID, CLASS_ID, LESSON_ID, QUESTION_ID, USER_ID, TIME_SEQ, TIME_USED, CODE, TEST_NUM, TEST_OK, MSG, SCORE, ES, TYPE)
select COURSE_ID, CLASS_ID, LESSON_ID, QUESTION_ID, USER_ID, TIME_SEQ,
	(select sum(TIME_SEG)
		from ST_MESSAGE b
		where b.CLASS_ID=a.CLASS_ID
		and b.COURSE_ID=a.COURSE_ID
		and b.LESSON_ID=a.LESSON_ID
		and b.QUESTION_ID=a.QUESTION_ID
		and b.USER_ID=a.USER_ID) as TIME_USED,
	CODE, TEST_NUM, TEST_OK, MSG, SCORE, ES, TYPE
from ST_MESSAGE a
where IS_LAST='y'
and CLASS_ID=?
and USER_ID=?
and COURSE_ID=?
and LESSON_ID=?
and QUESTION_ID=?
"""

		try {
			cc = sql.firstRow(q1, [class_id, user_id, course_id, lesson_id, task_id]).cc
			if (cc==0) {
				sql.executeUpdate(q2, [class_id, user_id, course_id, lesson_id, task_id])
				
				//add message
				sql.executeInsert(q3, [task_id, user_id, type, time_seq, mesg, code, lesson_id, course_id, class_id, time_seg, es, test_num, test_ok])
				
				//update ST_CODE
				sql.execute(q4, [class_id, user_id, course_id, lesson_id, task_id])
				sql.executeInsert(q5, [class_id, user_id, course_id, lesson_id, task_id])
			}
			print 'happy'
		}
		catch (e) {
			e.printStackTrace();
		}
		break;
//	case 'saveCode':
//		task_id = request.getParameter('task_id')
//		code = new String(request.getParameter('code').getBytes('ISO8859-1'), 'UTF-8')
//		time = request.getParameter('time')
//		
//		try {
//			row = sql.firstRow("select TIME_USED as time_used from ST_CODE where CLASS_ID=? and USER_ID=? and COURSE_ID=? and LESSON_ID=? and QUESTION_ID=?", [class_id, user_id, course_id, lesson_id, task_id])
//
//			if (row) {
//				update
//				add_time = row.time_used.toLong() + time.toLong()
//				sql.executeUpdate("update ST_CODE set TIME_SEQ=?, CODE=?, TIME_USED=? where CLASS_ID=? and USER_ID=? and COURSE_ID=? and LESSON_ID=? and QUESTION_ID=?", [System.currentTimeMillis(), code, add_time, class_id, user_id, course_id, lesson_id, task_id])
//			}
//			else {
//				insert
//				add_time = time.toLong()
//				sql.executeUpdate("insert into ST_CODE (COURSE_ID, CLASS_ID, LESSON_ID, QUESTION_ID, USER_ID, TIME_SEQ, CODE, TIME_USED) values (?, ?, ?, ?, ?, ?, ?, ?)", [course_id, class_id, lesson_id, task_id, user_id, System.currentTimeMillis(), code, add_time])
//			}
//		}
//		catch (e) {
//			println e.message
//		}
//		break;
	case 'uploadFile':
		path = request.getParameter('path')
		content = request.getParameter('content')
		
		q1 = """
select count(*) as cc
from USER_UPLOAD
where USER_ID=?
and CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
and PATH=?
"""

		q2 = """
insert into USER_UPLOAD
	(USER_ID, CLASS_ID, COURSE_ID, LESSON_ID, PATH, CONTENT)
values (?, ?, ?, ?, ?, ?)
"""

		q3 = """
update USER_UPLOAD
set CONTENT=?
where USER_ID=?
and CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
and PATH=?
"""

		try {
			cc = sql.firstRow(q1, [user_id, class_id, course_id, lesson_id, path]).cc
			
			if (cc == 0) {
				// INSERT
				sql.executeInsert(q2, [user_id, class_id, course_id, lesson_id, path, content])
			}
			else {
				// UPDATE
				sql.executeUpdate(q3, [content, user_id, class_id, course_id, lesson_id, path])
			}
		}
		catch (e) {
			println e.message
		}

		break;
	case 'saveProject':
		//content = new String(request.getParameter('content').getBytes('ISO8859-1'), 'UTF-8')
		content = request.getParameter('content')
		
		ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes('UTF-8'))
		project = XmlFactory.readProject(is);
		//project.setId(lesson_id.toString())
		
		StringWriter writer = new StringWriter()
		XmlFactory.saveProject(project, writer)
		
		title     = project.title
		text_xml  = writer.toString()
		text_size = text_xml.getBytes('UTF-8').length
		tasknum   = project.tasks.size()
		updated   = new Date().time

		q1 = """
update COURSE_FILE
set TITLE=?,
	TEXT_XML=?,
	TEXT_SIZE=?,
	TASKNUM=?,
	UPDATED=?
where COURSE_ID=?
and LESSON_ID=?
"""

		try {
			sql.executeUpdate(q1, [title, text_xml, text_size, tasknum, updated, course_id, lesson_id])
		}
		catch (e) {
			println e.message
		}

		println "(server response) Saved!!!"
		break;
	
	case 'saveGradeSetting':
		//course_id;
		//lesson_id;
		gradeSetting = request.getParameter('gradeSetting')
		
		q1 = """ SELECT COUNT(*) as cc FROM GRADE_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """
		q2 = """ INSERT INTO GRADE_SETTING(COURSE_ID, LESSON_ID, GRADE_SET) VALUES(?, ?, ?)"""
		q3 = """ UPDATE GRADE_SETTING SET GRADE_SET=?, TOTAL_SET=null WHERE COURSE_ID=? AND LESSON_ID=?"""
		
		try {
			cc = sql.firstRow(q1, [course_id, lesson_id]).cc
			if(cc == 0) {
				sql.executeInsert(q2, [course_id, lesson_id, gradeSetting])
			} else {
				sql.executeUpdate(q3, [gradeSetting, course_id, lesson_id])
			}
		} catch(e) {
			println e.message
		}
		
		break;
	case 'saveMasterySet':
		// course_id;
		// lesson_id;
		masterySet = request.getParameter('masterySet')
		
		q1 = """ SELECT COUNT(*) as cc FROM MASTERY_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """
		q2 = """ INSERT INTO MASTERY_SETTING(COURSE_ID, LESSON_ID, MASTERY_SETTING) VALUES(?, ?, ?) """
		q3 = """ UPDATE MASTERY_SETTING SET MASTERY_SETTING=? WHERE COURSE_ID=? AND LESSON_ID=? """
		q4 = """ DELETE FROM ST_MASTERY WHERE COURSE_ID=? AND LESSON_ID=? """
		
		try {
			cc = sql.firstRow(q1, [course_id, lesson_id]).cc
			
			if(cc == 0) {
				sql.executeInsert(q2, [course_id, lesson_id, masterySet])
			} else {
				sql.executeUpdate(q3, [masterySet, course_id, lesson_id])
			}
			
		} catch(e) {
			println e.message
		}
		
		 sql.execute(q4, [course_id, lesson_id])
		
		
		break;
}

sql.close()
