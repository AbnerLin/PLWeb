import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

query_teacher = """
select *
from ST_USER
where TYPE='T'
order by USER_ID
"""

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - 教師管理')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
		script(type: 'text/javascript', src: 'class_admin.js', '')
	}
	body (class: 'page') {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}

		h1 ('教師管理')

		div {
			a (href: 'index.groovy', '返回系統管理選單')
			span (' | ')
			a (href: 'javascript:location.reload()', '重新整理')
			span (' | ')
			a (href: '#mailinglist', '電郵清單')
		}
		
		hr ()
		
		h3 ('將使用者升級為教師權限')

		form (action: 'teacher_admin_register.groovy', type: 'post') {
			table {
				tr {
					th (colspan: 2, '新增教師權限')
				}
				tr {
					td ('使用者代碼 / User ID')
					td {
						input (name: 'user_id')
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						button (type: 'submit', '確認')
					}
				}
			}
		}
		
		hr ()

		mailinglist = []
		
		h3 ('List of Teachers')
		table(width:"100%") {
			tr {
				th (width: 40, "#")
				th (width: 70, "User ID")
				th ("E-Mail")
				th ("姓名")
				th ("教職員編號")
				th ("學校 / 系所")
				th ("電話")
				th ('權限')
				th ('切換權限')
			}
			c = 0
			sql.eachRow(query_teacher) {
				row ->

				mailinglist << "\"${row.NAME}\" <${row.EMAIL}>"

				tr (class: c%2==0?'even':'odd') {
					td (align:'center', ++c)
					td (row.user_id)
					td (row.email)
					td (row.name)
					td (row.enrollment)
					td ("${row.SCHOOL} / ${row.DEPARTMENT}")
					td (row.telephone)
					td {
						switch (row.type) {
						case 'S':
							strong('學生')
							break;
						case 'T':
							strong('教師')
							break;
						case 'A':
							strong('作者')
							break;
						case 'M':
							strong('管理者')
							break;
						}
					}
					td {
					}
				}
			}
		}

		hr()

		h3 ('電子郵件清單')
		a (name: 'mailinglist')
		div {
			textarea (style: 'width:100%;height:250px;', mailinglist.join(', '))
		}
	}
}

sql.close()
