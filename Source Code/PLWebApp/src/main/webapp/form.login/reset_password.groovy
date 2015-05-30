import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

u = helper.fetch('u')
p = helper.fetch('p')

if (!u || !p) {
	helper.redirect 'login.groovy'
}

password  = helper.fetch('password')
password2 = helper.fetch('password2')

if (password && password2) {
	errorno  = 0
	errormsg = []
	
	if (password != password2) {
		errorno++
		errormsg << 'Enter the same password twice.'
	}
	else if (password =~ /^[0-9]+$/) {
		errorno++
		errormsg << 'Password too simple.'
	}
	else if (password.trim().size() < 5) {
		errorno++
		errormsg << 'Password too short.'
	}
	
	if (errorno > 0) {
		helper.sess('login_error', "重設密碼失敗，請再試一次。(原因為: ${errormsg})")
	}
	else {
		sql = new Sql(helper.connection)
		
		query1 = 'update ST_USER set PASSWORD=md5(?) where USER_ID=? and RECOVERY_PASSWORD=md5(?)'
		
		sql.executeUpdate(query1, [password, u, p])

		sql.close()
		
		helper.sess('login_error', "您的密碼已重設，請使用新密碼登入！")
	}
	helper.redirect('login.groovy')
	
	return
}

helper.attr('helper', helper)

helper.forward('reset_password.gsp')
