import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect('/permission_denied.groovy')
	return;
}

uid = session.get('uid')

// get/post data
id			= helper.fetch('id')
years		= helper.fetch('years')
semester	= helper.fetch('semester')
name		= helper.fetch('name')
school		= helper.fetch('school')
dept		= helper.fetch('dept')
password	= helper.fetch('password')

course_type = Integer.valueOf(helper.fetch('course_type', '0'))

_SQL_NEW_ID_ = """
select max(CLASS_ID)+1 as NEWID
from ST_CLASS
where YEARS=?
and SEMESTER=?
"""

_SQL_ADD_CLASS_ = """
insert into ST_CLASS (CLASS_ID, CLASS_NAME, SCHOOL, DEPARTMENT, YEARS, SEMESTER, PASSWORD, ALIVE)
values (?,?,?,?,?,?,?,?)
"""

_SQL_LINK_USER_CLASS_ = """
insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
values(?,?,?)
"""

_SQL_LINK_CLASS_COURSE_ = """
insert into CLASS_COURSE (CLASS_ID, COURSE_ID, LESSON_ID, TITLE, SEQNUM)
                  values (?,        ?,         ?,         ?,     ?)
"""

sql = new Sql(helper.connection)

try {
	if (id == null) {
		// 找出新的id
		row = sql.firstRow(_SQL_NEW_ID_, [years, semester])

		if (row.newid != null) {
			id = row.newid
		}
		else {
			id = new String("${years}${semester}00001")
		}
	}
	
	sql.execute(_SQL_ADD_CLASS_, [id, name, school, dept, years, semester, password, 'y'])
	sql.execute(_SQL_LINK_USER_CLASS_, [uid, id, 'y'])

	//add "TQC+ Java 6" dirty hacks
	if (course_type >= 1 && course_type <= 4) {
		ccid = 173
		if (course_type >= 1 && course_type <= 3) {
			ccid = 173
		}
		else {
			ccid = 158
		}
		c = 0;
		sql.eachRow("select COURSE_ID, LESSON_ID, TITLE from COURSE_FILE where COURSE_ID=${ccid} and VISIBLED='y' order by SEQNUM asc") {
			course_row->

			sql.execute(
				_SQL_LINK_CLASS_COURSE_,
				id,
				course_row.COURSE_ID,
				course_row.LESSON_ID,
				course_row.TITLE,
				c++
			)
		}
	}

	session.setAttribute('alert_message', "新課程 ${id} 已經建立完成，請回到主畫面，並按瀏覽器的「重新整理」按鈕，「我的課程」才會顯示新課程。");
}
catch (e) {
	session.setAttribute('error_message', e.message);
}

//response.sendRedirect('index.groovy')
response.sendRedirect("schedule.groovy?id=${id}")

sql.close()
