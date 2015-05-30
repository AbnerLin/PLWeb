package org.plweb.suite.webstart;

import javax.swing.DefaultListModel;

class MessageListModel extends DefaultListModel {
	private static final long serialVersionUID = -4987910955258369973L;

	public synchronized Object getElementAt(int index) {
		int size = getSize() - 1;
		if (index >= size) {
			index = size;
		}
		return elementAt(index);
	}
}