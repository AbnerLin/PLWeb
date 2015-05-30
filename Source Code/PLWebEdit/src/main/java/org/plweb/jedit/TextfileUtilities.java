package org.plweb.jedit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

public class TextfileUtilities {
	final static private String defaultEncode = "UTF-8";

	public static String readText(String path) {
		String result = null;
		try {
			StringWriter writer = new StringWriter();
			FileReader reader = new FileReader(path);

			// 讀取緩衝資料, 上限2048bytes
			char data[] = new char[2048];
			int size;
			while (reader.ready()) {
				size = reader.read(data);
				writer.write(data, 0, size);
			}

			reader.close();
			writer.close();

			result = writer.getBuffer().toString();
		} catch (FileNotFoundException e) {
			// 忽略檔案不存在問題(傳回NULL空值)
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void writeText(File path, String context) {
		writeText(path.getPath(), context, defaultEncode);
	}

	public static void writeText(String path, String context) {
		writeText(path, context, defaultEncode);
	}

	public static void writeText(String path, String context, String encode) {
		try {
			OutputStreamWriter out;
			out = new OutputStreamWriter(new FileOutputStream(path), encode);
			out.write(context);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}