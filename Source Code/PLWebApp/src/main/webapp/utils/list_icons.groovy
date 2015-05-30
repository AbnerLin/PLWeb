import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

def p = ~/.*\.png/
new File( request.getRealPath('icon-16') ).eachFileMatch(p) {
	f ->
	println """<img src="${helper.basehref}icon-16/${f.name}" title="${f.name}" border="0" alt="" />""";
}
