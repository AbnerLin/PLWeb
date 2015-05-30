import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}

def uid = session.get('uid')
def utype = session.get('utype')

def ctype = helper.fetch('ctype')
def cyear = helper.fetch('cyear')

query1 = """
select ST_CLASS.*, USER_CLASS.IS_TEACHER
from ST_CLASS
inner join USER_CLASS
on USER_CLASS.CLASS_ID=ST_CLASS.CLASS_ID
where ST_CLASS.ALIVE='y'
and USER_CLASS.USER_ID=?
"""

if (ctype != null && ctype != "") {
	query1 += """
and ST_CLASS.SEMESTER='${ctype}'
"""
}

if (cyear != null && cyear != "") {
	query1 += """
and ST_CLASS.YEARS='${cyear}'
"""
}

query1 += """
order by ST_CLASS.CLASS_ID desc
"""

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title ('課程管理 - PLWeb')
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js', '')
		script (type: 'text/javascript', src: 'index.js')
	}
	body (class: 'admin-layout') {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}

		h1 ('課程管理')
		
		if ('T'.equalsIgnoreCase(utype)) {
			a (class: 'fancy-link', href: 'class_open.groovy') {
				span (class: 'icons ss_group_add')
				span ('建立新課程')
			}
			span('(教師專用)')
			span(' | ', style: 'color: gray')
		}
		
		a (class: 'fancy-link', href: 'class_join.groovy') {
			span (class: 'icons ss_user_add')
			span ('選修課程')
		}
		span('(學生)')
		span(' | ', style: 'color: gray')
		
		a (class: 'fancy-link', href: 'javascript:location.reload()') {
			span (class: 'icons ss_arrow_refresh')
			span ('重新整理')
		}

		hr ()

		def rows = sql.rows(query1, [uid])

		p {
			span ("您已經參與以下線上課程（共 ${rows.size()} 筆查詢結果）")

			def thisYear = Integer.valueOf(new Date().format('yyyy'))

			input (type: 'hidden', name: "cyear", value: cyear)
			select (id: 'year-filter') {
				option (value: '', '顯示全部年度')
				option (value: thisYear, "${thisYear}年")
				option (value: thisYear-1, "${thisYear-1}年")
				option (value: thisYear-2, "${thisYear-2}年")
			}

			input (type: 'hidden', name: "ctype", value: ctype)
			select (id: 'type-filter') {
				option (value: '', '顯示全部學期')
				option (value: '3', '上學期')
				option (value: '1', '下學期')
				option (value: '5', '測驗專用')
			}
		}

		table (width: '100%', class: 'fancy-table') {
			tr {
				th (width: 30, class: 'small', '#')
				th (width: 60, class: 'small', '課程代碼')
				th ('課程名稱')
				th (width: 200, class: 'small', '學校 / 系所')
				th (width: 120, class: 'small', '年 / 學期')
				
				if ('T'.equalsIgnoreCase(utype)) {
					th (width: 40, class: 'small', "進度")
					th (width: 40, class: 'small', "修改")
					th (width: 40, class: 'small', "刪除")
				}
			}
			c = 0

			rows.each {
				row ->
				
				href_list     = "class_list.groovy?class_id=${row.class_id}"
				href_schedule = "schedule.groovy?id=${row.class_id}"
				href_remove   = "class_remove.groovy?id=${row.class_id}"
				href_edit     = "class_edit.groovy?id=${row.class_id}"

				if(!row.semester.equals(5) || utype.equals('T'))
					tr (class: c%2==0?'even':'odd') {
						td (class: 'small', style: 'font-family: Georgia',  align: 'center', ++c)
						td (class: 'small', style: 'font-family: Georgia') {
							if ('y'.equalsIgnoreCase(row.IS_TEACHER)) {
								a (href: href_list, title:'List students', row.class_id)
							}
							else {
								span(row.class_id)
							}
						}
						td {
							//span (row.class_name)
							a (style: 'color: blue', href: response.encodeUrl("/classroom/${row.class_id}/"), target: '_top', "${row.class_name}" + ' »')
						}
						td (class: 'small', "${row.school} / ${row.department}")
						td (class: 'small') {
							span (style: 'font-family:Georgia', row.years)
							span (' / ')
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
								span('測驗專用')
								break;
							case '6':
								span('免登入專用')
								break;
							}
						}
						
						if ('T'.equalsIgnoreCase(utype)) {
							td (align: 'center', class: 'center') {
								if ('y'.equalsIgnoreCase(row.IS_TEACHER)) {
									a (href: href_schedule) {
										span (class: 'icons ss_calendar')
									}
								}
							}
							td (align: 'center', class: 'center') {
								if ('y'.equalsIgnoreCase(row.IS_TEACHER)) {
									a (href: href_edit) {
										span (class: 'icons ss_group_edit')
									}
								}
							}
							td (align: 'center', class: 'center') {
								if ('y'.equalsIgnoreCase(row.IS_TEACHER)) {
									a (href: href_remove, onclick: "return confirm('請確認是否移除？');") {
										span (class: 'icons ss_group_delete')
									}
								}
							}
						}
					}
			}
		}
	}
}

