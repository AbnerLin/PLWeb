package org.plweb.jedit;

import java.util.Properties;

public class ServerRequestRunner extends ServerRequest implements Runnable {
	public ServerRequestRunner(String action, Properties props) {
		super(action, props);
	}

	public void run() {
		request();
	}
}
