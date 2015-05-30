import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)
//def sql = new Sql(request.attribute('dbconn'))

// 取得表單參數
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

if (row) {
	helper.sess_new()
	helper.sess 'uid',		String.valueOf(row.USER_ID)
	helper.sess 'uemail',	row.EMAIL
	helper.sess 'uname',	row.NAME
	helper.sess 'utype',	row.TYPE
	helper.sess 'is_admin',	'y'.equalsIgnoreCase(row.IS_ADMIN)
	helper.sess 'department', row.DEPARTMENT
	helper.sess 'enrollment', row.ENROLLMENT
	
	helper.sess 'login_url', response.encodeUrl('/login')
	helper.sess 'logout_url', response.encodeUrl('/login/logout.groovy')
	
	isLogin = true
	
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
}
else {
	//LOGIN_LOG
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
}

if (isLogin) {
	helper.sess		'login_error', null
	helper.redirect	response.encodeUrl('/dashboard/index.groovy')
}
else {
	helper.sess		'login_email', email
	helper.sess		'login_error', "登入失敗！ (已記錄 ${helper.remoteAddr}, ${helper.datetimeString})"
	helper.redirect	response.encodeUrl('/login/index.groovy')
}
