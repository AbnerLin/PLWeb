import java.text.NumberFormat
import java.text.DecimalFormat
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

if (!session) {
	helper.sendError 403
	return
}

def uid	  = session.get('uid')
def uname = session.get('uname')
def utype = session.get('utype')

if (!uid) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def course_id = request.getParameter('course_id')

def sql = new Sql(helper.connection)

row = sql.firstRow('select COURSE_NAME, COURSE_TITLE from COURSE where COURSE_ID=?', [course_id])
course_name  = row.course_name
course_title = row.course_title

row = sql.firstRow('select * from USER_COURSE where COURSE_ID=? and USER_ID=?', [course_id, uid])
is_owner = row.is_owner=='y'

//日期及數字格式設定
def nf = NumberFormat.getInstance()
nf.setMaximumFractionDigits( 2 )
nf.setMinimumFractionDigits( 2 )

query0 = """
select count(*) as cc
from COURSE_FILE
where COURSE_ID=?
and VISIBLED='y'
"""

query1 = """
select TITLE, UPDATED, COURSE_ID, LESSON_ID, TEXT_SIZE, TASKNUM
from COURSE_FILE
where COURSE_ID=?
and VISIBLED='y'
order by SEQNUM
"""

query_member = """
select USER_COURSE.*, ST_USER.NAME, ST_USER.EMAIL
from USER_COURSE
inner join ST_USER
on USER_COURSE.USER_ID=ST_USER.USER_ID
where USER_COURSE.COURSE_ID=?
order by USER_COURSE.USER_ID
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
		title('教材單元列表 - PLWeb')
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
		
		h1('教材單元列表')
		
		p {
			span ("${course_name} / ${course_title}")
			span (style: 'color:gray', "(${course_id})")
		}

		a(href: 'index.groovy') {
			img (src:'../icons/arrow_undo.png', border:0)
			span ('返回教材管理')
		}

		hr ()

		table (width: '100%', class: 'fancy-table') {
			tr {
				th (width: 30, '#')
				th (width: 60, '排序')
				th ('單元標題')
				th (width: 40, '數量')
				th (width: 100, '容量')
				th (class: 'small', width: 150, '最後修改日期')
				th (class: 'small', width: 40, '修改')
				th (class: 'small', width: 100, '維護')
			}
			
			rows = sql.rows(query1, [course_id])
				
			c = 0
			t_size = 0
			t_task = 0
			rows.each {
				row->
				
				t_size += row.text_size
				t_task += row.tasknum
				
				href_export	= "lesson_export.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_play   = "javascript: lessonPlay('${row.course_id}', '${row.lesson_id}');"
				href_edit	= "/webstart.groovy?mode=author&course_id=${row.course_id}&lesson_id=${row.lesson_id}&class=0"
				href_up		= "lesson_up.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_down	= "lesson_down.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_copy	= "lesson_copy.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_remove	= "lesson_remove.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"

				tr (class: c%2==0?'even':'odd') {
					td (class: 'small', align:'center', style: 'text-align:center', ++c)
					td (align:'center') {
						if (c > 1)
						a(class: 'icon', href: href_up) {
							img (src: '../icons/arrow_up.png', border:0, align:'left')
						}
						if (c < rows.size())
						a(class: 'icon', href: href_down) {
							img (src: '../icons/arrow_down.png', border:0, align:'right')
						}
					}
					td {
						img (src: '../icons/book.png', border:0)
						a (href: href_edit, title: "編輯 ${row.title}") {
							span ("${row.title}")
						}
					}
					td (class: 'small', align: 'center', style: 'text-align:center') {
						span (row.tasknum)
					}
					td (class: 'small', align:'right', style: 'text-align:right') {
						def sizeInKilobytes = Math.round(row.text_size==null?0:row.text_size/1024)
						sizeInKilobytes = DecimalFormat.getNumberInstance().format(sizeInKilobytes)
						span ("${sizeInKilobytes} kb")
					}
					td (class:'small', align:'center', style: 'text-align:center') {
						def sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm")
						def dateToDisplay = sdf.format(new Date(row.updated?.toLong()))
						span (dateToDisplay)
					}
					td (align: 'center', style: 'text-align:center') {
						a (href: href_edit) {
							span (class: 'icons ss_book_edit')
						}
					}
					td (align:'center') {
						a (title: 'Play', href: href_play) {
							span (class: 'icons ss_book_go')
						}
						a (title: 'Export', href: href_export) {
							span (class: 'icons ss_book_link')
						}
						a (title: 'Copy', href: href_copy) {
							span (class: 'icons ss_book_add')
						}
						a (title: 'Remove', href: href_remove, onclick: "return confirm('Are you sure???');") {
							span (class: 'icons ss_book_delete')
						}
					}

				}
			}
			tr {
				th (colspan: 3, '小計')
				td (class: 'small', align: 'center', style: 'text-align:center') {
					span (t_task)
				}
				td (class:'small', align: 'right', style: 'text-align:right') {
					def sizeInKilobytes = Math.round(t_size==null?0:t_size/1024)
					sizeInKilobytes = DecimalFormat.getNumberInstance().format(sizeInKilobytes)
					span ("${sizeInKilobytes} kb")
				}
				td (colspan:3)
			}
			tr {
				td (colspan: 8, align: 'right') {
					input (type:'button', onclick:"location.href='course_update.groovy?course_id=${course_id}'", value: '更新單元')
				}
			}
		}
		
		hr()

		h2 ('新增單元')

		form (action: 'lesson_add.groovy', method: 'post') {
			input (type: 'hidden', name: 'course_id', value: course_id)
			span ('從範本選取:')
			select (name:'template', '') {
				sql.eachRow(query1, [0]) {
					row->
					option(value:row.lesson_id, row.title)
				}
			}
			input (type: 'submit', value: '新增單元')
		}
		hr()

		h2 ('進階設定')
		
		row = sql.firstRow('select * from COURSE where COURSE_ID=?', [course_id])
		table (width: '100%') {
			tr {
				th ('Setting Name')
				th ('Value')
				th ('Options')
				th ('Description')
			}
			tr {
				td ('Shareable?')
				td (row.is_share)
				td {
					a (href: "course_setting.groovy?course_id=${course_id}&action=is_share&value=y", 'y')
					span (' | ')
					a (href: "course_setting.groovy?course_id=${course_id}&action=is_share&value=n", 'n')
				}
				td ('Share this material to other teachers. (y = Yes, n = No)')
			}
		}
		hr()

		h2 ('權限設定')
		
		table (width: '100%'){
			tr {
				th ('#')
				th (width: 100, 'User ID')
				th ('E-Mail')
				th ('Name')
				th (width: '100', 'Owner')
				th (class: 'small', width: '40', 'Remove')
			}
			c = 0
			sql.eachRow(query_member, [course_id]) {
				row->
				tr (class: c%2==0?'even':'odd') {
					td (align: 'center', ++c)
					td (row.user_id)
					td (row.email)
					td (row.name)
					td (align: 'center') {
						if (row.is_owner=='y') {
							strong ('YES')
							span('(')
							a (href: "course_member_owner.groovy?action=unset&course_id=$course_id&user_id=${row.user_id}", "NO")
							span(')')
						}
						else {
							strong ('NO')
							span('(')
							a (href: "course_member_owner.groovy?action=set&course_id=$course_id&user_id=${row.user_id}", "YES")
							span(')')
						}
					}
					td (align: 'center'){
						if (row.user_id != uid) {
							a (href: "course_member_remove.groovy?course_id=$course_id&user_id=${row.user_id}", onclick: "return confirm('Are you sure?');") {
								img (src: 'icons/user_delete.png', border: 0)
							}
						}
					}
				}
			}
		}
		div (align: 'right') {
			ul {
				li ("You can't remove yourself.")
				li ("Owners have permission to add new member.")
			}
		}
		if (is_owner) {
			form (action: 'course_member_add.groovy', method: 'post') {
				input (type: 'hidden', name: 'course_id', value: course_id)
				span ('新增編輯成員:')
				input (name: 'user_id')
				input (type: 'submit', value: '新增成員')
			}
		}
		hr()
		h2 ('上傳教材')
		form (action: 'lesson_upload.groovy', method: 'post', enctype: 'multipart/form-data') {
			input (type: 'hidden', name: 'course_id', value: course_id)
			p {
				span ('教材檔案:')
				input (type: 'file', name: 'picture', size: '30')
				span ('(教材檔名必須為 *.xml)')
			}
			input (type: 'submit', value: '開始上傳')
		}
		hr()

		h2 ('匯出教材')
		
		p {
			span ('ZIP 壓縮格式')
			a (href: "course_export.groovy?course_id=${course_id}") {
				strong("${course_name}.zip")
			}
		}
		ul {
			li ("Clcik and save the file to your selected disk location.")
			li ("This archived file includes serval lesson*.xml files.")
		}
	}
}

sql.close()
