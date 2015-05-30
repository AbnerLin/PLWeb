/**
 * 
 * @param type			:enum[begin_date, begin_time, due_date, due_time]
 * @param class_id		:integer
 * @param course_id		:integer
 * @param lesson_id		:integer
 * @param date			:string
 * 
 * @return json
 */

import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	println "error"
	return
}

type		= helper.fetch('type')
class_id	= helper.fetch('class_id')
course_id	= helper.fetch('course_id')
lesson_id	= helper.fetch('lesson_id')
datetime	= helper.fetch('datetime')

sql = new Sql(helper.connection)

allow = 'y'.equalsIgnoreCase(sql.firstRow("""
	select IS_TEACHER
	from USER_CLASS
	where CLASS_ID=?
	and USER_ID=?
""", [class_id, uid]).IS_TEACHER)

if (allow) {
	row = sql.firstRow("""
		select BEGINDATE, DUEDATE
		from CLASS_COURSE
		where CLASS_ID=?
		and COURSE_ID=?
		and LESSON_ID=?
	""", [class_id, course_id, lesson_id])
	
	begindate	= row.BEGINDATE?	new Date(row.BEGINDATE.toLong())	: new Date()
	duedate		= row.DUEDATE?		new Date(row.DUEDATE.toLong())		: new Date()
	
	switch (type) {
		case 'begin_date':
		time = begindate.format(helper.timeFormat)
		begindate = new Date().parse(helper.datetimeFormat, "${datetime} ${time}")
		break;
		
		case 'begin_time':
		date = begindate.format(helper.dateFormat)
		begindate = new Date().parse(helper.datetimeFormat, "${date} ${datetime}")
		break;
		
		case 'due_date':
		time = duedate.format(helper.timeFormat)
		duedate = new Date().parse(helper.datetimeFormat, "${datetime} ${time}")
		break;
		
		case 'due_time':
		date = duedate.format(helper.dateFormat)
		duedate = new Date().parse(helper.datetimeFormat, "${date} ${datetime}")
		break;
	}
	
	sql.executeUpdate("""
		update CLASS_COURSE
		set BEGINDATE=?,
		DUEDATE=?
		where CLASS_ID=?
		and COURSE_ID=?
		and LESSON_ID=?
	""", [begindate.time, duedate.time, class_id, course_id, lesson_id])
}

sql.close()
