package org.plweb.jedit;

import org.apache.commons.lang.StringEscapeUtils;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.plweb.suite.common.xml.XTask;

public class BufferChangeListener extends BufferAdapter {

	// Singleton pattern implemented
	private static BufferChangeListener instance = null;

	public static BufferChangeListener getInstance() {
		if (instance == null) {
			instance = new BufferChangeListener();
		}
		return instance;
	}

	private ProjectEnvironment env = ProjectEnvironment.getInstance();

	private void updateBufferChange(String keystroke) {
		XTask task = env.getActiveTask();

		if (task != null) {

			StringBuffer buff = null;

			Object obj = task.getTempAttribute("buffer.keystroke");

			if (obj != null && obj instanceof StringBuffer) {
				buff = (StringBuffer) obj;
			}
			else {
				buff = new StringBuffer();
				task.setTempAttribute("buffer.keystroke", buff);
			}
			
			buff.append(keystroke);
		}
	}

	public void contentInserted(Buffer buffer, int startLine, int offset,
			int numLines, int length) {
		if (length > 0) {
			String str = new String();
			String curtime = new String();
			String strlen = new String();
			try {
				str = buffer.getText(offset, length);
				curtime = String.valueOf(System.currentTimeMillis());
				strlen = String.valueOf(length);
			} catch (Exception e) {
				e.printStackTrace();
			}

			StringBuffer buf = new StringBuffer();
			buf.append("<es:type>".concat("i"));
			buf.append("<es:offset>".concat(String.valueOf(offset)));
			buf.append("<es:string>".concat(StringEscapeUtils.escapeXml(str)));
			buf.append("<es:length>".concat(strlen));
			buf.append("<es:time>".concat(curtime));

			updateBufferChange(buf.toString());
		}
	}

	public void preContentRemoved(Buffer buffer, int startLine, int offset,
			int numLines, int length) {
		if (length > 0) {
			String str = new String();
			String curtime = new String();
			String strlen = new String();
			try {
				str = buffer.getText(offset, length);
				curtime = String.valueOf(System.currentTimeMillis());
				strlen = String.valueOf(length);
			} catch (Exception e) {
				e.printStackTrace();
			}

			StringBuffer buf = new StringBuffer();
			buf.append("<es:type>".concat("r"));
			buf.append("<es:offset>".concat(String.valueOf(offset)));
			buf.append("<es:string>".concat(StringEscapeUtils.escapeXml(str)));
			buf.append("<es:length>".concat(strlen));
			buf.append("<es:time>".concat(curtime));

			updateBufferChange(buf.toString());
		}
	}
}
