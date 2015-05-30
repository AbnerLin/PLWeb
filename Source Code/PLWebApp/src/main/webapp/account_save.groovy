import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

if (!session) {
	response.sendError 403
	return;
}

def uid = session.get('uid')
def sql = new Sql(helper.connection)

query1 = """
update ST_USER
set NAME=?,
	ENROLLMENT=?,
	TELEPHONE=?,
	SCHOOL=?,
	DEPARTMENT=?,
	PERSON_ID=?
where USER_ID=?
"""

/*
 * 表單資料儲存
 */
name		= helper.fetch('name')
enrollment	= helper.fetch('enrollment')
telephone	= helper.fetch('telephone')
school		= helper.fetch('school')
department	= helper.fetch('department')
person_id	= helper.fetch('person_id')

try {
	sql.execute(query1, [name, enrollment, telephone, school, department, person_id, uid])
	session.setAttribute('alert_message', 'Updated!!!');
}
catch (e) {
	session.setAttribute('error_message', e.message);
}

sql.close()

response.sendRedirect('account.groovy')