package org.plweb.suite.webstart;

class JEditExistsException extends Exception {
	private static final long serialVersionUID = 339216594956472283L;

	public JEditExistsException() {
		super("ERROR: Close the exist editor and try again.");
	}
}