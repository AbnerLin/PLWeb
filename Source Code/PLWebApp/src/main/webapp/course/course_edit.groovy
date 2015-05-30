import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def id    = request.getParameter('id')

def sql = new Sql(helper.connection)

row = sql.firstRow('select * from COURSE where COURSE_ID=?', [id])
id    = row.course_id
name  = row.course_name
title = row.course_title

//訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title("教材設定 - PLWeb")
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
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
		
		h2("教材設定")
		
		hr()

		form(action:"course_edit_save.groovy", method:"post") {
			input (type:"hidden", name:"id", value:id)
			
			table {
				tr {
					th (colspan: 2, '基本設定')
				}
				tr {
					td ('Content ID: ')
					td {
							input(type:'hidden', name:'name', value:name)
							span(name)
					}
				}
				tr {
					td ('Display Name: ')
					td {
						input (name:"title", value:title, size: 40)
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						input (type:"submit", value: "儲存")
						input (type:"button", value: "取消", onclick: "location.href='index.groovy';")
					}
				}
			}	
		}
	}
}
