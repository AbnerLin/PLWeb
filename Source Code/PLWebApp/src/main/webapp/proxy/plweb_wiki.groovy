import org.plweb.webapp.helper.CommonHelper
import org.cyberneko.html.parsers.SAXParser
import org.xml.sax.InputSource
import javax.xml.transform.TransformerFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

helper = new CommonHelper(request, response, session)

title   = helper.fetch('title')
sections = helper.fetch('section', '0')

if (!title) {
	println """
	<p>Error: Require [title] parameter.</p>
"""
	return
}

sections = sections.trim().split(',')

sections.each {
	section->
	
	rootNode = new XmlParser().parse("http://wiki.plweb.org/api.php?action=query&format=xml&titles=${title}&prop=revisions&rvprop=content&rvsection=${section}")
	
	wiki_text = rootNode.query.pages.page.revisions.rev[0].value()[0]
	wiki_text = "__NOTOC__\n${wiki_text}"
	
	def urlobj = new URL('http://wiki.plweb.org/api.php?action=parse&format=xml&absurl')
	def conn = urlobj.openConnection()
	conn.setRequestProperty('User-Agent', 'Mozilla/5.0')
	conn.setRequestMethod 'POST'
	conn.doOutput = true
	
	def writer = new OutputStreamWriter(conn.outputStream)
	writer.write 'text='
	writer.write URLEncoder.encode(wiki_text)
	writer.flush()
	writer.close()
	
	conn.connect()
	
	rootNode = new XmlParser().parseText(conn.content.text)
	print rootNode.parse.text[0].value()[0]
	
//	InputSource is = new InputSource();
//	is.setCharacterStream(new StringReader(rootNode.parse.text[0].value()[0]));
//	
//	def parser = new SAXParser()
//	parser.setFeature('http://xml.org/sax/features/namespaces', false)
//	page = new XmlParser(parser).parse(is)
//	
//	w = new StringWriter()
//	new XmlNodePrinter(new PrintWriter(w)).print(page)
//	w.toString()
}