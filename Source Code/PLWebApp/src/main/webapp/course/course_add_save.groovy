import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

if (!session) {
	response.sendError 403
	return
}

def uid = session.getAttribute('uid')

def name  = helper.fetch('name')
def title = helper.fetch('title')

if (name==null || name=='') {
	helper.sess 'error_message', "必須填寫教材代碼"
	helper.forward 'course_add.groovy'
	return
}

if (title==null || title=='') {
	helper.sess 'error_message', "必須填寫教材名稱"
	helper.forward 'course_add.groovy'
	return
}

def sql = new Sql(helper.connection)

try {
	//檢查是否曾經建立
	cc = sql.firstRow('select count(*) as cc from COURSE where COURSE_NAME=?', [name]).cc
	
	if (cc > 0) {
		helper.sess 'error_message', "指定的教材代碼已經存在"
		helper.forward 'course_add.groovy'
		return
	}
	
	//插入資料
	newId = sql.firstRow('select max(COURSE_ID)+1 as newid from COURSE').newid

	if (!newId) {
		newId = 1
	}
	
	sql.execute("insert into COURSE (COURSE_ID, COURSE_NAME, COURSE_TITLE) values (?,?,?)", [newId, name, title])

	sql.execute('insert into USER_COURSE(COURSE_ID, USER_ID, IS_OWNER) values (?,?,?)', [newId, uid, 'y'])
	
	helper.sess 'alert_message', '教材已新增完成'
	
	//回列表
	response.sendRedirect('index.groovy')
	
}
catch (e) {
	helper.sess 'error_message', "${e.message}"
    helper.forward 'course_add.groovy'
}
