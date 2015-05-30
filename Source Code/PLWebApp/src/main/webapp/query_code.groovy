import org.plweb.suite.common.xml.XmlFactory
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.text.SimpleDateFormat

class_id = request.getParameter('class_id')
lesson_id = request.getParameter('lesson_id')
course_id = request.getParameter('course_id')
user_id = request.getParameter('user_id')
task_id = request.getParameter('task_id')

msg = request.getParameter('msg')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

q_code = """
select CODE from ST_CODE
where CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
and QUESTION_ID=?
and USER_ID=?
"""

q_msg = """
select ES, CODE, MSG
from ST_MESSAGE
where MESSAGE_ID=?
"""

q_menu = """
select MESSAGE_ID, TIME_SEQ from ST_MESSAGE
where CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
and QUESTION_ID=?
and USER_ID=?
order by TIME_SEQ
"""

//日期格式設定
sdf = new SimpleDateFormat('yyyy/MM/dd hh:mm:ss')

msg_list = []
sql.eachRow(q_menu, [class_id, course_id, lesson_id, task_id, user_id]) {
	msg_list << [it.message_id, it.time_seq]
}

if (msg) {
	row = sql.firstRow(q_msg, [msg])
	
	code = row.code
	msg_out = row.msg
	
	jscode = ''
	es_code = row.es
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
			string : es_code.substring(pos_2+11, pos_3),
			length : es_code.substring(pos_3+11, pos_4),
			time : es_code.substring(pos_4+9, pos_5)
		]
		
		es_code = es_code.substring(pos_5, es_code.length())
		
		row.string = row.string.replace("\\", "\\\\'").replace("'", "\\'").replace("\"", "\\\"").replace("\r\n", "\\n").replace("\n", "\\n").replace("\r", "\\n")
		jscode += "init('${row.type}', '${row.offset}', '${row.string}', '${row.length}', '${row.time}');\n"
	}	
}

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
						li {
							href_player = sprintf('query_code.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s',
									class_id,
									course_id,
									lesson_id,
									task_id,
									URLEncoder.encode(user_id)
								)
								a (href: href_player, "完成程式碼")
						}
						c = 0
						msg_list.each {
							msg ->
							c++
							li {
								href_player = sprintf('query_code.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s&msg=%s',
									class_id,
									course_id,
									lesson_id,
									task_id,
									URLEncoder.encode(user_id),
									msg[0]
								)
								a (href: href_player) {
									datestr = sdf.format(new Date(msg[1].toLong()))
									span("紀錄${c} (${datestr})")
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
						
						h3 ('Messages')
						if (msg_out) {
							textarea(style:'width:640px;height:240px', msg_out)
						}
						
						/*
						h3 ('Keystrokes')
						
						textarea(id:'buf','', style:'width:640px;height:480px')
						
						div {
							span('Frame: ')
							input(id: 'idxnum', value: 0, size: 2)
							button(onclick:'show()', 'Play');
							button(onclick:'reset()', 'Reset');
						}
						*/
					}
					else {
						h3 ('Source Code')
						
						row = sql.firstRow(q_code, [class_id, course_id, lesson_id, task_id, user_id])
						textarea (style:'width:640px;height:480px', row.code)
					}
				}
			}
		}
	}
}

sql.close()