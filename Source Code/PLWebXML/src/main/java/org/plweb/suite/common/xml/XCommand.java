package org.plweb.suite.common.xml;

public class XCommand {
	private String mode;

	private String name;

	private String type;

	private String os;

	private String cmd;

	private String stdinFile;

	private String stdoutFile;

	public String getStdinFile() {
		return stdinFile;
	}

	public void setStdinFile(String stdinFile) {
		this.stdinFile = stdinFile;
	}

	public String getStdoutFile() {
		return stdoutFile;
	}

	public void setStdoutFile(String stdoutFile) {
		this.stdoutFile = stdoutFile;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
}
