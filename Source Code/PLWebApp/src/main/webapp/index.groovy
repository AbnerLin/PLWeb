import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

helper.forward 'login/index.groovy'