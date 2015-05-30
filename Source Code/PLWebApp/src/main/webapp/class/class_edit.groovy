import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}

def id = helper.fetch('id')

def row = sql.firstRow('select * from ST_CLASS where CLASS_ID=?', [id])

def name		= row.CLASS_NAME
def school		= row.SCHOOL
def department	= row.DEPARTMENT
def password	= row.PASSWORD
def displayOnMenu = row.DISPLAY_ON_MENU

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title('修改課程設定 - PLWeb')
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
	}
	body (class: 'admin-layout') {
		h1 ('修改課程設定')

		p ("課程代號：${id}")

		a (href: 'index.groovy') {
			span (class: 'icons ss_arrow_undo')
			span ('返回課程管理')
		}
		span (' | ')
		a (href: 'javascript:location.reload()') {
			span (class: 'icons ss_arrow_refresh')
			span ('重新整理')
		}
			
		hr ()
		
		form(action: 'class_edit_save.groovy', method: 'post') {
			input (type: 'hidden', name: 'id', value: id)
			
			table (width: '480') {
				tr {
					th (colspan: 2, '課程基本資料')
				}
				tr {
					td (width: 100, '代號：')
					td ("${id} （唯讀）")
				}
				tr {
					td ('課程名稱:')
					td {
						input (name: 'name', value: name, style: 'width:100%')
					}
				}
				tr {
					td ('授課學校:')
					td {
						input (name: 'school', value: school)
					}
				}
				tr {
					td ('授課系所:')
					td {
						input (name: 'department', value: department)
					}
				}
				tr {
					td ('密碼:')
					td {
						input (type: 'password', name: 'password', value: password, autocomplete: 'off')
						span ('（測驗專用）')
					}
				}
				tr {
					td ('顯示設定:')
					td {
						if (displayOnMenu == 'y') {
							input (type: 'checkbox', id: 'displayOnMenu', name: 'displayOnMenu', value: 'y', checked: 'checked')
						}
						else {
							input (type: 'checkbox', id: 'displayOnMenu', name: 'displayOnMenu', value: 'y')
						}
						label (for: 'displayOnMenu', '顯示於課程選單')
					}
				}
				tr {
					td (colspan: 2, align: 'center', style: 'text-align:center') {
						button (class: 'fancy-button', type: 'submit', '儲存')
						button (class: 'fancy-button-gray', '取消', onclick: "location.href='index.groovy';return false")
					}
				}
			}
		}
	}
}
