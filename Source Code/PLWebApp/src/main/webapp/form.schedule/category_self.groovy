import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

query = """
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
"""

uid = helper.sess('uid')

books = []

sql.eachRow(query, [uid]) {
	row->
	books << [id: row.COURSE_ID, name: row.COURSE_NAME, title: row.COURSE_TITLE]
}

helper.attr('books', books)

sql.close()

helper.forward('category_self.gsp')
