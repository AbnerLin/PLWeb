import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)

helper.attr('helper', helper)

helper.forward 'login.gsp'