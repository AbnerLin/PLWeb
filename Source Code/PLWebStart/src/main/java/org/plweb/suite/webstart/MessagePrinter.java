package org.plweb.suite.webstart;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JList;

class MessagePrinter {
	private ResourceBundle rb = ResourceBundle.getBundle("Messages");
	
	//private JList listComp;
	private DefaultListModel listModel;

	private List<String> buffer = new ArrayList<String>();
	
	public MessagePrinter(DefaultListModel listModel, JList listComp) {
		this.listModel = listModel;
		//this.listComp = listComp;
	}

	public void println(String text) {
		buffer.add(text);
		flush();
	}
	
	private String bufferToString() {
		StringBuffer buf = new StringBuffer();
		for (String item : buffer) {
			buf.append(item);
		}
		buffer.clear();
		return buf.toString();
	}
	
	public void flush() {
		listModel.addElement(bufferToString());	
	}
	
	public void render(String stringId) {
		println(rb.getString(stringId));
	}
	
	public void backward() {
		//listComp.clearSelection();
		listModel.remove(listModel.getSize() - 1);		
	}
	
	@Deprecated
	public void update(String text) {
		//listComp.clearSelection();
		listModel.remove(listModel.getSize() - 1);
		listModel.addElement(text);
	}
}