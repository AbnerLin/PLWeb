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
	isLogin = true
	
	//set login status	
	helper.sess_new()
	helper.sess 'uid',		String.valueOf(row.USER_ID)
	helper.sess 'uemail',	row.EMAIL
	helper.sess 'uname',	row.NAME
	helper.sess 'utype',	row.TYPE
	helper.sess 'is_admin',	row.IS_ADMIN
	
	helper.sess 'login_url', '/form.login/login.groovy'
	
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
	print("WebFace.Invoke('system', 'login', 'AfterLogin');");
}
else {
	// write login error message
	print("WebFace.Alert('<p>無法通過登入驗證，請檢查您輸入的資料。<p><p style=\"color: red\">已記錄IP位址：${helper.remoteAddr}</p>', '登入失敗');");
}
