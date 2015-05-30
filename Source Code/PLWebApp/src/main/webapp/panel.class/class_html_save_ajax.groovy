/**
 * Ajax content saving for class html editor.
 * Required valid-user authed by session.
 * 
 * @author Yan-hong Lin (lyhcode at gmail.com)
 * 
 * @session uid		:integer			for user authentication
 * 
 * @param class_id	:integer			WHERE-key
 * @param field		:enum[html_text]
 * @param value		:string
 * 
 * @return :json {
 *   result		:boolean	true or false
 *   message	:string		html formatted text message
 * }
 * 
 */

import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

uid		= helper.sess('uid')
utype	= helper.sess('utype')

if (!uid) {
	println 'no access'
	return
}

class_id = helper.fetch('class_id')
field = helper.fetch('field')
value = helper.fetch('value')

if (field.equals('html_text')) {
	sql = new Sql(helper.connection)
	
	//check user right to access class settings
	allowed = sql.firstRow("""
		select count(1) as CC from USER_CLASS
		where USER_ID =?
		and CLASS_ID = ?
		and IS_TEACHER = 'y'
	""", [uid, class_id]).CC > 0
	
	if (allowed) {
		result = sql.executeUpdate("""
			update ST_CLASS
			set HTML_TEXT = ?
			where CLASS_ID = ?
		""", [value, class_id])
		
		println value
		println result
	}
	
	sql.close()
}
