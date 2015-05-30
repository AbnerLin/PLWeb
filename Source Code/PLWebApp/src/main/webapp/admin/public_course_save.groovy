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

content = helper.fetch('list')

querySelect = """ SELECT COUNT(*) AS A FROM PUBLIC_COURSE """
queryInsert = """ INSERT INTO PUBLIC_COURSE VALUES(?)"""
queryUpdate = """ UPDATE PUBLIC_COURSE SET content=? """

row = sql.firstRow(querySelect)
if(row.A == 0)
    sql.executeInsert(queryInsert, [content])
else
    sql.executeUpdate(queryUpdate, [content])


response.sendRedirect('update_public_course.groovy')
