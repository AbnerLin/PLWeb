package org.plweb.jedit;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.gjt.sp.util.Log;

public class MessageConsole implements MessageConsoleInterface {
	private JTextPane textPane;

	private Map<Integer, Document> documents = new HashMap<Integer, Document>();;
	// private String currentBufferName;

	private static MessageConsole instance = null;

	public static MessageConsole getInstance() {
		if (instance == null) {
			return instance = new MessageConsole();
		} else {
			return instance;
		}
	}
	
	public MessageConsole() {
		
	}

	public MessageConsole(JLabel labelStatus, JTextPane textPaneConsole) {
		this.textPane = textPaneConsole;
	}

	public void switchTo(int idx) {
		if (!documents.containsKey(idx)) {
			Document document = new DefaultStyledDocument();
			init(document);
			documents.put(idx, document);
		}
		textPane.setDocument(documents.get(idx));
	}

	public void print(String text, Color color) {
		int currLength = textPane.getDocument().getLength();

		try {
			SimpleAttributeSet attributeSet = new SimpleAttributeSet();
			StyleConstants.setForeground(attributeSet, color);
			textPane.getDocument().insertString(currLength, text, attributeSet);
			textPane.setCaretPosition(textPane.getDocument().getLength());
		} catch (Exception ex) {
			Log.log(Log.ERROR, this, ex);
		}
	}

	public void print(String text) {
		print(text, Color.black);
	}

	public void println(String text) {
		print(text + System.getProperty("line.separator"));
	}

	public void println(String text, Color color) {
		print(text + System.getProperty("line.separator"), color);
	}

	public String getAllText() {
		try {
			return textPane.getDocument().getText(0,
					textPane.getDocument().getLength());
		} catch (Exception ex) {
			Log.log(Log.ERROR, this, ex);
		}
		return null;
	}

	public void init() {
		init(textPane.getDocument());
	}

	private void init(Document document) {
		try {
			document.remove(0, document.getLength());
			document.insertString(0, "\n", null);
			document.insertString(0, "> ", null);
		} catch (BadLocationException ex) {
			Log.log(Log.ERROR, this, ex);
		}
	}

	public JTextPane getTextPaneConsole() {
		return textPane;
	}

	public void setTextPaneConsole(JTextPane textPaneConsole) {
		this.textPane = textPaneConsole;
	}
}
