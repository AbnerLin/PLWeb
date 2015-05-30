import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

email		= helper.fetch('email')
email2		= helper.fetch('email2')
password	= helper.fetch('password')
password2	= helper.fetch('password2')
name		= helper.fetch('name')
enrollment	= helper.fetch('enrollment')
classid		= helper.fetch('classid')
phone		= helper.fetch('phone')

errorno  = 0
errormsg = []

try {
	
	if (!email) {
		errorno++
		errormsg << '電子郵件信箱必須填寫'
	}
	else if (sql.firstRow("select count(*) as cc from ST_USER where EMAIL=?", [email]).cc > 0) {
		errorno++
		errormsg << '填寫的電子郵件信箱已被註冊'
	}
	else if (email != email2) {
		errorno++
		errormsg << '兩次輸入的電子郵件信箱不相同'
	}
	else if (! (email =~ /^[a-zA-Z0-9\.\-]+@[a-zA-Z0-9\-\.]+$/) ) {
		errorno++
		errormsg << '輸入的電子郵件信箱不合法'
	}
	
	if (!password) {
		errorno++
		errormsg << '密碼必須設定'
	}
	else if (password != password2) {
		errorno++
		errormsg << '兩次輸入的密碼不相同'
	}
	else if (password =~ /^[0-9]+$/) {
		errorno++
		errormsg << '純數字密碼不安全'
	}
	else if (password.trim().size() < 5) {
		errorno++
		errormsg << '密碼字數過短'
	}
	
	if (!name) {
		errorno++
		errormsg << '姓名必須填寫'
	}
	
	if (!enrollment) {
		errorno++
		errormsg << '學號或編號必須填寫'
	}
}
catch (e) {
	errorno ++
	errormsg << e.message
}


if (errorno > 0) {
	
	// registeration not successed.
	error_display = '請先修正以下問題<ul>'
	errormsg.each {
		msg->
		error_display += "<li>${msg}</li>"
	}
	error_display += '</ul>';
	print("WebFace.Alert('${error_display}', '無法建立新帳號');");
}
else {
	// Checked, All correct, then save to database
	sql.executeInsert("""
		insert into ST_USER (EMAIL,NAME,PASSWORD,ENROLLMENT,TELEPHONE,SIGNUP_DATE,TYPE,AGREE)
		values (?, ?, md5(?), ?, ?, ?, 'S', 'n')""", [
		email,
		name,
		password,
		enrollment,
		phone,
		new Date().time
	])
	
	// Link user to class if CLASS ID filed is not empty
	if (classid) {
		user_id = sql.firstRow("""
			select USER_ID
			from ST_USER
			where EMAIL=?
		""", [email]).user_id
		
		sql.executeInsert("""
			insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
			values (?, ?, 'n')
		""", [user_id, classid])
	}
	
	print("WebFace.Alert('歡迎 ${name}！<br/><br/>您可以開始使用新帳號登入系統', '新帳號註冊成功');");
	print("WebFace.TabClose('registration', 'signup');");
	print("WebFace.TabOpen('歡迎登入系統', 'system', 'login');");
}

sql.close()
