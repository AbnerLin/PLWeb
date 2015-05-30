package org.plweb.suite.webstart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

public class HttpStringReader {

	private URL url;

	public HttpStringReader() {

	}

	public HttpStringReader(URL url) {
		this.url = url;
	}

	public String read() {
		return read(url);
	}

	public String read(URL url) {
		String result = null;
		try {
			URLConnection conn = url.openConnection();

			// Get response
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader rd = new BufferedReader(isr);
			String line;
			while ((line = rd.readLine()) != null) {
				pw.println(line);
			}

			pw.flush();
			result = sw.getBuffer().toString();

			pw.close();
			sw.close();
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
