import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)
query1 = 'select count(USER_ID) as USER_COUNT from ST_USER'

row = sql.firstRow(query1)

user_count = row?new DecimalFormat('#,###').format(row.USER_COUNT):'0'

sql.close()

helper.attr('helper', helper)
helper.attr('htmlhead', helper.htmlhead('../'))
helper.attr('user_count', user_count)
helper.attr('signup_errmsg', helper.sess('signup_errmsg'))

helper.forward('signup.gsp')