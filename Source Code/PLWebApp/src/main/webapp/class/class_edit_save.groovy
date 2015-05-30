import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def sql = new Sql(helper.connection)

def id				= helper.fetch('id')
def name			= helper.fetch('name')
def school			= helper.fetch('school')
def department		= helper.fetch('department')
def password		= helper.fetch('password')
def displayOnMenu	= helper.fetch('displayOnMenu')=='y'?'y':'n'

def __UPDATE_SQL__ = """
update ST_CLASS
set CLASS_NAME=?,
SCHOOL=?,
DEPARTMENT=?,
PASSWORD=?,
DISPLAY_ON_MENU=?
where CLASS_ID=?
"""

try {
	sql.executeUpdate(__UPDATE_SQL__, [name, school, department, password, displayOnMenu, id])
	session.setAttribute('alert_message', '已經更新一筆課程設定')
}
catch (e) {
	session.setAttribute('error_message', e.message)
}

response.sendRedirect('index.groovy')

