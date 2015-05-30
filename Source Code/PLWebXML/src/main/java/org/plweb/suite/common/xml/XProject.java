package org.plweb.suite.common.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class XProject {

	private String id;

	private String title;

	private List<XFile> files = new ArrayList<XFile>();

	private List<XTask> tasks = new ArrayList<XTask>();

	private Map<String, String> properties = new TreeMap<String, String>();

	private String rootPath;

	public XProject() {

	}

	public XProject(String rootPath) {
		setRootPath(rootPath.trim());
	}

	public XProject(File rootPath) {
		setRootPath(rootPath);
	}

	public String generateTaskId() {
		String ret = "";

		try {
			int max = 0;
			for (XTask xtask : getTasks()) {
				int id = Integer.valueOf(xtask.getId());
				if (id > max) {
					max = id;
				}
			}
			ret = String.valueOf(max + 1);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			ret = String.valueOf(tasks.size() + 1);
		}

		return ret;
	}

	public String generateFilePath(File path) {
		String ret = null;
		try {
			String cpath = path.getCanonicalPath();
			int length1 = cpath.length();
			int length2 = rootPath.length();
			// System.err.print(rootPath);
			// System.err.print(cpath);
			ret = cpath.substring(length2 + 1, length1);
			ret = ret.replace("\\", "/");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	public XTask findTask(String taskId) {
		for (XTask task : tasks) {
			if (task.getId().equals(taskId)) {
				return task;
			}
		}
		return null;
	}

	public XFile findFile(File file) {
		for (XFile f : files) {
			if (new File(f.getDiskPath()).equals(file)) {
				return f;
			}
		}
		return null;
	}

	public void writeToDisk() {
		if (rootPath != null && !rootPath.equals("")) {
			new File(rootPath).mkdirs();
			for (XFile xf : files) {
				xf.writeToDisk();
			}
		}
	}

	public void readFromDisk() {
		if (rootPath != null && !rootPath.equals("")) {
			files.clear();
			List<String> list = new ArrayList<String>();
			listDirectory(new File(rootPath), list);

			for (String s : list) {
				String path = s.substring(rootPath.length());
				if (path.startsWith("/") || path.startsWith("\\")) {
					path = path.substring(1);
				}
				XFile xfile;
				xfile = new XFile(this, path);
				xfile.readFromDisk();
				addFile(xfile);
			}
		}
	}

	private void listDirectory(File dir, List<String> list) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				listDirectory(file, list);
			} else {
				list.add(file.getPath().replace("\\", "/"));
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public List<XFile> getFiles() {
		return files;
	}

	public XFile getFile(int idx) {
		return files.get(idx);
	}

	public XFile getFile(String path) {
		for (XFile f : files) {
			if (f.getPath().equals(path)) {
				return f;
			}
		}
		return null;
	}

	public void setFiles(List<XFile> files) {
		this.files = files;
	}

	/**
	 * �W�[�ɮרëإ߯���
	 * 
	 * @param file
	 */
	public void addFile(XFile file) {
		file.setProject(this);
		files.add(file);
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(File rootPath) {
		try {
			this.rootPath = rootPath.getCanonicalPath();
		} catch (IOException ex) {
			System.err.println("Error root path: " + rootPath);
			ex.printStackTrace();
		}
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public List<XTask> getTasks() {
		return tasks;
	}

	public XTask getTask(int idx) {
		return tasks.get(idx);
	}

	public void addTask(XTask task) {
		tasks.add(task);
	}

	public void insertTask(int idx, XTask task) {
		tasks.add(idx, task);
	}

	public void removeTask(int idx) {
		tasks.remove(idx);
	}

	public void setTasks(List<XTask> tasks) {
		this.tasks = tasks;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * �ǤJ��r�i��ѼƥN���B�z
	 * 
	 * @param text
	 * @return
	 */
	public String getStringEx(String text) {
		return PropertyUtils.process(properties, text);
	}

	/**
	 * ��o�Ѽƴ����B�z�᪺�ݩ�
	 * 
	 * @param key
	 * @return
	 */
	public String getPropertyEx(String key) {
		return PropertyUtils.process(properties, properties.get(key));
	}

	/**
	 * ��oTask���ݩʳ]�w(�֤JProject Scope�ݩ�)
	 * 
	 * @param task
	 * @param key
	 * @return
	 */
	public String getTaskStringEx(XTask task, String text) {
		Map<String, String> props = new HashMap<String, String>();

		props.putAll(properties);
		props.putAll(task.getProperties());

		return PropertyUtils.process(props, text);
	}

	/**
	 * ��oTask���ݩʳ]�w(�֤JProject Scope�ݩ�)
	 * 
	 * @param task
	 * @param key
	 * @return
	 */
	public String getTaskPropertyEx(XTask task, String key) {
		Map<String, String> props = new HashMap<String, String>();

		props.putAll(properties);
		props.putAll(task.getProperties());

		return PropertyUtils.process(props, props.get(key));
	}

	/**
	 * ��o�ݩ�
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
}
