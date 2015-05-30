import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.text.SimpleDateFormat

sdf = new SimpleDateFormat('yyyy/MM/dd HH:mm:ss')

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid   = session.get('uid')
uname = session.get('uname')
utype = session.get('utype')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

query1 = """
update ST_USER
set LAST_UPDATE=?,
    LAST_IP=?,
    LAST_HOST=?,
    LAST_SESSION=?,
    LAST_AGENT=?,
    IS_LOGIN='y'
where USER_ID=?
"""

query2 = """
select COUNT(*) as USER_COUNT
from ST_USER
where LAST_UPDATE >= ?
and IS_LOGIN='y'
"""

query3 = """
select NAME, TYPE, 
(select CLASS_NAME from ST_CLASS where CLASS_ID=ST_USER.LAST_CLASS) AS CLASS_NAME
from ST_USER
where LAST_UPDATE >= ?
and IS_LOGIN='y'
"""

sql.executeUpdate(query1, [new Date().time, request.remoteAddr, request.remoteHost, session.id, request.getHeader("User-Agent"), uid])

user_count = sql.firstRow(query2, [new Date().time-(300*1000)]).user_count

html.setDoubleQuotes(true)
html.html {
	head {
		meta ('http-equiv': 'refresh', content: 60)
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body (style: 'margin:0;padding:0;overflow:hidden') {
		div (align: 'center') {
			select {
				
				user_text = user_count>1?'users':'user'
				
				option ("${user_count} ${user_text} online ("+sdf.format(new Date())+")")
				sql.eachRow(query3, [new Date().time-(180*1000)]) {
					row->
					types = new HashMap()
					types.put('T', ' (Teacher)')
					types.put('M', ' (Administrator)')
					types.put('S', '')
					class_name = row.class_name?" / ${row.class_name}":''
					option ("${row.name}${class_name}${types[row.type]}")
				}
			}
			
			marquee (width: 400, scrolldelay: 150, class: 'announce') {
				sql.eachRow('select * from ST_ANNOUNCE order by ANN_DATE') {
					row->
					font (color: "#${row.ann_color}", row.ann_text)
				}
			}
		}
	}
}

sql.close()