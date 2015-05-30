import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect 403
	return;
}

def uid = session.get('uid')

def dataPath = request.getRealPath('/data')

def sql = new Sql(helper.connection)
def rows = []

query = """
select COURSE.*, USER_COURSE.IS_OWNER,
(select count(*)
	from COURSE_FILE
	where COURSE_FILE.COURSE_ID=COURSE.COURSE_ID
	and COURSE_FILE.VISIBLED='y') as LESSON_NUM,
(select sum(TEXT_SIZE)
	from COURSE_FILE
	where COURSE_FILE.COURSE_ID=COURSE.COURSE_ID
	and COURSE_FILE.VISIBLED='y') as LESSON_SIZE
from COURSE
inner join USER_COURSE on USER_COURSE.COURSE_ID=COURSE.COURSE_ID
where USER_COURSE.USER_ID=?
and COURSE.VISIBLED='y'
order by COURSE.COURSE_ID
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
		title("教材管理 - PLWeb")

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
		
		h1 ("教材管理")

		a (href: 'course_add.groovy') {
			span (class: 'icons ss_application_add')
			span ('新增教材')
		}
		hr()

		p("教材清單如下")

		table (width: '100%', class: 'fancy-table') {
			tr {
				th (class: 'small', width: 30, '#')
				th (class: 'small', '教材代碼')
				th ('教材名稱')
				th (width: 40, class: 'small', '數量')
				th (width: 100, class: 'small', '容量')
				th (width: 40, class: 'small', '修改')
				th (width: 40, class: 'small', '刪除')
			}
			c = 0
			t_size = 0
			t_count = 0
			sql.eachRow(query, [uid]) {
				row ->
				href_select = "lesson_list.groovy?course_id=${row.course_id}"
				href_remove = "course_remove.groovy?id=${row.course_id}&course="+URLEncoder.encode("${row.course_name}")
				href_edit   = "course_edit.groovy?id=${row.course_id}"

				if (row.lesson_size) {
					t_size += row.lesson_size
				}
				if (row.lesson_num) {
					t_count += row.lesson_num
				}
				
				tr (class: c%2==0?'even':'odd') {
					td (align: 'center', class: 'small center', ++c)
					td {
						img (src:'../icons/application.png', border:0, class: 'icon')
						a (href: href_select, title: '列出教材單元') {
							span (style: 'font-family:Courier New;font-weight:bold', row.course_name)
						}
					}
					td (row.course_title)
					td (class: 'small', align: 'center', style: 'text-align:center') {
						span ("${row.lesson_num}")
					}
					td (class: 'small', style: 'text-align: right') {
						def sizeInKilobytes = Math.round(row.lesson_size==null?0:row.lesson_size/1024)
						sizeInKilobytes = DecimalFormat.getNumberInstance().format(sizeInKilobytes)
						span ("${sizeInKilobytes} kb")
					}
					td (align: 'center', style: 'text-align:center') {
						if (row.is_owner == 'y') {
							a(class:'icon', href: href_edit, title: 'Edit') {
								img (src:'../icons/application_edit.png', border:0)
							}
						}
					}
					td (align: 'center', style: 'text-align:center') {
						if (row.is_owner == 'y') {
							a(class:'icon', href: href_remove, title: 'Remove', onclick: "return confirm('是否確認要移除此教材?');") {
								img (src:'../icons/application_delete.png', border:0)
							}
						}
					}
				}
			}
			tr {
				th (colspan: 3) {
					span('總計')
				}
				td (class: 'small', align: 'center', style: 'text-align:center') {
					span("${t_count}")
				}
				td (class: 'small', align: 'right', style: 'text-align:right') {
					def sizeInKilobytes = Math.round(t_size==null?0:t_size/1024)
					sizeInKilobytes = DecimalFormat.getNumberInstance().format(sizeInKilobytes)
					span("${sizeInKilobytes} kb")
				}
				td (colspan: 2)
			}
		}
	}
}
