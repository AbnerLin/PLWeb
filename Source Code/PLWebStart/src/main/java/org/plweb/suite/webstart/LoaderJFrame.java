package org.plweb.suite.webstart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

public class LoaderJFrame extends JFrame {

	private static final long serialVersionUID = 3555732372365461392L;

	private PLWebEnvironment env = PLWebEnvironment.getInstance();

	private String diskRoot = env.getDiskRoot();
	private File jEditHome = new File(diskRoot, env.getJEditPath());

	private MessagePrinter msg;

	public LoaderJFrame() {
		DefaultListModel listModel = new MessageListModel();
		JList listComp = new JList(listModel);
		listComp.setForeground(Color.DARK_GRAY);
		listComp.setBackground(Color.WHITE);
		listComp.setPreferredSize(new Dimension(400, 120));
		listComp.setBorder(BorderFactory.createEmptyBorder());
		listComp.setAutoscrolls(true);
		listComp.setFocusable(false);
		listComp.setVisibleRowCount(100);
		
		msg = new MessagePrinter(listModel, listComp);
		//msg.render("test");
		
		
		JLabel labelComp;
		try {
			labelComp = new JLabel(new ImageIcon(new URL(env.getAdImage())));
		} catch (MalformedURLException ex) {
			labelComp = new JLabel("AD-NOT-FOUND");
		}
		labelComp.setPreferredSize(new Dimension(400, 100));
		setTitle("PLWeb WebStart");
		setLayout(new BorderLayout());
		add(labelComp, BorderLayout.NORTH);
		add(listComp, BorderLayout.CENTER);
		setResizable(false);
		pack();
		setVisible(true);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension f = getSize();
		int x = (d.width - f.width) / 2;
		int y = (d.height - f.height) / 2;
		this.setBounds(x, y, f.width, f.height);
	}

	public void autoload() {
		try {
			downloadJEditPackage();

			downloadJEditPlugins();

			downloadLessonXML();

			startJEditor();

			setVisible(false);

		} catch (Exception ex) {
			msg.println(ex.getMessage());
		}
	}

	private void startJEditor() {
		jEditLoader loader = new jEditLoader();
		loader.setSettingsPath(jEditHome);
		loader.setWorkspacePath(new File(jEditHome, "workspace"));
		loader.setMenubarVisible(false);
		loader.load();
	}

	private void downloadJEditPlugins() throws Exception {
		String pluginPath = env.getPluginPath();
		File tempPlugins = new File(diskRoot);
		File filePlugins = new File(diskRoot, pluginPath);

		String[] pluginsAsc = env.getPluginsAsc();
		int i = 0;

		for (String plugin : env.getPlugins()) {
			URL urlPlugin = new URL(plugin);
			String pluginName = new File(urlPlugin.getFile()).getName();

			File tempPlugin = new File(tempPlugins, pluginName);
			File filePlugin = new File(filePlugins, pluginName);

			boolean downloaded = false;

			if (tempPlugin.exists()) {
				msg.println("Plug-in Checksum [" + pluginName + "]: ");

				String asc = pluginsAsc[i];
				if (asc != null) {
					if (asc.trim().equals(
							Checksum.md5(tempPlugin.getPath()).trim())) {
						msg.update("OK");
						downloaded = true;
					}
				}

				/*if (!downloaded) {
					msg.update("FAILED");
				}*/
			}

			if (!downloaded) {
				msg.println("Plug-in Download [" + pluginName + "]: ");

				HttpDownloaderRunnable run = new HttpDownloaderRunnable(plugin,
						tempPlugin);

				Thread thread = new Thread(run);
				thread.start();

				int n = 0;
				while (thread.isAlive()) {
					msg.update(String.valueOf(run.getLength()) + " bytes");
					Thread.sleep(200);
					n++;
				}

				msg.update(String.valueOf(tempPlugin.length() / 1204)
						+ " Kbytes");
			}

			AntTask.copyFile(tempPlugin, filePlugin);

			i++;
		}
	}

	private void downloadJEditPackage() throws Exception {

		// *** 檢查jEdit是否已經啟動
		File fileJEditLog = new File(jEditHome, "activity.log");
		if (fileJEditLog.exists() && !fileJEditLog.delete()) {
			throw new JEditExistsException();
		}

		// *** 下載jEdit套件
		URL urlPackage = new URL(env.getUrlPackage());
		String packageName = new File(urlPackage.getFile()).getName();
		File filePackage = new File(diskRoot, packageName);

		boolean downloaded = false;

		if (filePackage.exists()) {
			msg.println("Package Checksum [" + packageName + "]: ");

			String asc = env.getUrlPackageAsc();
			if (asc != null) {
				if (asc.trim().equals(
						Checksum.md5(filePackage.getPath()).trim())) {
					msg.update("OK");
					downloaded = true;
				}
			}

			/*if (!downloaded) {
				msg.update("FAILED");
			}*/
		}

		if (!downloaded) {
			msg.println("Package Download [" + packageName + "]: ");

			HttpDownloaderRunnable run;
			run = new HttpDownloaderRunnable(urlPackage, filePackage);

			Thread thread = new Thread(run);
			thread.start();
			
			while (thread.isAlive()) {
				msg.update(String.valueOf(run.getLength()) + " bytes");
				Thread.sleep(200);
			}

			msg.update(String.valueOf(filePackage.length() / 1204) + " Kbytes");
		}

		// *** 解壓縮jEdit
		msg.println("Extracting to ".concat(jEditHome.getPath()));

		AntTask.delDir(jEditHome); // 移除舊資料夾
		AntTask.unzipFile(jEditHome, filePackage); // 解壓縮
	}

	private void downloadLessonXML() throws Exception {
		URL urlLesson = new URL(env.getUrlLesson());
		String lessonPath = env.getLessonPath();
		String lessonFile = env.getLessonFile();
		File fileLesson = new File(diskRoot, lessonPath);
		File fileLessonXml = new File(fileLesson.getParent(), lessonFile);

		msg.println("Download XML [" + lessonFile + "]: ");

		HttpDownloaderRunnable run;
		run = new HttpDownloaderRunnable(urlLesson, fileLessonXml, true);

		Thread thread = new Thread(run);
		thread.start();

		while (thread.isAlive()) {
			msg.update(String.valueOf(run.getLength()) + " bytes");
			Thread.sleep(200);
		}

		msg.update(String.valueOf(fileLessonXml.length() / 1204) + " Kbytes");
	}
}
