import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid   = session.get('uid')
def uname = session.get('uname')

def class_id  = request.getParameter('class_id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

class_row = sql.firstRow('select * from ST_CLASS where CLASS_ID=?', [class_id])

query1 = """
select U.USER_ID, U.NAME, U.ENROLLMENT, U.EMAIL
from ST_USER U
inner join USER_CLASS UC
on U.USER_ID=UC.USER_ID
where CLASS_ID=?
and UC.IS_TEACHER='n'
order by U.ENROLLMENT
"""

html.setDoubleQuotes(true)
html.html {
	head {
		title('查看狀態')
		script(type: 'text/javascript', src: 'view_lamp.js', '')
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {
		a (href: 'index.groovy', '返回課程管理')
		span (' | ')
		a (href: 'javascript:location.reload()', '重新整理')

		h2("您正在查詢 ${class_id} 課程資訊")
		
		table (width: 500) {
  			tr {
  				th (colspan: 2, '課程資訊')
  			}
  			tr {
  				th (width: 200, class: 'verticle', '課程代碼')
  				td (class_row.class_id)
  			}
  			tr {
  				th (class: 'verticle', '課程名稱')
  				td (class_row.class_name)
  			}
  			tr {
  				th (class: 'verticle', '授課學校名稱')
  				td (class_row.school)
  			}
  			tr {
  				th (class: 'verticle', '授課系所名稱')
  				td (class_row.department)
  			}
  			tr {
  				th (class: 'verticle', '開課年度(西元年)')
  				td (class_row.years)
  			}
  			tr {
  				th (class: 'verticle', '學期別')
  				td {
  					switch (class_row.semester) {
  					case '1':
  						span ('下學期')
  						break;
  					case '2':
  						span ('夏季班')
  						break;
  					case '3':
  						span ('上學期')
  						break;
  					case '4':
  						span ('冬季班')
  						break;
					case '5':
						span ('測驗專用')
						break;
  					}
  				}
  			}
		}

  		h3 ('修課學生清單')
  		
  		table (width: '100%') {
			tr {
				th (width: 40, '移除')
				th (width: 100, '學號')
				th (width: 100, '學生姓名')
				th (width: 100, '使用者代號')
				th ('電子郵件信箱')
				
				/*
				th (width:60, '#total')
				th (width:60, '#tested')
				th (width:60, '#test_ok')
				*/
			}

			c = 0

			sql.eachRow(query1, [class_id]) {
				row ->
			
				c++

				href_drop = "class_drop.groovy?class_id=${class_id}&user_id=${row.user_id}"
				tr (class: c%2==0?'even':'odd') {
					td (align: 'center'){
						a (href: href_drop, onclick: "return confirm('Are you sure?');") {
							img (src: '../icons/delete.png', border: 0)
						}
					}
					td (row.enrollment)
					td (row.name)
					td (row.user_id)
					td (row.email)
				}
			}
		}
	}
}
sql.close()
