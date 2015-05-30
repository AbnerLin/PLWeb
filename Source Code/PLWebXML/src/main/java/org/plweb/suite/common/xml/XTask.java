package org.plweb.suite.common.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class XTask {
	private String id;

	private String title;

	private List<XCommand> commands = new ArrayList<XCommand>();

	private Map<String, String> properties = new TreeMap<String, String>();

	/**
	 * Temporary Attributes for runtime variables
	 */
	private Map<String, Object> tempAttributes = new TreeMap<String, Object>();

	public Object getTempAttribute(String key) {
		return tempAttributes.get(key);
	}

	public void setTempAttribute(String key, Object value) {
		tempAttributes.put(key, value);
	}

	public Map<String, Object> getTempAttributes() {
		return tempAttributes;
	}

	public void setTempAttributes(Map<String, Object> tempAttributes) {
		this.tempAttributes = tempAttributes;
	}

	public void addCommand(XCommand command) {
		commands.add(command);
	}

	public List<XCommand> getCommands() {
		return commands;
	}

	public void setCommands(List<XCommand> commands) {
		this.commands = commands;
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

	public void setTitle(String title) {
		this.title = title;
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * getStringEx
	 * 
	 * @param text
	 * @return
	 */
	public String getStringEx(String text) {
		return PropertyUtils.process(properties, text);
	}

	/**
	 * getPropertyEx
	 * 
	 * @param key
	 * @return
	 */
	public String getPropertyEx(String key) {
		return PropertyUtils.process(properties, properties.get(key));
	}

	/**
	 * getProperty
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * getPropertyKeys
	 * 
	 * @return
	 */
	public List<String> getPropertyKeys() {
		List<String> result = new ArrayList<String>();
		for (String key : properties.keySet()) {
			result.add(key);
		}
		return result;
	}

	/**
	 * getPropertyKeys
	 * 
	 * @param prefix
	 * @return
	 */
	public List<String> getPropertyKeys(String prefix) {
		List<String> result = new ArrayList<String>();
		for (String key : properties.keySet()) {
			if (key.startsWith(prefix)) {
				result.add(key);
			}
		}
		return result;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
