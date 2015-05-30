import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

print "123";
helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)
row = sql.firstRow('select count(USER_ID) as USER_COUNT from ST_USER')
user_count = row?new DecimalFormat('#,###').format(row.USER_COUNT):'0'
sql.close()

helper.attr 'helper', helper
helper.attr 'user_count', user_count

helper.forward 'signup.gsp'