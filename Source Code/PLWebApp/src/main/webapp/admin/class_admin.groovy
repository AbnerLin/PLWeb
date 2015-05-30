import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

alive = request.getParameter('alive')
if (!alive) {
	alive = 'y'
}

query_class = """
select ST_CLASS.*,
	(select count(*) from USER_CLASS where USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID and USER_CLASS.IS_TEACHER='n') as USER_COUNT
from ST_CLASS
where ST_CLASS.ALIVE=?
order by CLASS_ID desc
"""

query_teacher = """
select ST_USER.NAME
from USER_CLASS
inner join ST_USER
on USER_CLASS.USER_ID=ST_USER.USER_ID
where USER_CLASS.CLASS_ID=?
and USER_CLASS.IS_TEACHER='y'
order by ST_USER.NAME
"""

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.doubleQuotes = true
html.html {
	head {
		title('PLWeb - 班級管理')
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

		h1('課程管理')

		div {
			a (href: 'index.groovy', '返回系統管理選單')
			span (' | ')
			a (href: 'javascript:location.reload()', '重新整理')
		}

		hr ()
		
		href_alive = 'class_admin.groovy?alive=y'
		href_nonalive = 'class_admin.groovy?alive=n'
		
		h3 ('已建立課程清單')
		
		a (href: href_alive) {
			span ('可以被使用的課程')
		}
		
		span (' | ')
		
		a (href: href_nonalive) {
			span ('已經隱藏的課程')
		}
		
		span ('(隱藏機制是讓被誤刪的課程還有救回的機會)')
		
		table(width: '100%') {
			tr {
				th (width:30, '#')
				th ('課程代碼')
				th ('課程名稱')
				th (class: 'small', '教師')
				th (class: 'small', '人數')
				th (class: 'small', '學校')
				th (class: 'small', '系所')
				th (class: 'small', '年度')
				th (class: 'small', '學期別')
				th (class: 'small', '隱藏？')
				th (class: 'small', width: 40, '修改')
				th (class: 'small', width: 40, '刪除')
			}
			c = 0
			sql.eachRow(query_class, [alive]) {
				row ->
				href_remove = "class_admin_remove.groovy?id=${row.class_id}"
				href_edit   = "class_admin_edit.groovy?id=${row.class_id}"
				tr (class: c%2==0?'odd':'even') {
					td (align:'center', ++c)
					td (style: 'font-family: Georgia', row.class_id)
					td (row.class_name)
					td {
						sql.eachRow(query_teacher, [row.class_id]) {
							row2->
							div (row2.name)
						}
					}
					td (row.user_count)
					td (row.school)
					td (row.department)
					td (row.years)
					td {
						switch (row.semester) {
						case '1':
							span('下學期')
							break;
						case '2':
							span('夏季班')
							break;
						case '3':
							span('上學期')
							break;
						case '4':
							span('冬季班')
							break;
						case '5':
							span ('測驗專用')
							break;
						}
					}
					td ('y'.equals(row.alive)?'可見':'隱藏')
					td (align: 'center') {
						a(href: href_edit) {
								img (src: '../icons/group_edit.png', border: 0)
						}
					}
					td (align: 'center') {
						a (href: href_remove, onclick: "return confirm('請確認是否要刪除？？？');") {
							img (src: '../icons/group_delete.png', border: 0)
						}
					}
				}
			}
		}
	}
}

sql.close()
