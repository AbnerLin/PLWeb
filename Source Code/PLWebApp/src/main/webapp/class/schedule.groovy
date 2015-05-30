import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import java.text.SimpleDateFormat
import java.util.*
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}

query1 = """
select CLASS_NAME
from ST_CLASS
where CLASS_ID=?
"""

query2 = """
select CLASS_COURSE.*,
(select COURSE_NAME from COURSE where COURSE_ID=CLASS_COURSE.COURSE_ID) as COURSE_NAME,
(select COURSE_TITLE from COURSE where COURSE_ID=CLASS_COURSE.COURSE_ID) as COURSE_TITLE
from CLASS_COURSE
where CLASS_ID=?
order by SEQNUM
"""

class_id = request.getParameter('id')
class_name = sql.firstRow(query1, [class_id]).class_name

rows = sql.rows(query2, [class_id])

// Date Format Setting
sdf1 = new SimpleDateFormat('yyyy/MM/dd')
sdf2 = new SimpleDateFormat('HH:mm:ss')

dNow = new Date()
ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss")
dNow = ft.format(dNow)

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title ('課程進度管理 - PLWeb')
		
		// jQuery + jQuery UI
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js', '')
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js', '')
		link (rel: 'stylesheet', type: 'text/css', href: 'https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.6/themes/flick/jquery-ui.css')

		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
		
		script (type: 'text/javascript', src: '../lesson_play.js', '')
		script (type: 'text/javascript', src: 'schedule.js', '')
	}
	body (class: 'admin-layout') {
		h1 ('課程進度管理')

		p ("課程代碼：${class_id}")
		p ("課程名稱：${class_name}")

		a (class: 'fancy-link', href: 'index.groovy') {
			span (class: 'icons ss_arrow_undo')
			span ('返回課程管理')
		}
		span (' | ')
		a (class: 'fancy-link', href: 'javascript:location.reload()') {
			span (class: 'icons ss_arrow_refresh')
			span ('重新整理')
		}

		hr ()

		p {
			a (class: 'fancy-link', href: "schedule_lessons.groovy?id=${class_id}") {
				span (class: 'icons ss_book_add')
				span ('加入新的教材')
			}
		}
		
		p {
			span ('編輯模式：')
			input (class: 'fancy-button-gray', type: 'button', value: '簡易', onclick: "\$('.detail').hide();\$('.simple').show();")
			input (class: 'fancy-button-gray', type: 'button', value: '詳細', onclick: "\$('.detail').show();\$('.simple').hide();")
			span("System time: ${dNow}", style: "float: right")
		}
		
		form (action: 'schedule_save.groovy', method: 'post') {
			input(type: 'hidden', name: 'class_id', value: class_id)

			table (width: '100%', class: 'fancy-table') {
				tr {
					th (width: 30, class: 'small', '#')
					th ('教材 / 單元名稱')
					th (class: 'small', width: 100, '練習開放日期(起)')
					th (class: 'small', width: 100, '練習開放日期(訖)')
					th (class: 'small', width: 30, '播放')
					th (class: 'small', width: 30, '報表')
					th (class: 'small', width: 30, '排序')
					th (class: 'small', width: 30, '刪除')
				}
				
				c = 0
				
				rows.each {
					row->

					tr (class: c%2==0?'odd':'even') {
						td (align: 'center', class: 'small', style: 'font-family: Georgia;', c+1)
						td (class: 'small') {
							div (class: "detail", style: 'display:none') {
								span ("${row.course_title} (${row.course_name})")
								br ()
								input(name: 'title[]', value: row.title, style: 'width:100%')
							}
							div (class: 'simple') {
								span ("${row.title}")
							}
							
							
							input(type: 'hidden', name: 'course_id[]', value: row.course_id)
							input(type: 'hidden', name: 'lesson_id[]', value: row.lesson_id)
						}
						td {
							date_str = ''
							if (row.begindate) {
								date_str = sdf1.format(new Date(row.begindate.toLong()))
							}
							time_str = ''
							if (row.begindate) {
								time_str = sdf2.format(new Date(row.begindate.toLong()))
							}
							input (type: 'text', name: 'begindate[]', value: date_str, style: 'width: 8em; font-family: Georgia', class: 'datebox')
							div (class: "detail", style: 'display:none') {
								input(type: 'text', name: 'begintime[]', value: time_str, style: 'width: 8em; font-family: Georgia')
							}
						}
						td {
							date_str = ''
							if (row.duedate) {
								date_str = sdf1.format(new Date(row.duedate.toLong()))
							}
							time_str = ''
							if (row.duedate) {
								time_str = sdf2.format(new Date(row.duedate.toLong()))
							}
							input (type: 'text', name: 'duedate[]', value: date_str, style: 'width: 8em; font-family: Georgia', class: 'datebox')
							div (class: "detail", style: 'display:none') {
								input(type: 'text', name: 'duetime[]', value: time_str, style: 'width: 8em; font-family: Georgia')
							}
						}

						td (align: 'center', class: 'center') {
							a (href: "javascript: lessonPlay(${row.course_id}, ${row.lesson_id});", title: 'Play Lesson Content') {
								img (src: '../icons/application_go.png', border: 0)
							}
						}
						td (align: 'center', class: 'center') {
							a (href: "lamp.groovy?class_id=${class_id}&course_id=${row.course_id}&lesson_id=${row.lesson_id}") {
								img (src:'../icons/chart_bar.png', border:0)
							}
						}
						td (align: 'center', class: 'center') {
							input(type: 'text', name: 'order[]', value:c, style: 'width:3em')
						}
						td (align:'center', class: 'center') {
							//input(type: 'checkbox', name: 'del[]', value: c)
							a (href: "schedule_delete.groovy?class_id=${class_id}&course_id=${row.course_id}&lesson_id=${row.lesson_id}", onclick: "return confirm('請確認是否移除？');") {
                                span (class: 'icons ss_application_delete')
                            }


						}
					}
					c++
				}
				
				tr {
					td (colspan: 8, align: 'center', class: 'center') {
						if (rows) {
							input (class: 'fancy-button', type: 'submit', value: '確認送出')
						}
						else {
							span (style: 'color:red;font-weight:bold', '本課程尚未加入任何教材')
						}
						a (class: 'fancy-button-gray', href: 'index.groovy', '取消')
					}
				}
			}
		}
	}
}