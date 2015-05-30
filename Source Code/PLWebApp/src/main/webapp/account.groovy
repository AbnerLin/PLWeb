import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

if (!session) {
	response.sendError 403
	return;
}

uid = session.get('uid')
uemail = session.get('uemail')

def sql = new Sql(helper.connection)

query1 = """
select *
from ST_USER
where USER_ID=?
"""

query2 = """
select *
from ST_CLASS c
inner join USER_CLASS uc
	on c.CLASS_ID=uc.CLASS_ID
where uc.USER_ID=?
"""

row = sql.firstRow(query1, [uid])

name		= row.name
enrollment	= row.enrollment
telephone	= row.telephone
type		= row.type
school		= row.school
department	= row.department
person_id	= row.person_id

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - My Account')
		script (type: 'text/javascript', src: 'jquery/jquery-1.3.2.min.js', '') 
		script (type: 'text/javascript', src: 'jquery/jquery-ui-1.7.1.custom.min.js', '')
		script (type: 'text/javascript', src: 'account.js', '')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
		link(rel: 'stylesheet', type: 'text/css', href: 'jquery/css/smoothness/jquery-ui-1.7.1.custom.css')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h2("正在檢視 [${name}] 個人帳號")
		
		div (id: 'tabs') {
			ul {
				li {
					a (href: '#tabs-1', '基本資料設定')
				}
				li {
					a (href: '#tabs-2', '修課清單')
				}
				li {
					a (href: '#tabs-3', '服務清單')
				}
			}
			div (id: 'tabs-1') {
				h3('個人資料 / Profile')
				
				form (action: 'account_save.groovy', method: 'post') {
					table (width: '560') {
						tr {
							th (colspan: 2, '使用者 / User')
						}
						tr {
							td (width: '150', '編號 / ID#: ')
							td (uid)
						}
						tr {
							td ('帳號 / E-Mail: ')
							td (uemail)
						}
						tr {
							td ('姓名 / Name: ')
							td {
								input (name:"name", value: name)
							}
						}
						tr {
							td ('學號 / Enrollment ID:')
							td {
								input (name:"enrollment", value: enrollment)
							}
						}
						tr {
							td ('電話 / Phone:')
							td {
								input (name:"telephone", value: telephone)
							}
						}
						tr {
							td ('學校 / School:')
							td {
								input (name: 'school', value: school)
							}
						}
						tr {
							td ('系所 / Department:')
							td {
								input (name: 'department', value: department)
							}
						}
						tr {
							td ('身分證字號 / Person ID:')
							td {
								input (name: 'person_id', value: person_id)
							}
						}
						tr {
							td ('權限角色 / Role Type:')
							switch (type) {
							case 'T':
								td('教師 / Teacher')
								break;
							case 'S':
								td('學生 / Student')
								break;
							case 'A':
								td('作者 / Author')
								break;
							case 'M':
								td('系統管理者 / Administrator')
								break;
							default:
								td('其他 / Other')
								break;
							}
						}
						tr {
							td (colspan:2, align: 'right') {
								input (type: "submit", value: "儲存變更")
							}
						}
					}
				}
				hr()
				h3 ('修改密碼 / Reset Password')
				form (action: 'account_password.groovy', method: 'post') {
					table (width: 560) {
						tr {
							th (colspan: 2, '密碼 / Password')
						}
						tr{
							td('輸入新密碼 / New Password: ')
							td {
								input(type: 'password', name: 'password')
							}
						}
						tr {
							td('再次確認密碼 / Confirm Password: ')
							td {
								input(type: 'password', name: 'password2')
							}
						}
						tr {
							td (colspan:2, align:'right') {
								button(type:'submit', '更新密碼')
							}
						}
					}
				}
			}
			div (id: 'tabs-2') {
				h3 ('加選 / Add')
				form (action:'account_signon.groovy', method:'post') {
					table {
						tr {
							th (colspan: 2, '加選設定')
						}
						tr {
							td('課程代碼 / Class ID: ')
							td {
								input(name: 'class_id', size: 30)
							}
						}
						tr {
							td (colspan:2, align: 'right') {
								button(type: 'submit', '確認加選')
							}
						}
					}
				}
				
				h3("已修課程 / List")
				
				table (width: '100%') {
					tr {
						th (width: 30, '#')
						th (class: 'small', 'Class ID')
						th (class: 'small', 'Class Name')
						th (class: 'small', 'School')
						th (class: 'small', 'Department')
						th (class: 'small', 'Year')
						th (class: 'small', 'Semester')
						th (class: 'small', 'Role')
						th (class: 'small', width: 50, 'Drop')
					}
					c = 0
					sql.eachRow(query2, [uid]) {
						row_class ->
						tr (class: c%2==0?'odd':'even') {
							td (align: 'center', ++c)
							td (class: 'small', row_class.class_id)
							td (row_class.class_name)
							td (row_class.school)
							td (row_class.department)
							td (row_class.years)
							td {
								switch (row_class.semester) {
			  					case '1':
			  						span('spring')
			  						break;
			  					case '2':
			  						span('summer')
			  						break;
			  					case '3':
			  						span('fall')
			  						break;
			  					case '4':
			  						span('winter')
			  						break;
								}
							}
							td (row_class.is_teacher=='y'?'Teacher':'Student')
							td {
								if (row_class.is_teacher!='y') {
									href_signoff = "account_signoff.groovy?class_id=${row_class.class_id}"
									
									a (href: href_signoff, onclick: "return confirm('此動作無法回復，請再次確認是否退選???');", '退選')
								}
							}
						}
					}
				}
			}
			div (id: 'tabs-3') {
				h3 ('線上學習履歷 / e-Portfolio')
				table (width: '100%') {
					tr {
						th ('功能', width: 250)
						th ('內容')
					}
					tr{
						td ('網址')
						td {
							url = "http://"+request.getServerName()+":"+request.getServerPort()+"/portfolio.groovy?user="+uid;
							a (href: url, url)
						}
					}
					tr {
						td ('說明')
						td ('此網址提供您在 PLWeb 的學習記錄，您可以將網頁另存新檔或列印永久保存。')
					}
				}
			}
		}		
	}
}

sql.close()