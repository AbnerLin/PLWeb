package org.plweb.jedit;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.gjt.sp.jedit.jEdit;
import org.plweb.suite.common.xml.XFile;
import org.plweb.suite.common.xml.XProject;
import org.plweb.suite.common.xml.XmlFactory;

public class MessageManager {

	private static MessageManager SINGLETON;

	public static MessageManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new MessageManager();
		}
		return SINGLETON;
	}

	private Thread bgt;
	private Queue<ServerRequest> queue1 = new ConcurrentLinkedQueue<ServerRequest>();
	private Map<String, ServerRequest> map1 = new ConcurrentHashMap<String, ServerRequest>();

	public MessageManager() {
		bgt = new Thread(new Runnable() {

			public void run() {

				int c = 0;

				while (true) {

					// System.err.println(queue1.size() + ", " + c++);

					if (jEdit.getActiveView() != null
							&& jEdit.getActiveView().getStatus() != null) {
						jEdit.getActiveView()
								.getStatus()
								.setMessage(
										"等待上傳的訊息數量 ("
												+ (queue1.size() + map1.size())
												+ ")");
					}

					synchronized (queue1) {
						ServerRequest request = queue1.peek();

						if (request != null) {
							request.request();

							if (request.isFinished()) {
								queue1.remove();
							} else {
								// delay 10 sec when error occured.
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

					synchronized (map1) {
						for (String key : map1.keySet()) {
							ServerRequest request = map1.get(key);

							if (request != null) {
								request.request();

								// if success
								if (request.isFinished()) {
									map1.remove(key);
								} else {
									// delay 10 sec when error occured.
									try {
										Thread.sleep(10000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		});
		bgt.start();
	}

	private static long prev_time_cli = (long) 0;

	/**
	 * Call service.request/save_message_v2.groovy
	 * 
	 * @param question_id
	 * @param time_cli
	 * @param time_grp
	 * @param time_run
	 * @param time_seg
	 * @param type
	 * @param result
	 * @param event
	 * @param code
	 * @param msg
	 * @param msg_error
	 * @param msg_exit
	 * @param keystroke
	 */
	/*
	 * public void saveMessage( Object question_id, Object time_grp, Object
	 * time_run, Object time_seg, Object type, Object result, Object event,
	 * Object code, Object msg, Object msg_error, Object msg_exit, Object
	 * keystroke ) { Properties props = new Properties();
	 * 
	 * // prevent time_cli == prev_time_cli, for message sequence order long
	 * time_cli = new Date().getTime(); if (time_cli==prev_time_cli) { time_cli
	 * ++; } else if (time_cli < prev_time_cli) { time_cli = prev_time_cli +
	 * (long) 1; } prev_time_cli = time_cli;
	 * 
	 * props.setProperty("time_cli", String.valueOf(time_cli));
	 * 
	 * props.setProperty("question_id", String.valueOf(question_id));
	 * props.setProperty("time_grp", String.valueOf(time_grp));
	 * props.setProperty("time_run", String.valueOf(time_run));
	 * props.setProperty("time_seg", String.valueOf(time_seg));
	 * props.setProperty("type", String.valueOf(type));
	 * props.setProperty("result", String.valueOf(result));
	 * props.setProperty("event", String.valueOf(event));
	 * props.setProperty("code", String.valueOf(code)); props.setProperty("msg",
	 * String.valueOf(msg)); props.setProperty("msg_error",
	 * String.valueOf(msg_error)); props.setProperty("msg_exit",
	 * String.valueOf(msg_exit)); props.setProperty("keystroke",
	 * String.valueOf(keystroke));
	 * 
	 * new Thread(new ServerRequestRunner("save_message_v2", props)).start(); }
	 */

	/**
	 * Call service.request/save_report_v2.groovy
	 * 
	 * @param question_id
	 * @param test_ok
	 * @param test_error
	 * @param time_run
	 * @param time_used
	 * @param report_state
	 * @param report_msg
	 */
	public void saveReport(Object question_id, Object test_ok,
			Object test_error, Object test_result, Object time_run,
			Object time_used, Object report_state, Object report_code,
			Object report_msg) {
		Properties props = new Properties();

		props.setProperty("question_id", String.valueOf(question_id));
		props.setProperty("test_ok", String.valueOf(test_ok));
		props.setProperty("test_error", String.valueOf(test_error));
		props.setProperty("test_result", String.valueOf(test_result));
		props.setProperty("time_run", String.valueOf(time_run));
		props.setProperty("time_used", String.valueOf(time_used));
		props.setProperty("report_state", String.valueOf(report_state));
		props.setProperty("report_code", String.valueOf(report_code));
		props.setProperty("report_msg", String.valueOf(report_msg));
		props.setProperty("time_cli", String.valueOf(new Date().getTime()));

		queue1.offer(new ServerRequest("save_report_v3", props));

		// new Thread(new ServerRequestRunner("save_report_v2", props)).start();
	}
	
	public void resetTask(Object question_id) {
		Properties props = new Properties();
		props.setProperty("question_id", String.valueOf(question_id));
		queue1.offer(new ServerRequest("resetTask", props));
	}

	/**
	 * Call service.request/save_snapshot_v2.groovy
	 * 
	 * @param question_id
	 * @param time_begin
	 * @param code
	 * @param keystroke
	 */
	public void saveSnapshot(Object question_id, Object time_begin,
			Object code, Object keystroke) {
		Properties props = new Properties();

		props.setProperty("question_id", String.valueOf(question_id));
		props.setProperty("time_begin", String.valueOf(time_begin));
		props.setProperty("code", String.valueOf(code));
		props.setProperty("keystroke", String.valueOf(keystroke));
		props.setProperty("time_cli", String.valueOf(new Date().getTime()));

		queue1.offer(new ServerRequest("save_snapshot_v2", props));

		// new Thread(new ServerRequestRunner("save_snapshot_v2",
		// props)).start();
	}

	/**
	 * upload user files (convert XFile to BASE64 encoding)
	 * 
	 * @param files
	 */
	public void uploadFile(List<XFile> files) {
		for (XFile f : files) {
			Properties props = new Properties();
			props.setProperty("path", f.getPath());
			props.setProperty("content", f.getEncodedContent());
			// new ServerRequest("uploadFile", props).request();

			map1.put(f.getPath(), new ServerRequest("uploadFile", props));
		}
	}

	/**
	 * upload XProject (mode=author)
	 * 
	 * @param project
	 */
	public Thread saveProject(XProject project) {
		// convert XProject to XML string
		StringWriter xml = new StringWriter();
		XmlFactory.saveProject(project, xml);

		Properties props = new Properties();
		props.setProperty("content", xml.toString());

		Thread result = new Thread(
				new ServerRequestRunner("saveProject", props));
		result.start();
		return result;
	}
	
	public Thread saveGradeSetting(Object gradeSetting){
		Properties props = new Properties();
		props.setProperty("gradeSetting", String.valueOf(gradeSetting));
		
		Thread result = new Thread(new ServerRequestRunner("saveGradeSetting", props));
		
		result.start();
		return result;
	}
	
	public Thread saveMasterySet(Object masterySet){
		Properties props = new Properties();
		props.setProperty("masterySet", String.valueOf(masterySet));
		
		Thread result = new Thread(new ServerRequestRunner("saveMasterySet", props));
		result.start();
		return result;
	}
	
	public String getMasterySet(Object courseId, Object lessonId){
		Properties props = new Properties();
		
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		
		ServerRequest request = new ServerRequest("getMasterySet", props);
		request.setFlag();
		queue1.offer(request);
		
		while(request.isFinished() == false){
			try {
					Thread.sleep(500);
			} catch (InterruptedException ex) {

			}
		}
		
		return request.getResponse();	
	}
	
	public String getStuMastery(Object classId, Object courseId, Object lessonId, Object userId){
		Properties props = new Properties();
		
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		props.setProperty("classId", String.valueOf(classId));
		props.setProperty("userId", String.valueOf(userId));
		
		ServerRequest request = new ServerRequest("getStuMastery", props);
		request.setFlag();
		queue1.offer(request);
		
		while(request.isFinished() == false){
			try {
					Thread.sleep(500);
			} catch (InterruptedException ex) {

			}
		}
		
		return request.getResponse();
	}
	
	public String getGrade(Object classId, Object courseId, Object lessonId, Object userId){
		Properties props = new Properties();
		
		props.setProperty("classId", String.valueOf(classId));
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		props.setProperty("userId", String.valueOf(userId));
		
		ServerRequest request = new ServerRequest("init", props);
		request.setFlag(); // set use grade url.
		queue1.offer(request);
		
		while(request.isFinished() == false){
			try {
					Thread.sleep(500);
			} catch (InterruptedException ex) {

			}
		}
		
		return request.getResponse();	
	}
	
	//mm.saveStuMastery(env.getClassId(), env.getCourseId(), env.getLessonId(), env.getUserId(), masteryCore.getMasteryString());
	public void saveStuMastery(Object classId, Object courseId, Object lessonId, Object userId, Object masteryString, Object isNeedHelp) {
		Properties props = new Properties();
		
		props.setProperty("classId", String.valueOf(classId));
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		props.setProperty("userId", String.valueOf(userId));
		props.setProperty("masteryGrade", String.valueOf(masteryString));
		props.setProperty("isNeedHelp", String.valueOf(isNeedHelp));
		
		ServerRequest request = new ServerRequest("saveMasteryGrade", props);
		request.setFlag();
		queue1.offer(request);
	}
	
	public void saveGrade(Object grade, Object classId, Object courseId, Object lessonId, Object userId){
		Properties props = new Properties();
		
		props.setProperty("classId", String.valueOf(classId));
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		props.setProperty("userId", String.valueOf(userId));
		props.setProperty("grade", String.valueOf(grade));
		
		ServerRequest request = new ServerRequest("saveGrade", props);
		request.setFlag(); // set use dataSetting url.
		queue1.offer(request);
	}
	//mm.updateConsoleText(env.getClassId(), env.getCourseId(), env.getLessonId(), env.getActiveTask().getId(), env.getUserId(), console.getAllText());
	public void updateConsoleText(Object classId, Object courseId, Object lessonId, Object taskId, Object userId, Object consoleText) {
		Properties props = new Properties();
		
		props.setProperty("classId", String.valueOf(classId));
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		props.setProperty("taskId", String.valueOf(taskId));
		props.setProperty("userId", String.valueOf(userId));
		props.setProperty("consoleText", String.valueOf(consoleText));
		
		queue1.offer(new ServerRequest("updateConsoleText", props));
	}
	
	public String getSolution(Object classId, Object courseId, Object lessonId, Object idx){
		Properties props = new Properties();
		
		props.setProperty("classId", String.valueOf(classId));
		props.setProperty("courseId", String.valueOf(courseId));
		props.setProperty("lessonId", String.valueOf(lessonId));
		props.setProperty("taskSeq", String.valueOf(idx));
		
		ServerRequest request = new ServerRequest("getSolution", props);
		request.setFlag(); // set use dataSetting url.
		queue1.offer(request);
		
		while(request.isFinished() == false){
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				return ex.toString();
			}
		}
		return request.getResponse();
	}
	
}
