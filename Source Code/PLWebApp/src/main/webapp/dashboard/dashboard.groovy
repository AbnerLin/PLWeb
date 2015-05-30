import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

//helper.attr('content_url', content_url)

helper.include 'dashboard.gsp'