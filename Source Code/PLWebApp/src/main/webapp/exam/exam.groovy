import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

//def uid = session.get('uid')
//def utype = session.get('utype')

def uid = ''
def utype = ''
def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
	select ST_CLASS.*, USER_CLASS.IS_TEACHER
	from ST_CLASS
	inner join USER_CLASS
	on USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID
	where ST_CLASS.ALIVE='y'
	and USER_CLASS.USER_ID=?
	order by ST_CLASS.CLASS_ID
"""

query2 = """
	select CLASS_COURSE.*, COURSE.COURSE_TITLE
	from CLASS_COURSE
	inner join COURSE
		on CLASS_COURSE.COURSE_ID=COURSE.COURSE_ID
	where CLASS_COURSE.CLASS_ID=? AND DUEDATE > ? AND BEGINDATE < ?
	order by CLASS_COURSE.SEQNUM
"""

email = helper.fetch('email')
password = helper.fetch('password')
classid = helper.fetch('classid')
classpw = helper.fetch('classpw')

// 訊息處理

html.html {
	head {
		title("PLWeb - 線上測驗專用網址")
		link (rel: 'stylesheet', type: 'text/css', href: '../css/reset.css', media:'all')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/default.css', media:'all')
		script(type: 'text/javascript', src: 'class_center.js', '')
	}
	body (class: 'page') {
		h1 ('測驗模式')
		
		h2 ('考生基本資料')

		form(action: 'exam.groovy', method: 'post') {
			table (width: 360) {
				tr {
					th (colspan: 2, '基本資料')
				}
				tr {
					th (class: 'verticle', width: 150) {
						span ('電子郵件(帳號)：')
					}
					td {
						input (name: 'email', value: email, style: 'width:100%')
					}
				}
				tr {
					th (class: 'verticle') {
						span ('登入密碼：')
					}
					td {
						input (type: 'password', name:'password', value: password, autocomplete: 'off', style: 'width:100%')
					}
				}
			}
			hr()
			h2 ('驗證測驗資料')
			p('以下欄位於測驗前公佈，如有疑問，請向監考人員詢問')
			p {
				span ('測驗編號')
				input (name: 'classid', value: classid)
				span ('（共 10 位數字）')
			}
			p {
				span('測驗密碼')
				input(type: 'password', name:'classpw', value: classpw)
			}
			p (style:'color:red', "IP 位址 ${helper.remoteAddr} 已經紀錄，測驗過程所有操作將由系統側錄，如發現舞弊之情事，將依試場規定處置。")
			input(type:'submit',value:'驗證資料')
		}

		if (email && password) {
			hr ()

			p ("您輸入的帳號：${email}")

			user = sql.firstRow('select USER_ID, EMAIL, NAME, TYPE, IS_ADMIN from ST_USER where EMAIL=? and (PASSWORD=? or PASSWORD=md5(?))', [email, password, password])
			if (user) {
				p (style:'color:blue', "「${user.NAME}」已通過驗證")

				if (classid) {
					classid = classid.trim()
					cls = sql.firstRow('select CLASS_NAME from ST_CLASS where CLASS_ID=? and PASSWORD=?', [classid, classpw])
					if (cls) {
						p ("測驗名稱 ${cls.CLASS_NAME}")

						h2 ('測驗列表')
						
						p ("請從以下列表選擇一項測驗，並按「啟動測驗」按鈕，就可以開始作答！")
						
						ucls = sql.firstRow('select count(*) as cc from USER_CLASS where USER_ID=? and CLASS_ID=?', [user.USER_ID, classid]).cc
						if (ucls <= 0) {
							sql.executeInsert('insert into USER_CLASS(USER_ID, CLASS_ID, IS_TEACHER) values(?, ?, \'n\')', [user.USER_ID, classid])
						}
						Date _now = new Date()
						sql.eachRow(query2, [classid, _now.getTime(), _now.getTime()]) {
							row->
							p {
								editor_url = helper.make_url('webstart.groovy', [mode: 'student', course_id: row.COURSE_ID, lesson_id: row.LESSON_ID, class_id: classid, isExam: true], true)
								span("${row.TITLE} (${row.COURSE_TITLE})")
								a (href: editor_url, style: 'font-size:1.25em;font-weight:bold', "啟動測驗")
							}
						}

						p (style: 'color:red', "請注意：測驗視窗執行後，非測驗用途的程式將會強制關閉！")
					}
					else {
						p (style: 'color:red', '查無此測驗，請重新輸入測驗編號及密碼。')
					}
				}
			}
			else {
				p (style: 'color:red', '帳號或密碼驗證失敗，請重新輸入。')
			}
		}
	}
}

sql.close()
