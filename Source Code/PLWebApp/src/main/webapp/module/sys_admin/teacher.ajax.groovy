import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

action = helper.fetch('action')

sql = new Sql(helper.connection)

switch (action) {
	case 'new_teacher':
		email = helper.fetch('email')
		result = sql.executeUpdate("""
			update ST_USER
			set TYPE='T'
			where EMAIL=?
		""", [email])
		print "WebFace.Alert('已經新增${result}筆教師權限', '操作已經完成');"
	break;
	
	case 'remove_teacher':
		user_id = helper.fetch('user_id')
		result = sql.executeUpdate("""
			update ST_USER
			set TYPE='S'
			where USER_ID=?
		""", [user_id])
		print "WebFace.Alert('已經移除${result}筆教師權限', '操作已經完成');"
	break;
}

sql.close()

print "WebFace.Invoke('sys_admin', 'teacher', 'PageReload');"
