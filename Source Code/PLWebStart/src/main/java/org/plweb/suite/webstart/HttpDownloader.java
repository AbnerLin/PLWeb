package org.plweb.suite.webstart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Download files using HTTP protocol.
 * 
 * @author Yan-hong Lin
 */
public class HttpDownloader {
	// HttpClient object
	private HttpClient http = new HttpClient();

	private String url;
	private File target;

	private boolean allowGzip;

	private long length;

	public boolean isAllowGzip() {
		return allowGzip;
	}

	public void setAllowGzip(boolean allowGzip) {
		this.allowGzip = allowGzip;
	}

	public HttpDownloader(String url, File target) {
		this.url = url;
		this.target = target;
	}

	public long getLength() {
		return length;
	}

	public boolean download() {

		// Reset length
		length = 0;

		boolean result = false;

		GetMethod get = new GetMethod(url);

		try {
			if (allowGzip) {
				get.addRequestHeader("accept-encoding", "gzip,deflate");
			}

			File targetDir = target.getParentFile();
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}

			http.executeMethod(get);

			InputStream is = get.getResponseBodyAsStream();
			if (allowGzip) {
				//is = new GZIPInputStream(is);
			}

			OutputStream os = new FileOutputStream(target);

			byte buff[] = new byte[2048];
			int size;

			while ((size = is.read(buff)) > 0) {
				// Count length
				length += size;

				os.write(buff, 0, size);
			}

			os.flush();
			os.close();
			is.close();

		} catch (HttpException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		return result;
	}
}
