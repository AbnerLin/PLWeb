package org.plweb.suite.common.xml;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class PLStdXmlTransformer {

	public String getXmlString() {
		StringWriter sw = new StringWriter();
		transform(sw);
		return sw.toString();
	}

	protected DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory factory;
		factory = DocumentBuilderFactory.newInstance();
		factory.setCoalescing(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		return factory.newDocumentBuilder();
	}

	protected void transform(Writer writer) {
		try {
			DocumentBuilder db = getDocumentBuilder();
			Document doc = db.newDocument();

			writeRootNode(doc);

			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			// tf.setAttribute("indent-number", new Integer(4));
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "xml");
			serializer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");

			serializer.transform(domSource, streamResult);
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	protected static Element makeNode(Document doc, String tag, String value) {
		Element elm = doc.createElement(tag);
		elm.setTextContent(value);
		return elm;
	}

	protected abstract void writeRootNode(Document doc);
}
