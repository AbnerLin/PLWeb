import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import javax.naming.InitialContext

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')
utype = helper.sess('utype')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

if(!session || utype != 'M'){
        response.sendRedirect('/permission_denied.groovy')
        return;
}

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)


query = """ SELECT content FROM PUBLIC_COURSE """

content = sql.firstRow(query).content

html.setDoubleQuotes(true)
html.html{
    head{
        title('PLWeb - Public Course')
    }
    body{
		h3('Preview'){
		    hr()
            print content
		}

		h3('update List') {
			form (action: 'public_course_save.groovy', method: 'post') {
				table {
					tr {
						hr()
					}
					tr {
						 th ('List:')
						 td {
							 textarea (cols: 100, rows: 30, name: 'list', content)
						 }
					}
					tr {
						td(colspan: 2, align: 'right') {
							 input (type: 'submit', value: 'Post')
						}
					}
				}
			}
		}
    }
}

