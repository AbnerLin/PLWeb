import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

// session data
uid		= helper.sess('uid')
utype	= helper.sess('utype')

if (uid == null) {
	helper.redirect response.encodeUrl('/')
	return
}

// get/post data
//email = helper.fetch('email')

// sql queries
_SQL_USER_ROW_ = """
	select *
	from ST_USER
	where USER_ID=?
"""

_SQL_INSERT_CONTACT_ = """
	insert into USER_CONTACT
	(CONTACT_TYPE, CONTACT_USER_ID, CONTACT_DATE, CONTACT_SEND, EMAIL, EMAIL2, PHONE, PHONE2, NAME, SCHOOL, DEPARTMENT, MESSAGE)
	values ('tqcplus_java', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

_SQL_UPDATE_CONTACT_ = """
	update USER_CONTACT
	set CONTACT_DATE=?, CONTACT_SEND=?, EMAIL=?, EMAIL2=?, PHONE=?, PHONE2=?, NAME=?, SCHOOL=?, DEPARTMENT=?, MESSAGE=?
	where CONTACT_USER_ID=?
	and CONTACT_TYPE='tqcplus_java'
"""

_SQL_HAS_CONTACT_ = """
	select count(1) as cc
	from USER_CONTACT
	where CONTACT_USER_ID=?
	and CONTACT_TYPE='tqcplus_java'
"""

_SQL_CONTACT_ROW_ = """
	select *
	from USER_CONTACT
	where CONTACT_USER_ID=?
	and CONTACT_TYPE='tqcplus_java'
"""

sql = new Sql(helper.connection)

has_contact = sql.firstRow(_SQL_HAS_CONTACT_, [uid]).cc > 0

submit = helper.fetch('submit')

if (submit != null) {
	email = helper.fetch('email')
	email2 = helper.fetch('email2')
	phone = helper.fetch('phone')
	phone2 = helper.fetch('phone2')
	name = helper.fetch('name')
	school = helper.fetch('school')
	department = helper.fetch('department')
	message = helper.fetch('message')

	subject = "PLWeb通知：教師授權申請聯繫"
	content =
"""<html>
<head>
<title>${subject}</title>
</head>
<body>
<p>${message}</p>
<p>主要信箱 ${email}<p>
<p>次要信箱 ${email2}<p>
<p>主要電話 ${phone}<p>
<p>次要電話 ${phone2}<p>
<p>教師姓名 ${name}<p>
<p>學校 ${school}<p>
<p>系所 ${department}<p>
<p>${new Date().format('yyyy-MM-dd HH:mm:ss')}</p>
</body>
</html>
"""

	new AntBuilder().mail(
		mailhost:	helper.adminMailHost,
		mailport:	helper.adminMailPort,
		ssl:		helper.adminSSL,
		user:		helper.adminSMTPUser,
		password:	helper.adminSMTPPassword,
		from:		helper.adminMailFrom,
		toList:		'tungsh@yuntech.edu.tw',
		ccList:		'tungsh@yuntech.edu.tw',
		subject:	subject,
		message:	content,
		messagemimetype: 'text/html'
	)
			
	if (!has_contact) {
		sql.execute(_SQL_INSERT_CONTACT_, [uid, new Date().time, 'y', email, email2, phone, phone2, name, school, department, message])
	}
	else {
		sql.execute(_SQL_UPDATE_CONTACT_, [new Date().time, 'y', email, email2, phone, phone2, name, school, department, message, uid])
	}

	has_contact = true
}

contact_date = new Date()

if (!has_contact) {
	user_row = sql.firstRow(_SQL_USER_ROW_, [uid])
	name = user_row.NAME
	school = user_row.SCHOOL
	department = user_row.DEPARTMENT
	email = user_row.EMAIL
	email2 = ''
	phone = user_row.TELEPHONE
	phone2 = ''
	message = '我想瞭解如何在課堂中使用PLWeb教學，請與我聯絡。'
}
else {
	row = sql.firstRow(_SQL_CONTACT_ROW_, [uid])
	name = row.NAME
	school = row.SCHOOL
	department = row.DEPARTMENT
	email = row.EMAIL
	email2 = row.EMAIL2
	phone = row.PHONE
	phone2 = row.PHONE2
	message = row.MESSAGE
	contact_date = new Date(Long.valueOf(row.CONTACT_DATE))
}

sql.close()

html.setDoubleQuotes(true)
html.html {
	head {
		title ('PLWeb - 取得 TQC+ Java 6 與 PLWeb 授權')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css')
	}
	body {
		if (has_contact) {
			p (style: 'font-size:15px;color:blue', "您已經在${contact_date.format('yyyy/MM/dd')}日送出聯繫單，請等待業務處理人員與您聯繫！如果您有需要修改聯繫內容，請直接在以下表單修改後再次確認送出！謝謝！")
		}

		form (action: 'tqcplus_java.groovy', method: 'post', class: '') {
			table (width: 650) {
				tr {
					th (colspan: 2, '教師聯繫專用表單')
				}
				tr {
					th (width: 150, class: 'verticle', '主要信箱')
					td {
						input (name: 'email', value: email, class: 'big_text max_width')
					}
				}
				tr {
					th (class: 'verticle', '次要信箱')
					td {
						input (name: 'email2', value: email2, class: 'big_text max_width')
					}
				}
				tr {
					th (class: 'verticle', '電話')
					td {
						input (name: 'phone', value: phone, class: 'big_text max_width')
					}
				}
				tr {
					th (class: 'verticle', '辦公室電話')
					td {
						input (name: 'phone2', value: phone2, class: 'big_text max_width')
					}
				}
				tr {
					th (class: 'verticle', '教師姓名')
					td {
						input (name: 'name', value: name, class: 'big_text', size: 30)
					}
				}
				tr {
					th (class: 'verticle', '授課學校')
					td {
						input (name: 'school', value: school, class: 'big_text', size: 30)
					}
				}
				tr {
					th (class: 'verticle', '系所')
					td {
						input (name: 'department', value: department, class: 'big_text', size: 30)
					}
				}
				tr {
					th (class: 'verticle', '我要留言')
					td {
						textarea (name: 'message', cols: 40, rows: 10, class: 'big_text', message)
					}
				}
				tr {
					td (colspan: 2, align: 'center') {
						input (type: 'submit', name: 'submit', value: '確認送出', class: 'big_text')
						//a (href: 'index.groovy', '取消')
					}
				}
			}
		}
	}
}
