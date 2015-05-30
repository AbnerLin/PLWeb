package org.plweb.suite.common.xml.test;

public class Result {
	private String os;
	private String type;
	private String cmd;
	private String exit;
	private String status;
	private String stdout;
	private String stderr;

	public Result(String os, String type, String cmd, String exit,
			String status, String stdout, String stderr) {
		this.os = os;
		this.type = type;
		this.cmd = cmd;
		this.exit = exit;
		this.status = status;
		this.stdout = stdout;
		this.stderr = stderr;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getExit() {
		return exit;
	}

	public void setExit(String exit) {
		this.exit = exit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStdout() {
		return stdout;
	}

	public void setStdout(String stdout) {
		this.stdout = stdout;
	}

	public String getStderr() {
		return stderr;
	}

	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
}
