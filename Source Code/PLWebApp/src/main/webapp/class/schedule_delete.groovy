import org.plweb.webapp.helper.CommonHelper
import groovy.sql.Sql
import javax.naming.InitialContext

def helper = new CommonHelper(request, response, session)
def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

def utype = helper.sess('utype')
if(!utype.equals("T")){
        println """ �v�������A�Э��s<a href=\"${helper.basehref}login/\">�n�J</a>�C """
        return
}

class_id = request.getParameter("class_id")
course_id = request.getParameter("course_id")
lesson_id = request.getParameter("lesson_id")

deleteQuery = """ delete from CLASS_COURSE where CLASS_ID=? and COURSE_ID=? and LESSON_ID=? """

sql.execute(deleteQuery, [class_id, course_id, lesson_id])


response.sendRedirect("schedule.groovy?id=${class_id}")
