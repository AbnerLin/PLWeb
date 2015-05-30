import org.plweb.suite.common.xml.XmlFactory
import org.plweb.webapp.helper.CommonHelper
import org.apache.commons.lang.StringEscapeUtils
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.text.SimpleDateFormat

helper = new CommonHelper(request, response, session)

class_id	= helper.fetch('class_id')
lesson_id	= helper.fetch('lesson_id')
course_id	= helper.fetch('course_id')
user_id		= helper.fetch('user_id')
task_id		= helper.fetch('task_id')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

msg = sql.firstRow("""
	select *
	from ST_REPORTS
	where CLASS_ID=?
	and COURSE_ID=?
	and LESSON_ID=?
	and QUESTION_ID=?
	and USER_ID=?
	and CURRENT=1
""", [class_id, course_id, lesson_id, task_id, user_id])

sql.close()

//日期格式設定
def sdf = new SimpleDateFormat('yyyy/MM/dd hh:mm:ss')

html.doubleQuotes = true
html.html {
	head {
		title ('PLWeb - 程式實作練習詳細檢視')
	}
	body {
		table (cellspacing: 5) {
			tr {
				td (valign: 'top') {
				}
				td {
					h3 ('Source Code')
					
                    def timestring = ''
                    if (msg.TIME_UPDATED) {
                        timestring = new Date(msg.TIME_UPDATED.toLong()).format('yyyy-MM-dd HH:mm:ss')
                    }

                    p ("記錄時間：${timestring}")
 
                    if (msg.CODE) {
					    textarea(style:'width:640px;height:240px', msg.CODE)
					}
					else {
						p('查無程式碼')
					}
					
					h3 ('Messages')
					textarea(style:'width:640px;height:240px', msg.MESSAGE)					
				}
			}
		}
	}
}
