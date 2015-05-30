import groovy.sql.Sql
import java.text.DecimalFormat
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

_QUERY_ACCOUNT = """
select NAME, EMAIL, ENROLLMENT
from ST_USER
where NAME=? or ENROLLMENT=?
"""

name       = helper.fetch('name')
enrollment = helper.fetch('enrollment')

sql = new Sql(helper.connection)

accounts = []
try {
    if (name != null || enrollment != null) {
        sql.eachRow(_QUERY_ACCOUNT, [name, enrollment]) {
            row->
            accounts << [
                email: row.EMAIL.replace('@', ' * '),
                name: row.NAME,
                enrollment: row.ENROLLMENT
            ]
        }
    }
}
catch(e) {
    println e.message
}

sql.close()

helper.attr 'accounts', accounts

helper.include 'account.gsp'