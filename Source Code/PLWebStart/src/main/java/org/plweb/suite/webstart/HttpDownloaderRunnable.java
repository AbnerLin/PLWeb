package org.plweb.suite.webstart;

import java.io.File;
import java.net.URL;

public class HttpDownloaderRunnable implements Runnable {
	private HttpDownloader downloader;

	public HttpDownloaderRunnable(String url, File target, boolean allowGzip) {
		downloader = new HttpDownloader(url, target);
		downloader.setAllowGzip(true);
	}

	public HttpDownloaderRunnable(URL url, File target, boolean allowGzip) {
		downloader = new HttpDownloader(url.toString(), target);
		downloader.setAllowGzip(true);
	}

	public HttpDownloaderRunnable(String url, File target) {
		downloader = new HttpDownloader(url, target);
	}

	public HttpDownloaderRunnable(URL url, File target) {
		downloader = new HttpDownloader(url.toString(), target);
	}

	public void run() {
		downloader.download();
	}

	public long getLength() {
		return downloader.getLength();
	}
}
