import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

query = """
select CLASS_ID, CLASS_NAME, SCHOOL, DEPARTMENT, YEARS, SEMESTER
from ST_CLASS
where CLASS_ID=?
"""

uid = helper.sess('uid')
class_id = helper.fetch('class_id')

row = sql.firstRow(query, [class_id])
classRow = [
	id: row.CLASS_ID,
	name: row.CLASS_NAME,
	school: row.SCHOOL,
	dept: row.DEPARTMENT,
	year: row.YEARS,
	sems: row.SEMESTER
]

helper.attr('htmlhead', helper.htmlhead('../'))
helper.attr('class_row', classRow)

sql.close()

helper.include('schedule_detail.gsp')
