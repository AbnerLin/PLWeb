package org.plweb.jedit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.TransferHandler;


public class TextFieldHanlder extends TransferHandler {
	
	protected String exportString(JComponent c){
		JList list = (JList) c;
		int _rtn = list.getSelectedIndex();
		return String.valueOf(_rtn + 1);
	}
	
	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(exportString(c));
	}


	public void importString(JComponent comp, String str) {
		JTextField target = (JTextField) comp;
		
		if(target.getText().equals("") || target.getText().endsWith(", ")) {
			target.setText(target.getText() + str);
		} else
			target.setText(target.getText() + ", " + str);
		
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String str = (String)t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);
                return true;
            } catch (UnsupportedFlavorException ufe) {
            } catch (IOException ioe) {
            }
        }
        return false;
    }

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (DataFlavor.stringFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
	
}
