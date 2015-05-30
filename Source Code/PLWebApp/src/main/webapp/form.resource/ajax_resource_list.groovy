import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	println "error"
	return
}

sql = new Sql(helper.connection)

resources = []
sql.eachRow("""
select COURSE.COURSE_ID, COURSE.COURSE_NAME, COURSE.COURSE_TITLE, USER_COURSE.IS_OWNER,
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
""", [uid]) {
	row->
	resources << [id: row.COURSE_ID, name: row.COURSE_NAME, title: row.COURSE_TITLE, num: row.LESSON_NUM, owner: 'y'.equalsIgnoreCase(row.IS_OWNER)]
}

sql.close()

helper.attr 'helper', helper
helper.attr 'resources', resources

helper.include('ajax_resource_list.gsp')
