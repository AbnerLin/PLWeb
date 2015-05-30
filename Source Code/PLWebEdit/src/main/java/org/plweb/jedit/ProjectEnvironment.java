package org.plweb.jedit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.plweb.suite.common.xml.XProject;
import org.plweb.suite.common.xml.XTask;
import org.plweb.suite.common.xml.XmlFactory;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import org.apache.commons.codec.binary.Base64;

import javax.swing.JEditorPane;

public class ProjectEnvironment {
	private String diskRoot = System.getProperty("javaws.plweb.diskroot");
	private String lessonPath = System.getProperty("javaws.plweb.lessonpath");
	private String lessonXml = System.getProperty("javaws.plweb.lessonxml");
	private String lessonMode = System.getProperty("javaws.plweb.lessonmode");
	private String requestUrl = System.getProperty("javaws.plweb.urlrequest");
	private String explorer = System.getProperty("javaws.plweb.explorer");
	private String shell = System.getProperty("javaws.plweb.shell");
	private String dataUrl = System.getProperty("javaws.plweb.urlDataSetting");
	
	private String department = System.getProperty("javaws.plweb.department");
	private String enrollment = System.getProperty("javaws.plweb.enrollment");
	private String uname = System.getProperty("javaws.plweb.uname");
	private String isExam = System.getProperty("javaws.plweb.isExam");
	
	private String userId = System.getProperty("javaws.plweb.var.user_id");
	private String courseId = System.getProperty("javaws.plweb.var.course_id");
	private String lessonId = System.getProperty("javaws.plweb.var.lesson_id");
	private String classId = System.getProperty("javaws.plweb.var.class_id");

	/**
	 * Specify active project, instead of activeLesson
	 */
	private XProject activeProject;
	private XTask activeTask = null;
	private MessageConsoleInterface activeConsole;
	//private JWebBrowser activeBrowser;
	private JEditorPane activeBrowser;
	private JMultiTextViewer activeViewer;

	private static ProjectEnvironment instance = null;

	public static ProjectEnvironment getInstance() {
		if (instance == null) {
			return instance = new ProjectEnvironment();
		} else {
			return instance;
		}
	}

	/**
	 * Load Project object
	 */
	public void loadActiveProject() {
		File fileLesson = new File(diskRoot, lessonPath);
		File fileLessonXml = new File(diskRoot, lessonXml);

		activeProject = XmlFactory.readProject(fileLessonXml);
		activeProject.setRootPath(fileLesson);
		activeProject.writeToDisk();

		// run project onload commands.
		// activeProject.getPropertyEx("");
		if (getLessonMode().equalsIgnoreCase("student")) {
			for (String key : activeProject.getProperties().keySet()) {
				if (key.startsWith("project.onload.command")) {
					String execmd = activeProject.getPropertyEx(key);
					String[] cmd = getShell(execmd);
					Runtime runtime = Runtime.getRuntime();
					Process process;
					try {
						process = runtime.exec(cmd, null, new File(
								activeProject.getRootPath()));
						process.waitFor();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public String getUname(){
		//String _uname = new String(this.uname, "UTF-8");
		//byte[] _uname = (this.uname).decodeBase64();
		try {
			Base64 base64 = new Base64();
			String _uname = new String(base64.decode((this.uname).getBytes()), "UTF-8");
			return _uname;
		} catch(UnsupportedEncodingException e) {
			return e.toString();
		}
	}
	
	public String getDepartment(){
		//String _department = new String(this.department, "UTF-8");
		//return _department;
		try {
			Base64 base64 = new Base64();
			String _department = new String(base64.decode((this.department).getBytes()), "UTF-8");
			return _department;
		} catch(UnsupportedEncodingException e) {
			return e.toString();
		}
	}
	
	public String getEnrollment(){
		return enrollment;
	}
	
	public String getLessonMode() {
		return lessonMode;
	}

	public void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
	}

	public XProject getActiveProject() {
		return activeProject;
	}

	public String getLessonXml() {
		return lessonXml;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public void setActiveTask(XTask task) {
		this.activeTask = task;
	}

	public XTask getActiveTask() {
		return this.activeTask;
	}

	public MessageConsoleInterface getActiveConsole() {
		return activeConsole;
	}

	public void setActiveConsole(MessageConsoleInterface activeConsole) {
		this.activeConsole = activeConsole;
	}

	/*public JWebBrowser getActiveBrowser() {
		return activeBrowser;
	}*/
	public JEditorPane getActiveBrowser() {
		return activeBrowser;
	}

	/*public void setActiveBrowser(JWebBrowser activeBrowser) {
		this.activeBrowser = activeBrowser;
	}*/
	public void setActiveBrowser(JEditorPane activeBrowser) {
		this.activeBrowser = activeBrowser;
	}

	public JMultiTextViewer getActiveViewer() {
		return activeViewer;
	}

	public void setActiveViewer(JMultiTextViewer activeViewer) {
		this.activeViewer = activeViewer;
	}

	public String getExplorer() {
		return explorer;
	}

	public String getExplorer(String root) {
		return explorer.replace("${root}", root);
	}

	public void setExplorer(String explorer) {
		this.explorer = explorer;
	}

	public String getShell() {
		return shell;
	}

	public String getDataUrl(){
		return dataUrl;
	}
	
	public String[] getShell(String command) {
		String[] result;
		String[] args = shell.split(" ");
		result = new String[args.length + 1];
		int i;
		for (i = 0; i < args.length; i++) {
			result[i] = args[i];
		}
		result[i] = command;
		return result;
	}

	public void setShell(String shell) {
		this.shell = shell;
	}
	
	public String getIsExam(){
		return isExam;
	}

	public String getUserId(){
		return userId;
	}
	
	public String getCourseId(){
		return courseId;
	}
	
	public String getLessonId(){
		return lessonId;
	}
	
	public String getClassId(){
		return classId;
	}
}
