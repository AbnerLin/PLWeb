import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

email = helper.fetch('email')

query1 = """
select COUNT(*) as USER_COUNT
from ST_USER
where LAST_UPDATE >= ?
and IS_LOGIN='y'
"""

sql = new Sql(helper.connection)

usercount = sql.firstRow(query1, [new Date().time-(300*1000)]).USER_COUNT

sql.close()

helper.attr('usercount', usercount)

helper.include('left_menu.gsp')