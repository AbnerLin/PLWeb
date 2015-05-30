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

query1 = """
select count(*) as cc
from ST_USER
where EMAIL=?
"""

query2 = """
insert into ST_USER (EMAIL,NAME,PASSWORD,ENROLLMENT,TELEPHONE,SIGNUP_DATE,TYPE,AGREE)
values (?, ?, md5(?), ?, ?, ?, 'S', 'n')
"""

query3 = """
insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
values (?, ?, 'n')
"""

query4 = """
select USER_ID
from ST_USER
where EMAIL=?
"""

errorno  = 0
errormsg = []

try {
	
	if (!email) {
		errorno++
		errormsg << 'E-Mail field is required.'
	}
	else if (sql.firstRow(query1, [email]).cc > 0) {
		errorno++
		errormsg << 'E-Mail has been registered.'
	}
	else if (email != email2) {
		errorno++
		errormsg << 'Enter the same E-Mail address twice.'
	}
	else if (! (email =~ /^[a-zA-Z0-9\.]+@[a-zA-Z0-9\-\.]+$/) ) {
		errorno++
		errormsg << 'Not valid E-Mail address.'
	}
	
	if (!password) {
		errorno++
		errormsg << 'Password field is required.'
	}
	else if (password != password2) {
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
	
	if (!name) {
		errorno++
		errormsg << 'Name field is required.'
	}
	
	if (!enrollment) {
		errorno++
		errormsg << 'Enrollment ID field is required.'
	}
	

	// Checked, All correct, then save to database
	if (errorno == 0) {
		sql.executeInsert(query2, [
			email,
			name,
			password,
			enrollment,
			phone,
			new Date().time
		])
		
		
		// Link user to class if CLASS ID filed is not empty
		if (classid) {
			user_id = sql.firstRow(query4, [email]).user_id
			sql.executeInsert(query3, [user_id, classid])
		}
	}
}
catch (e) {
	errorno ++
	errormsg << e.message
}

sql.close()

if (errorno > 0) {
	
	// registeration not successed.
	
	helper.sess('signup_errmsg', errormsg)
	helper.forward('signup.groovy')
}
else {
	helper.sess('signup_email', email)
	helper.redirect_keep('signup_ok.groovy')
}
