import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

helper.include 'password_ok.gsp'