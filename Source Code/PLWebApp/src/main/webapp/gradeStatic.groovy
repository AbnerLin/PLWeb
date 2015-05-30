import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.text.SimpleDateFormat

helper = new CommonHelper(request, response, session)

class_id = request.getParameter('class_id')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

query1 = """ SELECT DISTINCT QUESTION_ID FROM ST_REPORTS WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? ORDER BY QUESTION_ID"""

query2 = """ SELECT C.ENROLLMENT, A.USER_ID, B.TIME_CREATED, B.TIME_UPDATED, B.TIME_USED, B.TEST_OK FROM ST_USER AS C, USER_CLASS AS A LEFT JOIN (SELECT USER_ID, TIME_CREATED, TIME_UPDATED, TIME_USED, TEST_OK FROM ST_REPORTS WHERE CLASS_ID=? AND LESSON_ID=? AND QUESTION_ID=?) AS B ON A.USER_ID = B.USER_ID WHERE A.CLASS_ID=? AND A.USER_ID !=800 AND C.USER_ID=A.USER_ID ORDER BY C.ENROLLMENT """

count = 1;

print "<html>"
print "<head>"
print "<style type=\"text/css\">"
print """
table, td, tr, th{
        border : 1px solid black;
        text-align: center;
}
table {
        float: left;
        margin: 5px;
        margin-top: 10px;
}
caption{
        color: red;
}
div{
        width: 100%;
		float: left;
}

"""
print "</style>"
print "</head>"
print "<body>null = 未作答<div>"

sql.eachRow(query1, [class_id, course_id, lesson_id]) {
        //print it.QUESTION_ID
        question_id = it.QUESTION_ID
        print "<table> <caption><h3>第" + count++ + "題</h3></caption>"
        print "<tr><td>學生代號</td><td>學號</td><td>起始時間</td><td>提交時間</td><td>1=答對, 0=答錯</td></tr>"
        sql.eachRow(query2, [class_id, lesson_id, question_id, class_id]) {
                tmp = it.TIME_CREATED
                tmp2 = it.TIME_UPDATED
                tmpResult = ""
                if(tmp == null) {
                        tmpResult = "null"
                        tmpResult2 = "null"
                        tmpUsed = "null"
                } else {
                        date = new Date(Long.parseLong(tmp.toString()))
                        format = new SimpleDateFormat("yyyy MM dd HH:mm:ss")
                        tmpResult = format.format(date)

                        date = new Date(Long.parseLong(tmp2.toString()))
                        format = new SimpleDateFormat("yyyy MM dd HH:mm:ss")
                        tmpResult2 = format.format(date)
						tmpUsed = it.TIME_USED/1000
                }
                print "<tr><td>" + it.USER_ID + "</td><td>" + it.ENROLLMENT + "</td><td>" + tmpResult + "</td><td>" + tmpResult2 + "</td><td>" + tmpUsed + "</td><td>" + it.TEST_OK + "</td><tr>"
        }
        print "</table>"
}
print "</body></div>"
print "</html>"