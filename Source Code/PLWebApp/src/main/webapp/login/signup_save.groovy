import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat
import net.tanesha.recaptcha.ReCaptchaImpl
import net.tanesha.recaptcha.ReCaptchaResponse

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

//取得表單資料
email		= helper.fetch('email')
email2		= helper.fetch('email2')
password	= helper.fetch('password')
password2	= helper.fetch('password2')
name		= helper.fetch('name')
enrollment	= helper.fetch('enrollment')
classid		= helper.fetch('classid')
phone		= helper.fetch('phone')
school		= helper.fetch('school')
department	= helper.fetch('department')
checkonly   = helper.fetch('checkonly')
roletype	= helper.fetch('roletype')

//驗證碼表單特殊欄位
challenge 	= helper.fetch('recaptcha_challenge_field')
uresponse 	= helper.fetch('recaptcha_response_field')

//時間記錄
nowtime		= new Date().time

//將checkonly(僅檢查資料正確性)轉換為boolean值
if (checkonly != null && ('1'.equals(checkonly) || 'true'.equals(checkonly))) {
    checkonly = true
}
else {
    checkonly = false
}

_QUERY_EMAIL_EXISTS = """
select count(*) as cc
from ST_USER
where EMAIL=?
"""

_QUERY_SAVE_USER = """
insert into ST_USER (EMAIL, NAME, PASSWORD, ENROLLMENT, TELEPHONE, SCHOOL, DEPARTMENT, SIGNUP_DATE, TYPE, AGREE)
values (?, ?, md5(?), ?, ?, ?, ?, ?, 'S', 'n')
"""

_QUERY_CLASS_EXISTS = """
select count(*) as cc
from ST_CLASS
where CLASS_ID=?
"""

_QUERY_USER_ASSOC_CLASS = """
insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
values (?, ?, 'n')
"""

_QUERY_USER_BY_EMAIL = """
select USER_ID
from ST_USER
where EMAIL=?
"""

_QUERY_USER_REGISTER = """
insert into ST_USER_REGISTER (USER_ID, USER_TYPE, CREATED)
values (?, ?, ?)
"""

uid = 0
errorno  = 0
errormsg = []

try {
	remoteAddr 	= request.remoteAddr
	reCaptcha 	= new ReCaptchaImpl()
	reCaptcha.privateKey = '6LfVocMSAAAAAHvhuNUHuPIZYRgEhygpG3pnpVOk'
	reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse)
	
	if (!reCaptchaResponse.isValid()) {
		errorno ++
		errormsg << "驗證碼錯誤，請重新填寫一次"
	}
	
    //檢查E-Mail是否已經存在
    isEmailExists = false
    if (email != null && !("".equals(email))) {
        isEmailExists = (sql.firstRow(_QUERY_EMAIL_EXISTS, [email]).cc > 0)
    }
    
    //檢查E-Mail
	if (email == null || "".equals(email)) {
		errorno++
		errormsg << '必須填寫電子郵件！'
	}
	else if (isEmailExists) {
		errorno++
		errormsg << '輸入的電子郵件已被註冊！'
	}
	else if (!email.equals(email2)) {
		errorno++
		errormsg << '兩次輸入的電子郵件不一致！'
	}
	else if (!(email =~ /^[a-zA-Z0-9\-\_\.]+@[a-zA-Z0-9\-\_\.]+$/)) {
		errorno++
		errormsg << '電子郵件信箱格式不正確！'
	}
	
	//檢查密碼
	if (password == null || "".equals(password)) {
		errorno++
		errormsg << '必須填寫密碼！'
	}
	else if (!password.equals(password2)) {
		errorno++
		errormsg << '兩次輸入的密碼不一致！'
	}
	else if (password =~ /^[0-9]+$/) {
		errorno++
		errormsg << '密碼過於簡單(請用英數字混合組成的密碼)！'
	}
	else if (password.trim().size() < 5) {
		errorno++
		errormsg << '密碼字數過少(請輸入5個字數以上的密碼)！'
	}
	
	//檢查其他欄位
	if (!name) {
		errorno++
		errormsg << '必須填寫姓名！'
	}
	if (!enrollment) {
		errorno++
		errormsg << '必須填寫學號！'
	}
	
	//沒有錯誤、不是僅檢查模式，就將資料寫入
	if (errorno == 0 && !checkonly) {
	    
	    //儲存使用者資料
		sql.executeInsert(_QUERY_SAVE_USER, [
			email,
			name,
			password,
			enrollment,
			phone,
			school,
			department,
			nowtime
		])
		
		//取得新使用者的流水代號
		uid = sql.firstRow(_QUERY_USER_BY_EMAIL, [email]).USER_ID
		
		//儲存使用者及班級關聯資料
		if (classid != null && !("".equals(classid))) {
		    
		    //檢查班級是否存在
		    hasClass = (sql.firstRow(_QUERY_CLASS_EXISTS, [classid]).cc > 0)
		    
		    //只有課程代號存在才建立關聯
		    if (uid != null && hasClass) {
                //建立關聯
                sql.executeInsert(_QUERY_USER_ASSOC_CLASS, [uid, classid])
			}
		}
		
		//身分別記錄
		sql.executeInsert(_QUERY_USER_REGISTER, [uid, roletype, nowtime])
	}
}
catch (e) {
	errorno ++
	errormsg << e.message
}

sql.close()

if (checkonly) {
}
else {
    if (errorno > 0) {
        //資料驗證不通過，顯示錯誤內容
        sb = new StringBuilder()
        sb.append '<ul>'
        errormsg.each {
            msg->
            sb.append "<li>${msg}</li>"
        }
        sb.append '</ul>'
        
        helper.attr 'signup_errormsg', sb.toString()
        helper.forward 'index.groovy?m=signup'
    }
    else {
        helper.redirect response.encodeURL("/login/index.groovy?m=signup_ok&id=${uid}")
    }
}
