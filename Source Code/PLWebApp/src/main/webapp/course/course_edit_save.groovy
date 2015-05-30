import groovy.sql.Sql
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def uid = session.getAttribute('uid')

def id    = helper.fetch('id')
def name  = helper.fetch('name')
def title = helper.fetch('title')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.getConnection())

try {
	//更新資料
	sql.executeUpdate("update COURSE set COURSE_NAME=?, COURSE_TITLE=? where COURSE_ID=?", [name, title, id])
	
	session.setAttribute('alert_message', 'Book updated.');	
	
	//回列表
	response.sendRedirect('index.groovy')
	
}
catch (e) {
	session.setAttribute('error_message', e.message)
	
	//發生錯誤, 繼續編輯
    def rd = request.getRequestDispatcher('course_edit.groovy')
    rd.forward(request, response)
}

sql.close()
