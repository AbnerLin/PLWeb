package org.plweb.jedit;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.Font;

public class JMultiTextViewer extends JTabbedPane {

	private static final long serialVersionUID = 515087633114228581L;
	private JTextArea txa;
	private int txaFontSize = 12;
	
	public void clear() {
		this.removeAll();
	}
	
	public void showText(String title, String content) {
		this.txa = new JTextArea(content);
		this.txa.setFont(new Font(txa.getFont().getFontName(), txa.getFont().getStyle(), txaFontSize));
		this.add(title, new JScrollPane(this.txa));
	}
		
	public void editFontSize(int val){
		txaFontSize += 2 * val;
		this.txa.setFont(new Font(txa.getFont().getFontName(), txa.getFont().getStyle(), txaFontSize));
	}
	
}
