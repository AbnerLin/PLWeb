import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

field = helper.fetch('field')
value1 = helper.fetch('value1')
value2 = helper.fetch('value2')

switch (field) {
	/**
	 * Check E-Mail
	 */
	case 'email':
	
	sql = new Sql(helper.connection)
	query1 = 'select NAME from ST_USER where EMAIL=?'
	row = sql.firstRow(query1, [value1])
	isRegistered = row?true:false;
	sql.close()
	
	if (!value1 || !value2) {
		print "<span><font color=\"red\">Required.</font></span>"
	}
	else if (isRegistered) {
		print "<span><font color=\"red\">${value1} already registered.</font></span>"
	}
	else if (value1 != value2) {
		print "<span><font color=\"red\">Enter the same E-Mail address twice.</font></span>"
	}
	else if (! (value1 =~ /^[a-zA-Z0-9\.]+@[a-zA-Z0-9\-\.]+$/) ) {
		print "<span><font color=\"red\">Not valid E-Mail address.</font></span>"
	}
	else {
		print "<span><font color=\"blue\">Correct.</font></span>"
	}

	return;

	/**
	 * Check Password
	 */
	case 'password':
	
	if (!value1 || !value2) {
		print "<span><font color=\"red\">Required.</font></span>"
	}
	else if (value1 != value2) {
		print "<span><font color=\"red\">Enter the same password twice.</font></span>"
	}
	else if (value1.trim() =~ /^[0-9]+$/) {
		print "<span><font color=\"red\">Password too simple.</font></span>"
	}
	else if (value1.trim().size() < 5) {
		print "<span><font color=\"red\">Password too short.</font></span>"
	}
	else {
		print "<span><font color=\"blue\">Correct.</font></span>"
	}
	
	return;
	
	
	/**
	 * Check Name
	 */
	case 'name':
	   
	if ( ! value1 ) {
		print "<span><font color=\"red\">Required.</font></span>"
	}
	else {
		print "<span><font color=\"blue\">Correct.</font></span>"
	}
	
	return;

	
	/**
	 * Check Name
	 */
	case 'enrollment':
	   
	if ( ! value1 ) {
		print "<span><font color=\"red\">Required.</font></span>"
	}
	else {
		print "<span><font color=\"blue\">Correct.</font></span>"
	}
	
	return;
}