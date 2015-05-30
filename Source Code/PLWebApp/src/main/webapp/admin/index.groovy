import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid		= session.get('uid')
def uname	= session.get('uname')
def utype	= session.get('utype')

if (!helper.sess('is_admin')) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

html.doubleQuotes = true
html.html {
	head {
		meta ('http-equiv': "Content-Type", content: "text/html; charset=UTF-8")
		title ('系統管理 - PLWeb')
		script (type: "text/javascript", '')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/reset.css', media: 'all')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/default.css', media: 'all')
		link (rel: 'stylesheet', type: 'text/css', href: 'index.css', media: 'all')
	}
	body (class: 'page') {
		h1 ('系統管理區')
		div {
			a (href: 'javascript:location.reload()', '重新整理')
		}
		
		ul (class: 'menu-list') {
			li {
				a (href: 'latest_signup_users.groovy', '最新註冊使用者列表(僅顯示100筆)')
			}
			li {
				a (href: 'user_admin.groovy', '使用者管理')
			}
			li {
				a (href: 'teacher_admin.groovy', '教師管理')
			}
			li {
				a (href: 'course_admin.groovy', '教學資源管理')
			}
			li {
				a (href: 'class_admin.groovy', '課程管理')
			}
			li {
				a (href: 'sqlmon.groovy', '資料庫連線狀態監控')
			}
			li {
				a (href: 'announce.groovy', '公告管理')
			}
			li {
				a (href: 'aacsb.groovy', 'AACSB匯出程式')
			}
			li {
                a(href: 'update_public_course.groovy', '公開課程 (請使用admin帳號)')
            }
		}
	}
}
