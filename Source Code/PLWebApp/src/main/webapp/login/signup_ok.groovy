import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

_QUERY_USER_COUNT = """
select count(USER_ID) as cc from ST_USER
"""

sql = new Sql(helper.connection)

usercount = 0
try {
    row = sql.firstRow(_QUERY_USER_COUNT)
    usercount = row?new DecimalFormat('#,###').format(row.cc):'0'
}
catch (e) {
}

sql.close()

helper.attr 'usercount', usercount

helper.include 'signup_ok.gsp'