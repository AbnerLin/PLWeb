import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

email = helper.fetch('email')

charset = "0123456789abcdefghijklmnopqrstuvwxyz"

def random_password(length) {
    rand = new Random(System.currentTimeMillis())
    sb = new StringBuffer()
    for (i in 1..length) {
        pos = rand.nextInt(charset.length())
        sb.append(charset.charAt(pos))
    }
    return sb.toString()
}

if (email) {
	sql = new Sql(helper.connection)
	
	query1 = 'select USER_ID, NAME from ST_USER where EMAIL=?'
	
	query2 = 'update ST_USER set RECOVERY_PASSWORD=md5(?), RECOVERY_UPDATE=? where USER_ID=?'
	
	row = sql.firstRow(query1, [email])
	
	if (row && row.USER_ID) {

		uid = row.USER_ID
		
		name = row.NAME
		
		ip_addr = helper.remoteAddr
		
		datetime = helper.stdDatetime
		
		server_url = helper.serverPort.equals(80)?helper.serverName:"${helper.serverName}:${helper.serverPort}"
		
		password_recovery = random_password(6)
		
		recovery_url = "http://${server_url}/form.login/reset_password.groovy?u=${uid}&p=${password_recovery}"
		
		subject = "Important! Password Recovery for ${name}"
		
		content = """<html>
<head>
<title>${subject}</title>
</head>
<body>
Dear ${name},<br/>
<br/>
Someone has requested that the password for your account,<br/>
${email}, be reset.</br>
<br/>
If you did not make this request, please simply disregard this<br/>
e-mail; it is sent only to the address on file for your account,<br/>
and will become invalid after 24 hours, so you do not have to<br/>
worry about your account being taken over.<br/>
<br/>
To choose a new password, please go to the following URL:<br/>
<br/>
<a href="${recovery_url}">${recovery_url}</a><br/>
<br/>
This request originated from ${ip_addr} at ${datetime}<br/>
Your login account is ${email}<br/>
<br/>
Sincerely,<br/>
The <a href="http://plweb.org/">PLWeb</a> Team
</body>
</html>
"""
		helper.admin_sendmail(email, subject, content)
		
		helper.attr('mail_send_to', email)
		
		if (uid) {
			sql.executeUpdate(query2, [password_recovery, new Date().time, uid])
		}
	}
		
	sql.close()
}


helper.attr('helper', helper)

helper.include('forget_password.gsp')