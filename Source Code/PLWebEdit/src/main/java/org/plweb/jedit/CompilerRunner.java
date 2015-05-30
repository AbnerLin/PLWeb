package org.plweb.jedit;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.plweb.suite.common.xml.XCommand;
import org.plweb.suite.common.xml.XFile;
import org.plweb.suite.common.xml.XProject;
import org.plweb.suite.common.xml.XTask;
import org.plweb.suite.common.xml.test.Result;
import org.plweb.suite.common.xml.test.TestResult;

public class CompilerRunner extends Thread {
	private ProjectEnvironment env = ProjectEnvironment.getInstance();

	private MessageManager mm = MessageManager.getInstance();

	private Process process;

	private MessageConsoleInterface console;

	private boolean enabled = true;

	private String mode;

	private String code;

	private Long time_run;

	private Long time_begin;

	private Long time_used;

	private Long time_cli;

	private int test_ok = 0;

	private int test_error = 0;

	private String report_state;

	private XTask task;

	private String keystroke = "";

	private TestResult testResult;
	
	final private Color COLOR_OK = new Color(0, 180, 0);
	final private Color COLOR_ERROR = new Color(255, 0, 0);
	
	private Boolean isSubmit[];
	private int currentTaskIdx;
	//private int masteryIndex;
	
	private ProgramTester testRobot = null;
	
	private Boolean isInterrupt = false;
	
	private Encryption fileEncrypt;
	
	private String classId = env.getClassId();

	public CompilerRunner(MessageConsoleInterface console, String mode,
			String bufferText) {
		this.console = console;
		this.mode = mode;
		this.code = bufferText;
		this.time_run = new Date().getTime();
		this.task = env.getActiveTask();
		this.testResult = new TestResult(mode);

		/*
		// retrieve time_cli
		time_cli = (Long) task.getTempAttribute("time.begin");

		// retrieve time_begin
		time_begin = (Long) task.getTempAttribute("time.begin");

		// retrieve time_used
		Long time_update = (Long) task.getTempAttribute("time.update");
		if (time_update == null) {
			time_used = new Date().getTime() - time_begin;
		} else {
			time_used = new Date().getTime() - time_update;
		}
		task.setTempAttribute("time.update", new Date().getTime());
*/

		//time_cli = (Long) task.getTempAttribute("time.begin");

		if(((Long) task.getTempAttribute("time.begin")) == null)
			task.setTempAttribute("time.begin", new Date().getTime());
		
		time_begin = (Long) task.getTempAttribute("time.begin");
					
		time_used = new Date().getTime() - time_begin;
		
		task.setTempAttribute("time.update", new Date().getTime());

		// retrieve keystroke from task
		StringBuffer buff = (StringBuffer) task
				.getTempAttribute("buffer.keystroke");
		task.setTempAttribute("buffer.keystroke", new StringBuffer());
		if (buff != null) {
			keystroke = buff.toString();
		}
		
		
		
	}

	/**
	 * Create .plwebenv file
	 */
	private void createPLWebEnvFile() {
		XProject project = env.getActiveProject();
		File rootPath = new File(project.getRootPath());
		File envFile = new File(rootPath, ".plwebenv");

		try {
			PrintWriter w = new PrintWriter(envFile);

			w.print("user_id: ");
			w.println(System.getProperty("plweb.var.user_id"));

			// w.print("user_name: ");
			// w.println(System.getProperty("plweb.var.user_name"));

			w.print("course_id: ");
			w.println(System.getProperty("plweb.var.course_id"));

			w.print("lesson_id: ");
			w.println(System.getProperty("plweb.var.lesson_id"));

			w.print("class_id: ");
			w.println(System.getProperty("plweb.var.class_id"));

			w.print("question_id: ");
			w.println(task.getId());

			w.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public void run() {

		// create .plwebenv file
		// createPLWebEnvFile();
		XProject project = env.getActiveProject();
		
		if((env.getIsExam().equals("false")) || (env.getIsExam().equals("true") && project.getPropertyEx("hasExamFile").equals("false")) || (env.getIsExam().equals("true") && project.getPropertyEx("hasExamFile").equals("true") && isSubmit[currentTaskIdx].equals(false))) {
			
			String language = new String();

			File rootPath = new File(project.getRootPath());

			String osName = System.getProperty("os.name");
			String osVersion = System.getProperty("os.version");

			console.init();
			console.println("OS: " + osName + " (" + osVersion + ")");
			String status = "undefined";
			try {
				if (task != null) {
					
					if(env.getLessonMode().equalsIgnoreCase("author")){
						/*File _main = new File(project.getRootPath() + "\\" + task.getProperty("ExName") + ".main");
						File _java = new File(project.getRootPath() + "\\" + task.getProperty("ExName") + ".java");
						File _tmp = new File(project.getRootPath() + "\\" + task.getProperty("ExName") + ".tmp");
						*/
						
						File exam = new File(project.getRootPath() + "\\" + task.getProperty("ExName") + ".exam");
						if(exam.exists()) {
						
							if(project.getPropertyEx("language") != null)
								language = project.getProperty("language");
							else
								console.println("Please set the language into Project property.");
								
							project.setProperty("hasExamFile", "true");
							testRobot = ProgramTester.getInstance(project.getRootPath());
							console.print("compiling the source code...\n", Color.green);
							testRobot.compiler(language, task.getProperty("ExName"));
							
							String exName = task.getProperty("ExName");
							File javaTest = new File(project.getRootPath() + "\\" + exName + "Test.java");
							if(javaTest.exists()) {
								exName = exName + "Test";
								testRobot.compiler(language, exName);
							}
	
							console.print("read the parameter from .exam file\n", Color.green);
							ArrayList<String> param = testRobot.readFile(task.getProperty("ExName") + ".exam", "#");
							String result = new String();
							int paramNum = 1;
							if(param.size() == 0){
								result += testRobot.executeSrc(language, "", exName);
								result += "\n#####\n";
							} else {
								for(int i = 0; i < param.size(); i++){
									if(isInterrupt.equals(true))
										break;
									
									result += testRobot.executeSrc(language, param.get(i), exName);
									result += "#####\n";
								}
								paramNum = param.size();
								isInterrupt = false;
							}
							
							task.setProperty("paramNum", Integer.toString(paramNum));
							testRobot.printer(task.getProperty("ExName") + ".cond2", result);
				
							console.println("=============" + task.getProperty("ExName") + ".cond2=============\n", Color.red);
							console.println(result);
							
							
						}
					}

					
					for (XCommand command : task.getCommands()) {
						if (!command.getMode().equalsIgnoreCase(mode)) {
							continue;
						}
						if (!enabled) {
							break;
						}
						if (!checkOS(command.getOs())) {
							continue;
						}

						// do not process .after and .before type commands.
						if (command.getType().toLowerCase().endsWith(".after")) {
							continue;
						}
						if (command.getType().toLowerCase().endsWith(".before")) {
							continue;
						}

						// print command Name/Type
						console.print(command.getName(), Color.blue);
						console.print(" (", Color.blue);
						console.print(command.getType(), Color.blue);
						console.println(")", Color.blue);

						// get command by magic StringEx
						String execmd = project.getTaskStringEx(task,
								command.getCmd());

						// get command wrapper shell, run
						String[] cmd = env.getShell(execmd);
						Runtime runtime = Runtime.getRuntime();
						process = runtime.exec(cmd, null, rootPath);

						// dump something to <STDIN>
						dumpStdin(rootPath, command.getStdinFile(),
								process.getOutputStream());

						StringWriter stdout = new StringWriter();
						StringWriter stderr = new StringWriter();
						new DumpThread(process.getErrorStream(), stderr).start();
						new DumpThread(process.getInputStream(), stdout).start();

						// timeout prevented
						waitForOrKill(process, 180000);

						dumpStdout(rootPath, command.getStdoutFile(), stdout);

						stdout.close();
						stderr.close();

						Thread.sleep(500);

						if (process.exitValue() != 0) {
							String exitCode = String.valueOf(process.exitValue());
							console.println("return: ".concat(exitCode), COLOR_OK);

							status = command.getType().concat("_error");
							console.println(status, COLOR_ERROR);

						} else {
							String exitCode = String.valueOf(process.exitValue());
							console.println("return: ".concat(exitCode), COLOR_OK);

							status = command.getType().concat("_ok");
							console.println(status, COLOR_OK);
						}

						// update last state; count TEST_OK, TEST_ERROR
						updateState(status);

						// if (env.getLessonMode().equalsIgnoreCase("student")) {
						// mm.saveMessage(task.getId(),
						// (Long) task.getTempAttribute("time.begin"),
						// time_run, 0, "run", status, execmd, "", stdout,
						// stderr, process.exitValue(), "");
						// }

						testResult.addResult(new Result(command.getOs(), command
								.getType(), command.getCmd(), String
								.valueOf(process.exitValue()), status, stdout
								.toString(), stderr.toString()));

						// %ERRORLEVEL% <> 0; error
						if (process.exitValue() != 0) {
							break;
						}
					}

					// if (env.getLessonMode().equalsIgnoreCase("student")) {
					// mm.saveMessage(
					// task.getId(),
					// time_cli,
					// time_run,
					// time_used,
					// "done",
					// "success",
					// "All jobs done.",
					// code,
					// console.getAllText(),
					// "",
					// 0,
					// keystroke
					// );
					// }
					
					
				}
			} catch (Exception e) {
				e.printStackTrace();

				// mm.saveMessage(
				// task.getId(),
				// time_cli,
				// time_run,
				// time_used,
				// "done",
				// "failure",
				// "All jobs done.",
				// code,
				// console.getAllText(),
				// e.getMessage(),
				// 0,
				// keystroke
				// );
			}

			// ---save-report---------------------------------------------
		
				if(classId.indexOf("6") != 4){
					saveReport();
					
					// check mode
					if (env.getLessonMode().equals("student")) {
						// ---upload--------------------------------------------
						new Thread(new UploadFile(project, task)).start();
					}
				}
				
				
				
			

			// save snapshot
			// saveSnapshot();

			

			// File testFile = new File(rootPath, ".plwebtest");
			// try {
			// PrintWriter w = new PrintWriter(testFile);
			//
			// w.print("status: ");
			// w.println(m_status);
			//
			// w.print("test_num: ");
			// w.println(test_num);
			//
			// w.print("test_ok: ");
			// w.println(test_ok);
			//
			// w.close();
			// } catch (FileNotFoundException ex) {
			// ex.printStackTrace();
			// }

			// .after
			try {
				for (XCommand command : task.getCommands()) {
					if (!command.getMode().equals(mode)) {
						continue;
					}
					if (!enabled) {
						break;
					}
					if (!checkOS(command.getOs())) {
						continue;
					}
					if (!command.getType().toLowerCase().endsWith(".after")) {
						continue;
					}

					String execmd = project.getTaskStringEx(task, command.getCmd());

					// show command name
					console.println(command.getName().concat(" (")
							+ command.getType().concat(")"), Color.blue);

					String[] cmd = env.getShell(execmd);

					Runtime runtime = Runtime.getRuntime();

					process = runtime.exec(cmd, null, rootPath);

					dumpStdin(rootPath, command.getStdinFile(),
							process.getOutputStream());

					StringWriter stdout = new StringWriter();
					StringWriter stderr = new StringWriter();
					new DumpThread(process.getErrorStream(), stderr).start();
					new DumpThread(process.getInputStream(), stdout).start();

					waitForOrKill(process, 180000);

					dumpStdout(rootPath, command.getStdoutFile(), stdout);

					stdout.close();
					stderr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if (envFile.exists()) {
			// envFile.delete();
			// }
			//
			// if (testFile.exists()) {
			// testFile.delete();
			// }
			
			if((project.getPropertyEx("hasMastery") != null && project.getPropertyEx("hasMastery").equals("true")) && env.getLessonMode().equals("student")){
				
				if(!MasteryCore.getInstance().getIsChangeTask())
					MasteryCore.getInstance().compare(Integer.valueOf(task.getId()), time_used, status);
							
				//console.print("\n###" + time_used, Color.BLUE);
				//setMasteryIndex(MasteryCore.getInstance().getCurrentIdx());
			} 
		}
	}
	
	/*public void getMasteryIndex(){
		return masteryIndex;
	}
	
	private void setMasteryIndex(int idx){
		this.masteryIndex = idx;
	}*/
	
	public void setIsSubmit(Boolean isSubmit[]){
		this.isSubmit = isSubmit;
	}
	public void setCurrentTaskIdx(int idx){
		this.currentTaskIdx = idx;
	}
	
	/**
	 * Update LAST_STATE; count TEST_OK, TEST_ERROR
	 * 
	 * @param status
	 */
	private void updateState(String status) {
		// update last state
		report_state = status;

		if ("test_ok".equalsIgnoreCase(status)) {
			test_ok++;
		} else if ("test_error".equalsIgnoreCase(status)) {
			test_error++;
		}
	}

	/**
	 * Save report
	 */
	private void saveReport() {
		mm.saveReport(task.getId(), test_ok, test_error,
				testResult.getXmlString(), time_run, time_used, report_state,
				code, console.getAllText());
		
	}

	/**
	 * Save Snapshot
	 */
	private void saveSnapshot() {
		mm.saveSnapshot(task.getId(), time_begin, code, keystroke);
	}

	/**
	 * Check OS Type
	 * 
	 * @param os
	 * @return
	 */
	private boolean checkOS(String os) {

		String osName = System.getProperty("os.name");
		// String osVersion = System.getProperty("os.version");

		if (os == null || os.trim().equals("")) {
			return true;
		}

		if (os.trim().equalsIgnoreCase("all")) {
			return true;
		}

		if (os.trim().equalsIgnoreCase("any")) {
			return true;
		}

		if (osName.toUpperCase().contains(os.trim().toUpperCase())) {
			return true;
		}

		return false;
	}

	/**
	 * Write something to process in standard input.
	 * 
	 * @param rootPath
	 * @param stdinFile
	 * @param outputStream
	 */
	private void dumpStdin(File rootPath, String stdinFile,
			OutputStream outputStream) {

		if (stdinFile != null && !stdinFile.trim().equals("")) {
			try {
				BufferedReader stdin = new BufferedReader(new FileReader(
						new File(rootPath, stdinFile)));
				PrintStream stream = new PrintStream(outputStream);
				String line;
				while ((line = stdin.readLine()) != null) {
					stream.print(line);
				}
				stream.close();
				stdin.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Save <STDOUT> contents to disk
	 * 
	 * @param rootPath
	 * @param stdoutFile
	 * @param stdout
	 */
	private void dumpStdout(File rootPath, String stdoutFile,
			StringWriter stdout) {
		if (stdoutFile != null && !stdoutFile.trim().equals("")) {
			try {
				Writer stdoutWriter = new PrintWriter(new File(rootPath,
						stdoutFile));
				stdoutWriter.write(stdout.getBuffer().toString());
				stdoutWriter.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public ProgramTester getRobot(){
		return testRobot;
	}
	
	public void interrupt() {
		enabled = false;
		if (process != null) {
			process.destroy();
			process = null;
		}		
		if(testRobot != null)
			testRobot.interrupt();
		
		isInterrupt = true;
	}
	
	static void waitForOrKill(Process self, long numberOfMillis) {
		ProcessRunner runnable = new ProcessRunner(self);
		Thread thread = new Thread(runnable);
		thread.start();
		runnable.waitForOrKill(numberOfMillis);
	}

	static class ProcessRunner implements Runnable {
		Process process;
		private boolean finished;

		public ProcessRunner(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
			}
			synchronized (this) {
				notifyAll();
				finished = true;
			}
		}

		public synchronized void waitForOrKill(long millis) {
			if (!finished) {
				try {
					wait(millis);
				} catch (InterruptedException e) {
				}
				if (!finished) {
					process.destroy();
				}
			}
		}
	}

	class DumpThread extends Thread {
		private InputStream stream;
		private PrintWriter pw;

		public DumpThread(InputStream stream, StringWriter sw) {
			this.stream = stream;
			this.pw = new PrintWriter(sw);
		}

		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stream));
				String line;
				while ((line = reader.readLine()) != null) {
					console.println(line);
					pw.println(line);
				}
				pw.flush();
				pw.close();

				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				console.println(ex.getMessage());
			}
		}
	}

	class UploadFile implements Runnable {

		private XProject project;

		private XTask task;

		public UploadFile(XProject project, XTask task) {
			this.project = project;
			this.task = task;
		}

		public void run() {
			List<XFile> files = new ArrayList<XFile>();
			for (String key : task.getProperties().keySet()) {
				if (checkUploadPermission(key)) {
					String file = project.getTaskPropertyEx(task, key);
					if (file != null) {
						files.add(new XFile(project, file, true));
					}
				}
			}
			mm.uploadFile(files);
		}

		private boolean checkUploadPermission(String filePropKey) {
			if (filePropKey.equals("file.main")) {
				return true;
			} else if (filePropKey.startsWith("file.attach")) {
				return true;
			}
			return false;
		}
	}
}
