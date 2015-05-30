import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

helper.forward 'CFInstall.gsp'
