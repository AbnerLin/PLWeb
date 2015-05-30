import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

helper.attr 'helper', helper
helper.include 'test_loop.gsp'
