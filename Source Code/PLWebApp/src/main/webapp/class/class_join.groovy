import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import java.text.SimpleDateFormat
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}

def utype = session.get('utype')

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title('學生選修課程 - PLWeb')
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
	}
	body (class: 'admin-layout') {
		h1 ('選修課程')

		a (class: 'fancy-link', href: 'index.groovy') {
			span (class: 'icons ss_arrow_undo')
			span ('返回課程管理')
		}
		span (' | ')
		a (class: 'fancy-link', href: 'javascript:location.reload()') {
			span (class: 'icons ss_arrow_refresh')
			span ('重新整理')
		}
		
		hr()

		form (action: 'class_join_save.groovy', method: 'post') {
			table (width: 500, class: 'fancy-table') {
				tr {
					th (colspan: 2, '輸入課程代碼')
				}
				tr {
					td (align: 'right', class: 'right', '課程代碼:')
					td {
						input (name: 'class_id', size: 25, style: 'padding:2px;font-size:20px;font-family:Georgia')
						div (style: 'padding:5px;font-size:13px', '請向任課教師或註教取得課程代碼！')
					}
				}
				tr {
					td (colspan: 2, align: 'center', class: 'center') {
						button (class: 'fancy-button', type: 'submit', '確認送出')
						a (href: 'index.groovy', class: 'fancy-button-gray', '取消')
					}
				}
			}
		}
	}
}
