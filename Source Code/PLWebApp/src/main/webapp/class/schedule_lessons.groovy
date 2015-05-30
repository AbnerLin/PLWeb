import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import javax.naming.InitialContext

uid = session.get('uid')

def class_id = request.getParameter('id')

def ds  = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
select CLASS_NAME
from ST_CLASS
where CLASS_ID=?
"""

query2 = """
select COURSE_FILE.COURSE_ID,
	COURSE_FILE.LESSON_ID,
	COURSE_FILE.TITLE,
	COURSE.COURSE_NAME,
	COURSE.COURSE_TITLE,
	COURSE.IS_GLOBAL,
	(select count(*)
		from CLASS_COURSE
		where CLASS_COURSE.CLASS_ID=?
		and CLASS_COURSE.COURSE_ID=COURSE_FILE.COURSE_ID
		and CLASS_COURSE.LESSON_ID=COURSE_FILE.LESSON_ID) as IS_EXISTS,
	(select count(*)
		from USER_COURSE
		where USER_COURSE.USER_ID=?
		and USER_COURSE.COURSE_ID=COURSE_FILE.COURSE_ID
		and USER_COURSE.IS_OWNER='y') as IS_OWNER
from COURSE_FILE
inner join COURSE
on COURSE_FILE.COURSE_ID=COURSE.COURSE_ID
where COURSE.VISIBLED='y'
and COURSE_FILE.COURSE_ID > 0
and COURSE_FILE.VISIBLED='y'
and (COURSE.IS_SHARE='y'
		or exists (select 1
				from USER_COURSE
				where USER_COURSE.USER_ID=?
				and USER_COURSE.COURSE_ID=COURSE_FILE.COURSE_ID))
order by COURSE_FILE.COURSE_ID, COURSE_FILE.SEQNUM
"""

class_name = sql.firstRow(query1, [class_id]).class_name

html.setDoubleQuotes(true)
html.html {
	head {
		title('加入新教材 - PLWeb')
		script (type: 'text/javascript', src: 'lesson_play.js', '')
		script (type: 'text/javascript', src: 'jquery/jquery-1.3.2.min.js', '') 
		script (type: 'text/javascript', src: 'jquery/jquery-ui-1.7.1.custom.min.js', '')
		script (type: 'text/javascript', src: 'schedule_lessons.js', '')
		link (rel:'stylesheet', type:'text/css', href:'../css/reset.css', media:'all')
		link(rel: 'stylesheet', type: 'text/css', href: 'default.css')
		link(rel: 'stylesheet', type: 'text/css', href: 'jquery/css/smoothness/jquery-ui-1.7.1.custom.css')

	}
	body (class: 'page') {
		h1 ('加入新教材')

		p ("課程代碼：${class_id}")
		p ("課程名稱：${class_name}")

		a(href: "schedule.groovy?id=${class_id}") {
			img (src:'../icons/arrow_undo.png', border:0)
			span ('返回')
		}

		hr ()

		row_map = new HashMap()
		row_section = new HashMap()
		row_owner = new HashMap()
		row_global = new HashMap()
		
		sql.eachRow(query2, [class_id, uid, uid]) {
			row->
			if (row_map.containsKey(row.course_id)) {
				row_list = row_map.get(row.course_id)
			}
			else {
				row_list = []
				row_map.put(row.course_id, row_list)
				row_section.put(row.course_id, "${row.course_title} (${row.course_name})")
				
				
				if (row.IS_OWNER > 0) {
					row_owner.put(row.course_id, "${row.course_title} (${row.course_name})")
				}
				
				if ('y'.equalsIgnoreCase(row.IS_GLOBAL)) {
					row_global.put(row.course_id, "${row.course_title} (${row.course_name})")
				}
			}
			row_list << [course_id:row.course_id, lesson_id:row.lesson_id, title:row.title, course_name:row.course_name, course_title:row.course_title, is_exists:row.is_exists]
		}
				
		div (class: 'book-catalog') {
			h2 ('教材分類')

			h3 ('PLWeb 標準教材（平台提供）', onclick: "toogleCatalog('.book-catalog-plweb')")
			ul (class: 'book-catalog-plweb') {
				row_global.keySet().each {
					course_id->
					row = row_global.get(course_id)
					li {
						a (href: "#course${course_id}", row)
					}
				}
			}
			
			h3 ('我的教材', onclick: "toogleCatalog('.book-catalog-owner')")
			ul (class: 'book-catalog-owner') {
				row_owner.keySet().each {
					course_id->
					row = row_owner.get(course_id)
					li {
						a (href: "#course${course_id}", row)
					}
				}
			}
			
			h3 ('其他教師分享的教材', onclick: "toogleCatalog('.book-catalog-other')")
			ul (class: 'book-catalog-other') {
				row_section.keySet().each {
					course_id->
					row = row_section.get(course_id)
					li {
						a (href: "#course${course_id}", row)
					}
				}
			}
		}
		
		hr ()
		
		form (action: 'schedule_lessons_save.groovy', method: 'post') {
			input(type: 'hidden', name: 'class_id', value: class_id)
			
			div (id: 'accordion') {
				row_map.keySet().each {
					course_id->
					
					div {
						p {
							a (class: 'icon', name: "course${course_id}") {
								img (src: '../icons/book.png', border: 0)
								span (row_section.get(course_id))
							}
						}
					
						n = 0
						table (width: '100%') {
							tr {
								th (width:40, '#')
								//th (width:250, 'Book Name')
								th ('教材單元名稱')
								th (width: 40, '預覽')
								th (width: 50, '選取')
							}
							c = 0
							row_map.get(course_id).each {
								row->
								tr (class: c%2==0?'odd':'even') {
									td(align: 'center', ++c)
									//td("${row.course_title} (${row.course_name})")
									td (row.title)
									td (align: 'center') {
										a (href: "javascript: lessonPlay(${row.course_id}, ${row.lesson_id});", title: '預覽教材內容') {
											img (src: '../icons/application_go.png', border: 0)
										}
									}
									td (align:'center') {
										if (row.is_exists) {
											input(type: 'checkbox', name: 'checks[]', value: '', disabled: true, checked: true)
										}
										else {
											input(type: 'checkbox', name:'checks[]', value: "${row.course_id};${row.lesson_id}")
										}
									}
								}
							}
							tr {
								td (colspan: 4, align: 'right') {
									input (type: 'submit', value: '加入選取的教材')
								}
							}
						}
					}
				}
			}
		}
	}
}

sql.close()
