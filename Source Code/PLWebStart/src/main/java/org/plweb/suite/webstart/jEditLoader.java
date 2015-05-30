package org.plweb.suite.webstart;

import java.awt.Font;
import java.io.File;
import java.net.URL;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;

/**
 * Loading jEdit
 * 
 * @author Yan-hong Lin
 * @see http://www.jedit.org/users-guide/cli-usage.html
 */
public class jEditLoader {

	private File settingsPath;
	private File workspacePath;

	private boolean menubarVisible;

	public void load() {

		//File fileIcons = new File(settingsPath, "icons");
		//System.err.println(fileIcons.toURI().toString());
        //GUIUtilities.setIconPath(fileIcons.toURI().toString());

		System.setProperty("jedit.home", workspacePath.getPath());

		String args[] = { 
            "-settings=" + settingsPath.getPath(),
            "-noserver",
            "-nobackground",
            "-gui",
            "-norestore",
            "-nostartupscripts",
            "-nosplash"
        };

		jEdit.main(args);
		
		// Load DroidSansMono for Mac OS X only
		if (System.getProperty("os.name").startsWith("Mac")) {
			try {
				URL fonturl = this.getClass().getResource("DroidSansMono.ttf");
				if (fonturl!=null) {
					Font font = Font.createFont(Font.TRUETYPE_FONT, fonturl.openStream());
					font.deriveFont(Font.PLAIN, 16);
					jEdit.setFontProperty("view.font", font);
					jEdit.setIntegerProperty("view.fontsize", 16);
					jEdit.setBooleanProperty("view.fracFontMetrics", true);
					jEdit.setProperty("view.antiAlias", "standard");
				}
			}
			catch (Exception ex) {
				System.err.println("Could not load DroidSansMono.");
				ex.printStackTrace();
			}
		}
		
		EBComponentImpl ebcomp = new EBComponentImpl();
		
		ebcomp.setMenubarVisible(menubarVisible);

		EditBus.addToBus(ebcomp);
	}

	public File getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(File workspacePath) {
		this.workspacePath = workspacePath;
	}

	public File getSettingsPath() {
		return settingsPath;
	}

	public void setSettingsPath(File settingsPath) {
		this.settingsPath = settingsPath;
	}

	public boolean isMenubarVisible() {
		return menubarVisible;
	}

	public void setMenubarVisible(boolean menubarVisible) {
		this.menubarVisible = menubarVisible;
	}
}
