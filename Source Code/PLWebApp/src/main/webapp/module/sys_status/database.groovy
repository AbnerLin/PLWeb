import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)

helper.attr 'helper', helper
helper.attr 'ds', new InitialContext().lookup('java:comp/env/jdbc/plweb')
helper.include 'database.gsp'