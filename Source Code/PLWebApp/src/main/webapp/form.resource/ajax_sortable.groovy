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
lessonMap = request.getParameterValues('lessonMap[]');

sql = new Sql(helper.connection)

seqnum = 0

lessonMap.each {
	lesson_id ->
	sql.executeUpdate("""
		update COURSE_FILE
		set SEQNUM=?
		where COURSE_ID=?
		and LESSON_ID=?
	""", [seqnum, course_id, lesson_id])
	
	seqnum++
}

sql.close()

println "已更新${seqnum}筆記錄";