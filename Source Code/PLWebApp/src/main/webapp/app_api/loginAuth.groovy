import org.plweb.webapp.helper.CommonHelper
import groovy.sql.Sql
import org.json.simple.JSONValue
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.json.simple.JSONArray

def helper = new CommonHelper(request, response, session)
def sql = new Sql(helper.connection)

email = helper.fetch('email')
password = helper.fetch('password')

def __QUERY_AUTH = '''
	select USER_ID, EMAIL, NAME, TYPE, IS_ADMIN, DEPARTMENT, ENROLLMENT
	from ST_USER
	where EMAIL=?
	and (
		PASSWORD=? or
		PASSWORD=md5(?) or
		exists (select ID from ST_USER_RECOVERY where ST_USER_RECOVERY.USER_ID=ST_USER.USER_ID and ST_USER_RECOVERY.PASSWORD=md5(?))
	)
'''

def __QUERY_UPDATE = '''
	update ST_USER
	set IS_LOGIN='y',
	LAST_UPDATE=?,
	LAST_IP=?,
	LAST_HOST=?,
	LAST_SESSION=?,
	LAST_AGENT=?
	where USER_ID=?
'''

def __QUERY_LOGGING = '''
		insert into LOGIN_LOG (
		LOGIN_DATE,
		LOGIN_UID,
		LOGIN_NAME,
		LOGIN_EMAIL,
		LOGIN_AUTH,
		REMOTE_AGENT,
		REMOTE_IP,
		REMOTE_HOST,
		SERVER_HOST,
		SERVER_PORT,
		SERVER_SESSION
	) values (
		?,?,?,?, ?,?,?,?, ?,?,?
	)
'''

def isLogin = false

def row = sql.firstRow(__QUERY_AUTH, [email, password, password, password])

if(row) {
	isLogin = true
	helper.sess_new()


	sql.executeUpdate(__QUERY_UPDATE, [
		new Date().time,
		helper.remoteAddr,
		helper.remoteHost,
		helper.session.id,
		helper.request.getHeader('User-Agent'),
		row.USER_ID
	])
	
	//LOGIN_LOG
	sql.executeUpdate(__QUERY_LOGGING, [
		new Date().time,
		row.USER_ID,
		row.NAME,
		row.EMAIL,
		'y',
		helper.request.getHeader('User-Agent'),
		helper.remoteAddr,
		helper.remoteHost,
		helper.serverName,
		helper.serverPort,
		helper.session.id
	])


//	print CH_encoding(row.DEPARTMENT)
//	print row.ENROLLMENT
//	print String.valueOf(row.USER_ID)
//	print CH_encoding(row.NAME) + "<br>"

	def classSetQuery = """ SELECT USER_CLASS.CLASS_ID, ST_CLASS.CLASS_NAME FROM ST_CLASS, USER_CLASS WHERE ST_CLASS.ALIVE='y' AND USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID AND USER_CLASS.USER_ID=? AND USER_CLASS.CLASS_ID NOT LIKE '____5_____' ORDER BY USER_CLASS.CLASS_ID DESC """
	def courseSetQuery = """ SELECT COURSE_ID, LESSON_ID, TITLE FROM CLASS_COURSE WHERE CLASS_ID=? ORDER BY SEQNUM """	


	def classSetRows = sql.rows(classSetQuery, [row.USER_ID])
	def JSONObject _return = new JSONObject();
	_return.put("Department", CH_encoding(row.DEPARTMENT))
	_return.put("Enrollment", row.ENROLLMENT)
	_return.put("UserId", String.valueOf(row.USER_ID))
	_return.put("Uname", CH_encoding(row.NAME))
		
	JSONArray classSet = new JSONArray();
	JSONObject classData = new JSONObject();
	JSONArray courseSet = new JSONArray();
	JSONObject lessonData = new JSONObject();

	classSetRows.each {
		_classData ->
//			print _classData.CLASS_ID + " " + _classData.CLASS_NAME
//			print "<br>"
			
			classData.put("class_id", _classData.CLASS_ID)
			classData.put("name", CH_encoding(_classData.CLASS_NAME))

			def courseSetRows = sql.rows(courseSetQuery, [_classData.CLASS_ID])

			courseSetRows.each {
				_courseData -> 
//					print "_" + _courseData.COURSE_ID + " "  + _courseData.LESSON_ID + " " + _courseData.TITLE + "<br>"
					lessonData.put("course_id", _courseData.COURSE_ID)
					lessonData.put("lesson_id", _courseData.LESSON_ID)
					lessonData.put("name", CH_encoding(_courseData.TITLE))
					
					courseSet.add(new JSONObject(lessonData))
					lessonData.clear()
			}
			
			classData.put("course", (JSONArray) courseSet.clone())
			classSet.add(new JSONObject(classData))
			
			classData.clear()
			courseSet.clear()
			lessonData.clear()	
			
	}
	_return.put("class", classSet)

	



	print _return.toJSONString()
} else {
	sql.executeUpdate(__QUERY_LOGGING, [
		new Date().time,
		0,
		"",
		email,
		'n',
		helper.request.getHeader('User-Agent'),
		helper.remoteAddr,
		helper.remoteHost,
		helper.serverName,
		helper.serverPort,
		""
	])
	print "login failed."
}

sql.close()



def CH_encoding(str){
        String _encoding;
        if(str != null)
                _encoding = str.bytes.encodeBase64().toString();
        else {
                String _str = "null";
                _encoding = _str.bytes.encodeBase64().toString();
        }
        return _encoding;
}
