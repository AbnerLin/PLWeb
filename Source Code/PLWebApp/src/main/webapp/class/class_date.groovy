import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import groovy.sql.Sql

def uid = session.get('uid')
def utype = session.get('utype')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

def class_id = request.getParameter('class_id')

query1 = """
select CLASS_COURSE.*, COURSE.COURSE_TITLE
from CLASS_COURSE
inner join COURSE
	on CLASS_COURSE.COURSE_ID=COURSE.COURSE_ID
where CLASS_COURSE.CLASS_ID=?
order by CLASS_COURSE.SEQNUM
"""

sdf = new java.text.SimpleDateFormat('yyyy/MM/dd hh:mm:ss')

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Class Menu')
		link(rel: 'stylesheet', type: 'text/css', href: 'default.css')
	}
	body {
		table (width: '100%') {
			tr {
				th (width: 30, '#')
				th ('Book Name')
				th ('Chapter Name')
				th ('Begin Date/Time')
				th ('Due Date/Time')
			}
			c = 0
			sql.eachRow(query1, [class_id]) {
				row ->
				
				tr (class: c%2==0?'even':'odd') {
					td (align: 'center', c+1)
					td (row.course_title)
					td (row.title)
					
					begindate = row.begindate
					if (begindate){
						begindate = new Date(row.begindate.toLong())
						
						if (begindate <= new Date()) {
							td {
								strong {
									span (style: 'color:blue;font-size:15px', sdf.format(begindate))
								}
								
							}
						}
						else {
							td {
								strong {
									span (style: 'color:red;font-size:15px', sdf.format(begindate) + " !!!")
								}
							}
						}
					}
					else {
						td ('No Setting')
					}

					duedate = row.duedate
					if (duedate){
						duedate = new Date(row.duedate.toLong())
						
						if (duedate <= new Date()) {
							td {
								strong {
									span (style: 'color:blue;font-size:15px', sdf.format(duedate))
								}
								
							}
						}
						else {
							td {
								strong {
									span (style: 'color:red;font-size:15px', sdf.format(duedate) + " !!!")
								}
							}
						}
					}
					else {
						td ('No Setting')
					}
					
				}
				
				c++
			}
		}
	}
}

sql.close()
