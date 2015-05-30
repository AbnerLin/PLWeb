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
lesson_id = helper.fetch('lesson_id')

sql = new Sql(helper.connection)

//println sql.firstRow("""
//	select HTML_TEXT
//	from COURSE_FILE
//	where COURSE_ID=?
//	and LESSON_ID=?
//""", [course_id, lesson_id]).HTML_TEXT

sql.close()

println """
<iframe class="chapter-import-iframe" src="form.import/select.groovy?course_id=${course_id}&lesson_id=${lesson_id}" />
"""

