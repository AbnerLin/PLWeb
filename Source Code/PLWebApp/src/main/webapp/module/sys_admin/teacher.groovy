import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)
sql = new Sql(helper.connection)

helper.attr 'rows', sql.rows("""
select *
from ST_USER
where TYPE='T'
order by LAST_UPDATE desc
""")

helper.attr 'helper', helper
helper.include 'teacher.gsp'

sql.close()