import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.text.DecimalFormat
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

_SQL_QUERY_USERS = """
select a.*, b.USER_TYPE
from ST_USER a
right outer join ST_USER_REGISTER b on b.USER_ID=a.USER_ID
order by a.SIGNUP_DATE desc
limit 100
"""

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Users Administration')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
	}
	body (class: 'page') {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h1 ('最新註冊使用者列表(僅顯示100筆)')

		div {
			a (href: 'index.groovy', '返回系統管理選單')
			span ('|')
			a (href: 'javascript:location.reload()', '重新整理')
		}
		
		hr ()
		
		h2 ('使用者清單')
		form (action: 'latest_signup_users_action.groovy', method: 'post') {
		div (style: 'text-align:right') {
			button (name: 'upgrade', '設定為教師')
		}
		table (width: '100%', style: '') {
			tr () {
				th (width: 40, "#")
				th (width: 70, 'User ID')
				th ('E-Mail')
				th ('姓名')
				th ('學號 / 教職員編號')
				th ('學校 / 系所')
				th ('電話')
				th ('註冊日期')
				th ('權限')
				th ('註冊身份')
				th (width: 40, '選取')
			}
			c = 0
			sql.eachRow(_SQL_QUERY_USERS) {
				row ->
				tr (class: c%2==0?'even':'odd') {
					td (align:'center', ++c)
					td (row.user_id)
					td (row.email)
					td (row.name)
					td (row.enrollment)
					td ("${row.SCHOOL} / ${row.DEPARTMENT}")
					td (row.telephone)
					td (row.SIGNUP_DATE!=null?new Date(row.SIGNUP_DATE.toLong()).format('yyyy-MM-dd HH:mm:ss'):'未設定')
					td {
						switch (row.type) {
						case 'S':
							strong('學生')
							break;
						case 'T':
							span (style: 'color:blue;font-weight:bold;', '教師')
							break;
						case 'M':
							strong('管理者')
							break;
						}
					}
					td {
						switch (row.USER_TYPE) {
							case 'S':
							span ('學生')
							break;
							case 'T':
							span (style: 'color:blue;font-weight:bold;', '教師')
							break;
							case 'A':
							span ('作者')
							break;
						}
					}
					td (align: 'center'){
						input (type: 'checkbox', name: 'check_id_list[]', value: row.USER_ID)
					}
				}
			}
		}
		}
	}
}

sql.close()
