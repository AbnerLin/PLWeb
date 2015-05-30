import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response)
println "123";
helper.forward 'dashboard.gsp'