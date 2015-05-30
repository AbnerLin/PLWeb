import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import org.json.simple.JSONObject

def helper = new CommonHelper(request, response, session)
def sql = new Sql(helper.connection)

def server_host = request.serverName
def server_port = request.serverPort


helper.sess_new()
session_id = helper.session.id
ticket_no   = new Date().time
ticket_date = new Date().time

def course_id = request.getParameter('course_id')
def class_id  = request.getParameter('class_id')
def lesson_id = request.getParameter('lesson_id')
def uid = request.getParameter('user_id')

try {
	
	data = [
		'SESSION_ID':	session_id,
		'TICKET_NO':	ticket_no,
		'TICKET_DATE':	ticket_date,
		'USER_ID':	uid,
		'COURSE_ID':	course_id,
		'CLASS_ID':	class_id,
		'LESSON_ID':	lesson_id,
		'LESSON_MODE':	"student",
		'REMOTE_AGENT':	helper.getHeader('User-Agent'),
		'REMOTE_IP':	helper.remoteAddr,
		'REMOTE_HOST':	helper.remoteHost,
		'SERVER_HOST':	helper.serverName,
		'SERVER_PORT':	helper.serverPort
	]

	// write WS_TICKET
	// insert
	helper.simpleSqlInsert 'WS_TICKET', data, sql
}
catch (e) {
	println e
}

course_name = sql.firstRow("""
	select COURSE_NAME
	from COURSE
	where COURSE_ID=?
""", [course_id]).course_name

def lesson_xml  = "http://${server_host}:${server_port}/ServerLesson.groovy?s=${session_id}&t=${ticket_no}"

def request_url = "\"http://${server_host}:${server_port}/ServerRequest.groovy?s=${session_id}&t=${ticket_no}\""

def lessonpath = "\"${uid}/${class_id}/${course_name}/${lesson_id}\""

def lessonxml = "\"${uid}/${class_id}/${course_name}/lesson${lesson_id}.xml\""


JSONObject obj = new JSONObject()
obj.put("xmlLink", lesson_xml.toString())
obj.put("requestLink", request_url)
obj.put("lessonPath", lessonpath)
obj.put("lessonxml", lessonxml)

print obj.toString()




sql.close()



