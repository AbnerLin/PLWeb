import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

id    = helper.fetch('id')
name  = helper.fetch('name')
title = helper.fetch('title')

//訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.html {
	head {
		title("編輯教材設定 - PLWeb")
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
	}
	body (class: 'admin-layout') {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h1 ('新增教材')

		a (href: 'index.groovy') {
			span (class: 'icons ss_arrow_undo')
			span ('返回課程管理')
		}
		span (' | ')
		a (href: 'javascript:location.reload()') {
			span (class: 'icons ss_arrow_refresh')
			span ('重新整理')
		}
		
		hr()

		form(action:"course_add_save.groovy", method:"post") {
			input (type:"hidden", name:"id", value:id)
			input (type:"hidden", name:"oname", value:name)
			
			table (class: 'fancy-table') {
				tr {
					th (colspan: 2, '設定新教材')
				}
				tr {
					td ('教材代碼:')
					td {
						input (name:"name", value:name)
						br ()
						span ('輸入書籍ISBN或自訂代碼')
						br ()
						span ('若使用程式語言名稱建議加上個人代號, 例如 User1_Java')
						br ()
						span ('(限大小寫英數字及底線, 儲存後無法修改)')
					}
				}
				tr {
					td ('教材顯示名稱:')
					td {
						input (name:"title", value:title, size: 40)
						br ()
						span ('輸入書籍顯示名稱(中英文數字符號皆可)')
					}
				}
				tr {
					td (colspan: 2, align: 'center', class: 'center') {
						button (class: 'fancy-button', type: 'submit', '儲存')
						a (class: 'fancy-button-gray', href: 'index.groovy', '取消')
					}
				}
			}	
		}
	}
}
