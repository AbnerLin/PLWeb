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
	select *
	from COURSE
	where COURSE_ID=?
""", [course_id])

resource = [
	id: row.COURSE_ID,
	name: row.COURSE_NAME,
	title: row.COURSE_TITLE,
	visibled: 'y'.equalsIgnoreCase(row.VISIBLED),
	is_share: 'y'.equalsIgnoreCase(row.IS_SHARE),
	html_text: row.HTML_TEXT,
	html_link: row.HTML_LINK
]

chapters = []

sql.eachRow("""
	select TITLE, UPDATED, COURSE_ID, LESSON_ID, TEXT_SIZE, TASKNUM
	from COURSE_FILE
	where COURSE_ID=?
	and VISIBLED='y'
	order by SEQNUM
""", [course_id]) {
	row->
	chapters << [
		id: row.LESSON_ID,
		pid: row.COURSE_ID,
		title: row.TITLE,
		num: row.TASKNUM,
		size: row.TEXT_SIZE,
		updated: new Date(row.UPDATED.toLong()).format('yyyy-MM-dd HH:mm:ss'),
		editor_url: helper.make_url('webstart.groovy', [mode: 'author', course_id: course_id, lesson_id: row.LESSON_ID], true)
	]
}

sql.close()

sort_url = helper.make_url('form.resource/sortable.groovy', [course_id: course_id], true)

helper.attr 'sort_url', sort_url
helper.attr 'helper', helper
helper.attr 'resource', resource
helper.attr 'chapters', chapters

helper.include 'ajax_resource_load.gsp'
