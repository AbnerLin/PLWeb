import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendError 403
	return
}

def uid = helper.sess('uid')

def class_id = helper.fetch('class_id')

_SQL_LINK_USER_CLASS_ = """
insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
values (?, ?, ?)
"""

_SQL_CHECK_CLASS_ = """
select count(1) as cc from ST_CLASS where CLASS_ID=?
"""

_SQL_CHECK_LINK_ = '''
select count(1) as cc from USER_CLASS where CLASS_ID=? and USER_ID=?
'''

def sql = new Sql(helper.connection)

try {
	if (class_id != null) {
		def cc1 = sql.firstRow(_SQL_CHECK_CLASS_, [class_id]).cc
		def cc2 = sql.firstRow(_SQL_CHECK_LINK_, [class_id, uid]).cc
		
		if (cc1 == 0) {
			helper.sess 'error_message', '課程代碼不存在！'
		}
		else if (cc2 > 0) {
			helper.sess 'error_message', '無法重複選修此課程！'
		}
		else {
			sql.execute(_SQL_LINK_USER_CLASS_, [uid, class_id, 'n'])
			helper.sess 'alert_message', "您已經加選 ${class_id}完成，返回我的課程頁面請記得重新整理。"			
		}
	}
	else {
		helper.sess 'error_message', '未輸入課程代碼！'
	}
}
catch (e) {
	helper.sess 'error_message', e.message
}

helper.redirect 'index.groovy'
