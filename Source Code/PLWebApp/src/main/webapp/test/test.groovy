import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat
import net.tanesha.recaptcha.ReCaptchaImpl
import net.tanesha.recaptcha.ReCaptchaResponse
import java.util.Calendar

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

_SQL = """
select TICKET_DATE from WS_TICKET where CLASS_ID in (2011100003, 2011100004, 2011100005) order by TICKET_DATE
"""

sql.eachRow(_SQL) {
	row->
	
	cal = Calendar.getInstance();
	cal.setTime(new Date(row.TICKET_DATE.toLong()));
	t = cal.get(Calendar.WEEK_IN_MONTH);
	println "${t}<br/>"
}
sql.close()