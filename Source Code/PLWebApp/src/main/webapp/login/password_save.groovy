import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat
import net.tanesha.recaptcha.ReCaptchaImpl
import net.tanesha.recaptcha.ReCaptchaResponse

import javax.mail.*
import java.util.Properties
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

helper = new CommonHelper(request, response, session)

def random_password(length) {
	charset = "0123456789abcdefghijklmnopqrstuvwxyz"
	rand = new Random(System.currentTimeMillis())
	sb = new StringBuffer()
	for (i in 1..length) {
		pos = rand.nextInt(charset.length())
		sb.append(charset.charAt(pos))
	}
	return sb.toString()
}

_QUERY_EMAIL_EXISTS = """
select count(*) as cc
from ST_USER
where EMAIL=?
"""

_QUERY_USER_BY_EMAIL = """
select USER_ID, NAME
from ST_USER
where EMAIL=?
"""

_QUERY_ADD_RECOVERY = """
insert into ST_USER_RECOVERY (USER_ID, PASSWORD, CREATED, IS_ALIVE)
values (?, md5(?), ?, 'y')
"""

sql = new Sql(helper.connection)

email 		= helper.fetch('email')
challenge 	= helper.fetch('recaptcha_challenge_field')
uresponse 	= helper.fetch('recaptcha_response_field')

remoteAddr 	= request.remoteAddr
reCaptcha 	= new ReCaptchaImpl()
reCaptcha.privateKey = '6LfVocMSAAAAAHvhuNUHuPIZYRgEhygpG3pnpVOk'
reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse)

errorno = 0
errormsg = []

if (email == null || "".equals(email)) {
	errorno ++
	errormsg << "必須填寫電子郵件"
}
else {
	hasEmail = false
	try {
		hasEmail = (sql.firstRow(_QUERY_EMAIL_EXISTS, [email]).cc > 0)
	}
	catch (e) {
		errorno ++
		errormsg << e.message
	}
	
	if (!hasEmail) {
		errorno ++
		errormsg << "電子郵件信箱錯誤"
	}
}

if (!reCaptchaResponse.isValid()) {
	errorno ++
	errormsg << "驗證碼錯誤，請重新填寫一次"
}

if (errorno > 0) {
	sb = new StringBuilder()
	sb.append '<ul>'
	errormsg.each {
		msg ->
		sb.append "<li>${msg}</li>"
	}
	sb.append '</ul>'
	
	helper.attr 'password_errormsg', sb.toString()
	helper.forward 'index.groovy?m=password'
}
else {
	try {
		row = sql.firstRow(_QUERY_USER_BY_EMAIL, [email])
		
		if (row != null && row.USER_ID != null) {
	
			uid			= row.USER_ID
			name		= row.NAME
			ipaddr		= helper.remoteAddr
			datetime	= helper.stdDatetime
			server_url	= helper.serverPort.equals(80)?helper.serverName:"${helper.serverName}:${helper.serverPort}"
			password	= random_password(6)
			
			recovery_url = "http://${server_url}" + response.encodeURL("/login/password_reset.groovy?u=${uid}&p=${password}")
			
			subject = "重要通知!PLWeb密碼重新設定${name}"
			content =
"""<html>
<head>
<title>${subject}</title>
</head>
<body>
Dear ${name},<br/>
<br/>
您已經提出密碼重新設定申請，帳號是${email}。<br/>
<br/>
如果您從未提出密碼申請，可能是遭到不明人士冒充您的身分，<br/>
請不需要為此擔心，忽略本通知信即可，新密碼將自動在24小時內失效，<br/>
您仍舊可以使用舊密碼登入。<br/>
<br/>
新密碼：<br/>
${password}<br/>
請於登入成功後盡速重新設定您的密碼，並請牢記。<br/>
<br/>
申請者來源為 ${ipaddr} 時間是 ${datetime}<br/>
您的帳號(電子郵件信箱)為 ${email}<br/>
<br/>
感謝您使用本系統！<br/>
<a href="http://plweb.org/">PLWeb</a>開發團隊祝您學習愉快。
</body>
</html>
"""
/*
			new AntBuilder().mail(
				mailhost:	helper.adminMailHost,
				mailport:	helper.adminMailPort,
				ssl:		helper.adminSSL,
				user:		helper.adminSMTPUser,
				password:	helper.adminSMTPPassword,
				from:		helper.adminMailFrom,
				toList:		email,
				subject:	subject,
				message:	content,
				messagemimetype: 'text/html'
			)
*/

			Properties props = new Properties();
			props.setProperty("mail.smtp.host", helper.adminMailHost);
			props.setProperty("mail.smtp.port", helper.adminMailPort);
			props.setProperty("mail.debug","true");
			props.setProperty("mail.smtp.auth","true");
			props.setProperty("mail.smtp.starttls.enable","true");

			Session _session = Session.getDefaultInstance(props,null)

			MimeMessage message = new MimeMessage(_session);
			message.setSubject(subject.toString());
			message.setFrom(new InternetAddress(helper.adminSMTPUser));
			message.setContent(content.toString(), "text/html;charset=UTF-8");

			InternetAddress toAddress = new InternetAddress(email);
			message.addRecipient(Message.RecipientType.TO, toAddress);

			Transport transport = _session.getTransport("smtps");
			transport.connect(helper.adminMailHost, helper.adminSMTPUser, helper.adminSMTPPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			
			sql.executeInsert(_QUERY_ADD_RECOVERY, [uid, password, new Date().time])
		}
	}
	catch (e) {
		//println e.message
	}
	
	helper.redirect 'index.groovy?m=password_ok'
}

sql.close()