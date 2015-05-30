package org.plweb.suite.common.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class XFile {

	private String path;

	private String encodedContent;

	private Map<String, String> properties = new HashMap<String, String>();

	private XProject project;

	public XFile() {

	}

	public XFile(XProject project) {
		this.project = project;
	}

	public XFile(String path) {
		this.path = path;
	}
	
	public XFile(XProject project, String path) {
		this.project = project;
		this.path = path;
	}

	/**
	 * 使用第三個參數(傳入true)決定是否自動載入檔案內容
	 * @param project
	 * @param path
	 * @param autoLoad
	 */
	public XFile(XProject project, String path, boolean autoLoad) {
		this.project = project;
		this.path = path;
		if (autoLoad) {
			readFromDisk();
		}
	}
	
	public XFile(String path, String encodedContent) {
		this.path = path;
		this.encodedContent = encodedContent;
	}

	public boolean writeToDisk() {
		String diskPath = getDiskPath();
		boolean ret = false;
		if (diskPath != null) {
			File f = new File(diskPath);
			File dir = f.getParentFile();

			// make dirs
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				// make file
				f.createNewFile();

				// write content to file if not null
				if (getEncodedContent() != null
						&& !getEncodedContent().equals("")) {
					FileOutputStream writer = new FileOutputStream(f);
					writer.write(getDecodedContent());
					writer.close();
				}
				ret = true;
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		return ret;
	}

	public boolean readFromDisk() {
		boolean ret = false;
		String diskPath = getDiskPath();
		if (diskPath != null) {
			File f = new File(diskPath);

			byte[] buf = null;

			try {
				InputStream is = new FileInputStream(f);
				buf = new byte[is.available()];
				is.read(buf);
				is.close();
				
				//編碼
				setEncodedContent(new BASE64Encoder().encode(buf));
				ret = true;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}

	public String getDiskPath() {
		return new File(project.getRootPath(), path).getPath();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getEncodedContent() {
		return encodedContent;
	}

	public byte[] getDecodedContent() {
		byte[] result = null;
		try {
			result = new BASE64Decoder().decodeBuffer(getEncodedContent());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public void setEncodedContent(String encodedContent) {
		this.encodedContent = encodedContent;
	}

	public XProject getProject() {
		return project;
	}

	public void setProject(XProject project) {
		this.project = project;
	}

	public boolean isAttachFile() {
		// not impl
		return false;
	}

	public boolean isActiveFile() {
		// not impl
		return false;
	}

	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
