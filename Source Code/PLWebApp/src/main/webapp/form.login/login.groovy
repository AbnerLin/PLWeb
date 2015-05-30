import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)
/*
helper.attr('htmlhead', helper.htmlhead('../'))

helper.attr('helper', helper)
helper.attr('login_error', helper.sess('login_error'))

helper.forward('login.gsp')
*/

helper.redirect '/'