package org.plweb.suite.common.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlFactory {

	private static DocumentBuilder getDocumentBuilder()
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

	public static XProject readProject(InputStream is) {
		try {
			return readProject(getDocumentBuilder().parse(is));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static XProject readProject(String url) {
		try {
			return readProject(getDocumentBuilder().parse(url));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static XProject readProject(File file) {
		try {
			return readProject(getDocumentBuilder().parse(file));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static XProject readProject(Document doc) {
		NodeList nodes = doc.getChildNodes();
		int nodes_size = nodes.getLength();

		for (int i = 0; i < nodes_size; i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("project")) {
					return readProjectNode(node);
				}
			}
		}

		return null;
	}

	public static void saveProject(XProject project, Writer writer) {
		try {
			DocumentBuilder db = getDocumentBuilder();
			Document doc = db.newDocument();

			writeProjectNode(doc, project);

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

	private static XProject readProjectNode(Node rootNode) {
		XProject lesson = new XProject();

		NodeList nodes = rootNode.getChildNodes();
		int nodes_size = nodes.getLength();

		for (int j = 0; j < nodes_size; j++) {
			Node node = nodes.item(j);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("id")) {
					lesson.setId(node.getTextContent().trim());
				} else if (node.getNodeName().equals("title")) {
					lesson.setTitle(node.getTextContent().trim());
				} else if (node.getNodeName().equals("file")) {
					lesson.addFile(readFileNode(node));
				} else if (node.getNodeName().equals("task")) {
					lesson.addTask(readTaskNode(node));
				} else if (node.getNodeName().equals("property")) {
					String map[] = readPropertyNode(node);
					lesson.addProperty(map[0], map[1]);
				}
			}
		}

		return lesson;
	}

	private static XFile readFileNode(Node rootNode) {
		XFile file = new XFile();

		NodeList nodes = rootNode.getChildNodes();
		int nodes_size = nodes.getLength();

		for (int j = 0; j < nodes_size; j++) {
			Node node = nodes.item(j);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("path")) {
					file.setPath(node.getTextContent().trim());
				} else if (node.getNodeName().equals("content")) {
					file.setEncodedContent(node.getTextContent());
				} else if (node.getNodeName().equals("property")) {
					String map[] = readPropertyNode(node);
					file.addProperty(map[0], map[1]);
				}
			}
		}
		return file;
	}

	private static XTask readTaskNode(Node rootNode) {
		XTask task = new XTask();

		NodeList nodes = rootNode.getChildNodes();
		int nodes_size = nodes.getLength();

		for (int j = 0; j < nodes_size; j++) {
			Node node = nodes.item(j);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("id")) {
					task.setId(node.getTextContent().trim());
				} else if (node.getNodeName().equals("title")) {
					task.setTitle(node.getTextContent());
				} else if (node.getNodeName().equals("command")) {
					task.addCommand(readCommandNode(node));
				} else if (node.getNodeName().equals("property")) {
					String map[] = readPropertyNode(node);
					task.setProperty(map[0], map[1]);
				}
			}
		}

		return task;
	}

	private static XCommand readCommandNode(Node rootNode) {
		XCommand command = new XCommand();

		NodeList nodes = rootNode.getChildNodes();
		int nodes_size = nodes.getLength();

		for (int j = 0; j < nodes_size; j++) {
			Node node = nodes.item(j);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("mode")) {
					command.setMode(node.getTextContent().trim());
				} else if (node.getNodeName().equals("name")) {
					command.setName(node.getTextContent().trim());
				} else if (node.getNodeName().equals("type")) {
					command.setType(node.getTextContent().trim());
				} else if (node.getNodeName().equals("os")) {
					command.setOs(node.getTextContent().trim());
				} else if (node.getNodeName().equals("cmd")) {
					command.setCmd(node.getTextContent().trim());
				} else if (node.getNodeName().equals("stdin-file")) {
					command.setStdinFile(node.getTextContent().trim());
				} else if (node.getNodeName().equals("stdout-file")) {
					command.setStdoutFile(node.getTextContent().trim());
				}
			}
		}

		return command;
	}

	private static String[] readPropertyNode(Node rootNode) {
		String[] map = new String[2];

		NodeList nodes = rootNode.getChildNodes();
		int nodes_size = nodes.getLength();

		for (int j = 0; j < nodes_size; j++) {
			Node node = nodes.item(j);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("key")) {
					map[0] = node.getTextContent().trim();
				} else if (node.getNodeName().equals("value")) {
					map[1] = node.getTextContent().trim();
				}
			}
		}

		return map;
	}

	private static void writeProjectNode(Document doc, XProject project) {
		Element elm = doc.createElement("project");

		elm.appendChild(makeNode(doc, "id", project.getId()));
		elm.appendChild(makeNode(doc, "title", project.getTitle()));

		writePropertyNodes(doc, elm, project.getProperties());
		writeFileNodes(doc, elm, project.getFiles());
		writeTaskNodes(doc, elm, project.getTasks());

		doc.appendChild(elm);
	}

	private static void writePropertyNodes(Document doc, Element rootElm,
			Map<String, String> properties) {
		for (String key : properties.keySet()) {
			String value = properties.get(key);

			Element elm = doc.createElement("property");
			elm.appendChild(makeNode(doc, "key", key));
			elm.appendChild(makeNode(doc, "value", value));

			rootElm.appendChild(elm);
		}
	}

	private static void writeFileNodes(Document doc, Element rootElm,
			List<XFile> files) {
		for (XFile f : files) {
			Element e = doc.createElement("file");
			e.appendChild(makeNode(doc, "path", f.getPath()));
			e.appendChild(makeNode(doc, "content", f.getEncodedContent()));
			writePropertyNodes(doc, e, f.getProperties());
			rootElm.appendChild(e);
		}
	}

	private static void writeTaskNodes(Document doc, Element rootElm,
			List<XTask> tasks) {
		for (XTask task : tasks) {
			Element e = doc.createElement("task");
			e.appendChild(makeNode(doc, "id", task.getId()));
			e.appendChild(makeNode(doc, "title", task.getTitle()));
			writePropertyNodes(doc, e, task.getProperties());
			writeCommandNodes(doc, e, task.getCommands());
			rootElm.appendChild(e);
		}
	}

	private static void writeCommandNodes(Document doc, Element rootElm,
			List<XCommand> commands) {

		for (XCommand c : commands) {
			Element e = doc.createElement("command");

			e.appendChild(makeNode(doc, "mode", c.getMode()));
			e.appendChild(makeNode(doc, "name", c.getName()));
			e.appendChild(makeNode(doc, "type", c.getType()));
			e.appendChild(makeNode(doc, "os", c.getOs()));
			e.appendChild(makeNode(doc, "cmd", c.getCmd()));
			e.appendChild(makeNode(doc, "stdin-file", c.getStdinFile()));
			e.appendChild(makeNode(doc, "stdout-file", c.getStdoutFile()));

			rootElm.appendChild(e);
		}
	}

	private static Element makeNode(Document doc, String tag, String value) {
		Element elm = doc.createElement(tag);
		elm.setTextContent(value);
		return elm;
	}
}
