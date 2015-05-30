/**
 * 
 * @param course_id		:integer
 */
import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	println "error"
	return
}

course_id = helper.fetch('course_id')

sql = new Sql(helper.connection)

row = sql.firstRow("""
	select COURSE_ID, COURSE_NAME, COURSE_TITLE
	from COURSE
	where COURSE_ID=?
""", [course_id])

resource = [
	id: row.COURSE_ID,
	name: row.COURSE_NAME,
	title: row.COURSE_TITLE
]

chapters = []

sql.eachRow("""
	select LESSON_ID, TITLE
	from COURSE_FILE
	where COURSE_ID=?
	and VISIBLED='y'
	order by SEQNUM
""", [course_id]) {
	row->
	chapters << [
		id: row.LESSON_ID,
		title: row.TITLE
	]
}

sql.close()

helper.attr 'helper', helper
helper.attr 'resource', resource
helper.attr 'chapters', chapters
helper.attr 'course_id', course_id

helper.include 'sortable.gsp'
