package org.plweb.jedit;

import org.gjt.sp.jedit.EBPlugin;
import org.plweb.suite.common.xml.XTask;

public class CompilerPlugin extends EBPlugin {
	private ProjectEnvironment env = ProjectEnvironment.getInstance();
	private MessageManager mm = MessageManager.getInstance();

	public CompilerPlugin() {
		env.loadActiveProject();
	}

	@Override
	public void start() {

		// jEdit.getActiveView().handleMessage(msg)
//		mm.saveMessage(0, 0, 0, 0, "start", "", "jEdit startup", "", "", "", 0, "");
	}

	@Override
	public void stop() {
		XTask task = env.getActiveTask(); // current task

//		if (task != null) {
//			mm.saveMessage(
//				task.getId(),
//				(Long) task.getTempAttribute("time.begin"),
//				(Long) task.getTempAttribute("time.begin"),
//				0,
//				"end",
//				"",
//				"",
//				"",
//				0,
//				0,
//				0,
//				0
//			);
//		}
		
//		mm.saveMessage(0, 0, 0, 0, "stop", "", "jEdit shutdown", "", "", "", 0, "");
	}

}
