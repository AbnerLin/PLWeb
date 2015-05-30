import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

email = helper.fetch('email')
password = helper.fetch('password')

row = sql.firstRow("""
	select USER_ID, EMAIL, NAME, TYPE, IS_ADMIN
	from ST_USER
	where EMAIL=?
	and (PASSWORD=? or PASSWORD=md5(?))
""", [email, password, password])

isLogin = false

if (row) {
	//set login status
	
	helper.sess_new()
	helper.sess 'uid',		String.valueOf(row.USER_ID)
	helper.sess 'uemail',	row.EMAIL
	helper.sess 'uname',	row.NAME
	helper.sess 'utype',	row.TYPE
	helper.sess 'is_admin',	'y'.equalsIgnoreCase(row.IS_ADMIN)
	
	helper.sess 'login_url', '/form.login/login.groovy'
	
	isLogin = true
	
	sql.executeUpdate("""
		update ST_USER
		set IS_LOGIN='y',
		LAST_UPDATE=?,
		LAST_IP=?,
		LAST_HOST=?,
		LAST_SESSION=?,
		LAST_AGENT=?
		where USER_ID=?
	""", [
		new Date().time,
		helper.remoteAddr,
		helper.remoteHost,
		helper.session.id, //helper.sess_id(),
		helper.request.getHeader('User-Agent'), //helper.getHeader('User-Agent'),
		row.USER_ID
	])
	
	//LOGIN_LOG
	sql.executeUpdate("""
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
	""", [
		new Date().time,
		row.USER_ID,
		row.NAME,
		row.EMAIL,
		'y',
		helper.request.getHeader('User-Agent'), //helper.getHeader('User-Agent'),		
		helper.remoteAddr,
		helper.remoteHost,
		helper.serverName,
		helper.serverPort,
		helper.session.id //helper.sess_id()
	])
}
else {
	//LOGIN_LOG
	sql.executeUpdate("""
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
	""", [
		new Date().time,
		0,
		"",
		email,
		'n',
		helper.request.getHeader('User-Agent'), //helper.getHeader('User-Agent'),		
		helper.remoteAddr,
		helper.remoteHost,
		helper.serverName,
		helper.serverPort,
		""
	])
}

sql.close()

if (isLogin) {
	// clear login error message
	helper.sess		'login_error', null
	helper.redirect	'../form.dashboard/dashboard.groovy'
}
else {
	// write login error message
	helper.sess		'login_email', email
	helper.sess		'login_error', """Login failed. (IP: ${helper.remoteAddr}, ${helper.datetimeString})"""
	helper.redirect	'login.groovy'
}
