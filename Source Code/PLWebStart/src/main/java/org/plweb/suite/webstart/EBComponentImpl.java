package org.plweb.suite.webstart;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class EBComponentImpl implements EBComponent {

	private boolean menubarVisible = false;

	public boolean isMenubarVisible() {
		return menubarVisible;
	}

	public void setMenubarVisible(boolean menubarVisible) {
		this.menubarVisible = menubarVisible;
	}

	public void handleMessage(EBMessage msg) {
		if (msg instanceof ViewUpdate) {
			ViewUpdate vu = (ViewUpdate) msg;
			if (vu.getWhat().equals(ViewUpdate.CREATED)) {
				configView(vu.getView());
			}
		} else if (msg instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) msg;
			if (epu.getWhat() == EditPaneUpdate.CREATED) {
				configEditPane(epu.getEditPane());
			}
		}
	}

	private void configView(View view) {
		
		// disable jEdit MenuBar
		if (!menubarVisible) {
			view.setJMenuBar(null);
		}
		((JPanel) view.getContentPane()).setBorder(BorderFactory
				.createEmptyBorder(6, 6, 3, 3));
		
		//view.getToolBar()
		//		.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
		//GUIUtilities.centerOnScreen(view);
	}

	private void configEditPane(EditPane editPane) {
		JEditTextArea textArea = editPane.getTextArea();
		JPopupMenu pop = textArea.getRightClickPopup();
		pop.remove(pop.getComponentCount() - 1);
		pop.remove(pop.getComponentCount() - 1);
	}
}
