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

msg = helper.fetch('msg')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

msg_list = []
sql.eachRow("""
	select SNAPSHOT_ID, TIME_UPDATED
	from ST_SNAPSHOT
	where CLASS_ID=?
	and COURSE_ID=?
	and LESSON_ID=?
	and QUESTION_ID=?
	and USER_ID=?
	order by TIME_UPDATED
""", [class_id, course_id, lesson_id, task_id, user_id]) {
	msg_list << [it.SNAPSHOT_ID, it.TIME_UPDATED]
}

if (msg) {
	row = sql.firstRow("""
		select CODE, KEYSTROKE
		from ST_SNAPSHOT
		where SNAPSHOT_ID=?
	""", [msg])
	
	code = row.CODE
	
	jscode = ''
	es_code = row.KEYSTROKE
	//es_code = row.ES.getSubString(1, (int)row.ES.length())
	
	while (es_code != '') {
		pos_0 = es_code.indexOf('<es:type>')
		pos_1 = es_code.indexOf('<es:offset>')
		pos_2 = es_code.indexOf('<es:string>')
		pos_3 = es_code.indexOf('<es:length>')
		pos_4 = es_code.indexOf('<es:time>')
		pos_5 = es_code.indexOf('<es:type>', pos_4+9)>=0?es_code.indexOf('<es:type>', pos_4+9):es_code.length()
		
		row = [
			type : es_code.substring(pos_0+9, pos_1),
			offset : es_code.substring(pos_1+11, pos_2),
			string : StringEscapeUtils.unescapeXml( es_code.substring(pos_2+11, pos_3) ),
			length : es_code.substring(pos_3+11, pos_4),
			time : es_code.substring(pos_4+9, pos_5)
		]
		
		es_code = es_code.substring(pos_5, es_code.length())
		
		//row.string = row.string.replace("\\", "\\\\'").replace("'", "\\'").replace("\"", "\\\"").replace("\r\n", "\\n").replace("\n", "\\n").replace("\r", "\\n")
		row.string = StringEscapeUtils.escapeJavaScript( row.string )
		jscode += "init('${row.type}', '${row.offset}', '${row.string}', '${row.length}', '${row.time}');\n"
	}
}

sql.close()

//日期格式設定
def sdf = new SimpleDateFormat('yyyy/MM/dd hh:mm:ss')

html.html {
	head {
		title ('PLWeb - Keystroke Player')
		script(src:'keystroke_player.js', '')
		if (msg) {
			script(jscode)
		}
	}
	body {
		table (cellspacing: 5) {
			tr {
				td (valign: 'top') {
					ul {
						msg_list.each {
							msg ->
							li {
								href_player = sprintf('keystroke_player.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s&msg=%s',
									class_id,
									course_id,
									lesson_id,
									task_id,
									URLEncoder.encode(user_id),
									msg[0]
								)
								a (href: href_player) {
									span(sdf.format(new Date(msg[1].toLong())))
								}
							}
						}
					}
				}
				td {
					if (msg) {
						h3 ('Source Code')
						if (code) {
							textarea(style:'width:640px;height:240px', code)
						}
						
//						h3 ('Messages')
//						if (msg_out) {
//							textarea(style:'width:640px;height:240px', msg_out)
//						}
						
						
						h3 ('Keystrokes')
						
						textarea(id:'buf','', style:'width:640px;height:480px')
						
						div {
							span('Frame: ')
							input(id: 'idxnum', value: 0, size: 2)
							button(onclick:'show()', 'Play');
							button(onclick:'reset()', 'Reset');
						}
					}
				}
			}
		}
	}
}
