import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

def uid		= helper.sess('uid')
def utype	= helper.sess('utype')

if (!uid) {
	println """權限不足，請重新<a href=\"${helper.basehref}login/\">登入</a>。"""
	return
}

def class_id = helper.fetch_keep('c')

def course_id	= null

_SQL_CLASS = """
	select a.*
	from ST_CLASS a
	where a.CLASS_ID=?
"""

_SQL_LESSON_LIST = """
	select CLASS_COURSE.*, COURSE.COURSE_TITLE
	from CLASS_COURSE
	inner join COURSE
		on CLASS_COURSE.COURSE_ID=COURSE.COURSE_ID
	where CLASS_COURSE.CLASS_ID=?
	order by CLASS_COURSE.SEQNUM
"""

_SQL_STUDENT_LIST = """
	select u.NAME, uc.IS_TEACHER
	from USER_CLASS uc
	inner join ST_USER u on u.USER_ID=uc.USER_ID
	where uc.CLASS_ID=?
"""

_SQL_HAS_GRADE = """  SELECT COUNT(*) AS COUNT FROM CLASS_COURSE AS A, GRADE_SETTING AS B WHERE A.CLASS_ID=? AND A.COURSE_ID=B.COURSE_ID AND A.LESSON_ID=B.LESSON_ID """
hasGrade = sql.firstRow(_SQL_HAS_GRADE, [class_id]).COUNT

is_teacher = sql.firstRow("""
	select IS_TEACHER
	from USER_CLASS
	where USER_ID=?
	and CLASS_ID=?
""", [uid, class_id]).IS_TEACHER.equalsIgnoreCase('y')

the_class = sql.firstRow(_SQL_CLASS, [class_id])

if(the_class.semester.equals(5) && !utype.equals("T")){
        println """ 權限不足。 """
        return
}

students = []
sql.eachRow(_SQL_STUDENT_LIST, [class_id]) {
	row->
	if ('n'.equals(row.IS_TEACHER)) {
		students << row.NAME
	}
}

schedule_url = helper.make_url('class/schedule.groovy', [id: class_id], true)
status_url   = helper.make_url('panel.class/student_report.groovy', [class_id: class_id], true)
grade_report = helper.make_url('panel.class/grade_report.groovy', [class_id: class_id], true)

html.doubleQuotes = true
html.div(class: 'showClass') {
	h2 ('課程')
	table (class: 'classTable', cellpadding: 0, cellspacin: 0, border: 0, width: '100%', style: 'line-height:1.4em') {
		tr {
			th (width: 120, '課程代碼')
			td (style: 'font-size:26px;color:red;font-weight:bold;font-family:Georgia;', the_class.CLASS_ID)
		}
		tr {
			th ('課程名稱')
			td (the_class.CLASS_NAME)
		}
		tr {
			th ('開課學校 / 系所')
			td ("${the_class.SCHOOL} / ${the_class.DEPARTMENT}")
		}
		tr {
			semester = the_class.SEMESTER
			switch (the_class.SEMESTER) {
				case 1:
				semester = "下學期"
				break;
				case 3:
				semester = "上學期"
				break;
			}
			th ('學期別')
			td ("${the_class.YEARS} / ${semester}")
		}
		if (is_teacher) {
			tr {
				th ('修課學生')
				td {
					div (style: 'font-size: 13px') {
						if (students.size() > 10) {
							span (class: 'head-students') {
								span (students[0..5].join(', '))
								a (href: '#', '...', class: 'button-show-students', title: '顯示全部學生名單')
							}
							span (class: 'hide-students') {
								span (students.join(', '))
							}
						}
						else {
							span (students.join(', '))
						}
						span ("(共 ${students.size()} 位)")
					}
				}
			}
			
			tr {
				th ('課程規劃')
				td {
					button (class: 'btn-control', onclick: "embedded_link('${schedule_url}')") {
						img (src: 'icon-16/calendar.png', alt: 'calendar.png')
						span ('課程進度管理')
					}
				}
			}
		}
		tr {
			th ('學習狀態')
			td {
				button (class: 'btn-control', onclick: "embedded_link('${status_url}')") {
					img (src: 'icon-16/calendar.png', alt: 'calendar.png')
					span ('學習狀態報表')
				}
			}
		}
		if(is_teacher && hasGrade > 0){
			tr {
				th ('學期成績')
				td {
					button (class: 'btn-control', onclick: "embedded_link('${grade_report}')") {
						img (src: 'icon-16/calendar.png', alt: 'calendar.png')
						span ('學期成績報表')
					}
				}
			}
		}
	}

	h2 ('課程大綱')
	if (is_teacher) {
		a (href: "/dashboard/edithtml.groovy?c=${the_class.CLASS_ID}", style: 'float:right', class: 'embedded-link', '編輯內容')
	}
	div (class: 'prettyhtml') {
		if (the_class.HTML_TEXT) {
			mkp.yieldUnescaped (the_class.HTML_TEXT)
		}
		else {
			p ('無內容！')
		}
	}
	h2 ('教學單元')
	ol (class: 'lesson-menu') {
		sql.eachRow(_SQL_LESSON_LIST, [class_id]) {
			lesson->
			if (lesson.BEGINDATE) {
				li {
					href = response.encodeUrl("/content/${class_id}/${lesson.COURSE_ID}/${lesson.LESSON_ID}/")
					a (class: 'lesson-title', href: href, lesson.TITLE)
					span (class: 'courseTitle', lesson.COURSE_TITLE)
				}
			}
			else if (is_teacher) {
				li {
					href = response.encodeUrl("/content/${class_id}/${lesson.COURSE_ID}/${lesson.LESSON_ID}/")
					a (class: 'lesson-title-preview', href: href, lesson.TITLE)
					span (class: 'courseTitle', lesson.COURSE_TITLE)
				}
			}
			else {
				li {
					span (class: 'lesson-title-preview', lesson.TITLE)
					span (class: 'courseTitle', lesson.COURSE_TITLE)
				}
			}
		}
	}
}