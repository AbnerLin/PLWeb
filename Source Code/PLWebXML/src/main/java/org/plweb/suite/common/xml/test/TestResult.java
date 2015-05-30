package org.plweb.suite.common.xml.test;

import java.util.ArrayList;
import java.util.List;

import org.plweb.suite.common.xml.PLStdXmlTransformer;
import org.plweb.suite.common.xml.PLXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestResult extends PLStdXmlTransformer implements
		PLXmlSerializable {

	protected String mode;
	protected List<Result> results = new ArrayList<Result>();

	public TestResult(String mode) {
		super();
		this.mode = mode;
	}

	public void addResult(Result result) {
		results.add(result);
	}

	@Override
	protected void writeRootNode(Document doc) {
		Element elm = doc.createElement("test-result");

		writeEnvNode(doc, elm);
		writeResultNodes(doc, elm);

		doc.appendChild(elm);
	}

	protected void writeEnvNode(Document doc, Element root) {
		Element elm = doc.createElement("env");

		elm.appendChild(makeNode(doc, "mode", mode));

		writeOsNode(doc, elm);

		root.appendChild(elm);
	}

	protected void writeOsNode(Document doc, Element root) {
		Element elm = doc.createElement("os");

		elm.appendChild(makeNode(doc, "name", System.getProperty("os.name")));
		elm.appendChild(makeNode(doc, "version",
				System.getProperty("os.version")));

		root.appendChild(elm);
	}

	protected void writeResultNodes(Document doc, Element root) {
		for (Result result : results) {

			Element elm = doc.createElement("result");
			elm.appendChild(makeNode(doc, "os", result.getOs()));
			elm.appendChild(makeNode(doc, "type", result.getType()));
			elm.appendChild(makeNode(doc, "cmd", result.getCmd()));
			elm.appendChild(makeNode(doc, "exit", result.getExit()));
			elm.appendChild(makeNode(doc, "status", result.getStatus()));
			elm.appendChild(makeNode(doc, "stdout", result.getStdout()));
			elm.appendChild(makeNode(doc, "stderr", result.getStderr()));

			root.appendChild(elm);
		}
	}
}
