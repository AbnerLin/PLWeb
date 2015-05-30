import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringEscapeUtils
import org.plweb.webapp.helper.CommonHelper
import org.plweb.suite.common.xml.XmlFactory

def helper = new CommonHelper(request, response, session)

def uid = helper.sess('uid')

if (!uid) {
	response.sendError 403
	return
}

def course_id = helper.fetch('course_id')
def lesson_id = helper.fetch('lesson_id')

def sql = new Sql(helper.connection)

text_xml = sql.firstRow("""
	select TEXT_XML
	from COURSE_FILE
	where COURSE_ID=?
	and LESSON_ID=?
""", [course_id, lesson_id]).TEXT_XML

is = new ByteArrayInputStream(text_xml.getBytes("UTF-8"))
project = XmlFactory.readProject(is)

files = []

project.tasks.each {
	task ->
	file_name = project.getTaskPropertyEx(task, 'file.main')
	file_main = project.getFile(file_name)
	
	files << [
		name: file_name,
		content: new String(file_main.decodedContent, 'UTF-8')
//		content: StringEscapeUtils.escapeHtml(new String(file_main.decodedContent, 'UTF-8'))
	]
}

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title ('觀看解答 - PLWeb')
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')

		link (href: "http://google-code-prettify.googlecode.com/svn/trunk/src/prettify.css", type: "text/css", rel: "stylesheet")
		script (type: "text/javascript", src: "http://google-code-prettify.googlecode.com/svn/trunk/src/prettify.js")
	}
	body (class: 'admin-layout', onload: "prettyPrint()") {
		h1 ('觀看解答')

		files.each {
			file->

			h3 (file.name)

			def className = "prettyprint"

			if (file.name?.endsWith('.java')) {
				className += " lang-java"
			}

			pre (class: className, style: 'line-height:1.5em', file.content)
		}
	}
}

/*
<%helper=request.get('helper')%>
<%helper.attr('files').each{file->%>
<div>${file.name}</div>
<div class="prettyhtml"><pre>${file.content}</pre></div>
<%}%>
*/
//helper.forward 'ajax_load_answer.gsp'

