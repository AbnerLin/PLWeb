import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	return
}

sql = new Sql(helper.connection)

query1 = """
	update ST_USER
	set IS_LOGIN='n'
	where USER_ID=?
"""

sql.executeUpdate(query1, [uid])

sql.close()

helper.sess('uid',		null)
helper.sess('uemail',	null)
helper.sess('uname',	null)
helper.sess('utype',	null)

print("location.reload();");
