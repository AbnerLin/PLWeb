package org.plweb.jedit;

import java.io.*;
import java.util.ArrayList;

public class ProgramTester {
	
	private Boolean isPassCompiler = false;
	
	private String rootPath;
	private String _javac = "javac -encoding utf-8";
	private String _java = "java";
	private String _javaExtension = ".java";
	
	private String _cc = "gcc -fexec-charset=big5 -o ";
	private String _cExecute = ".exe";
	private String _cExtension = ".c";
	
	private String _scheme = "petite -q < ";
	private String _schemeExecute = ".scm";
	
	private String _vbc = "vbc ";
	private String _vbExecute = ".exe";
	private String _vbExtension = ".vb";
	
	
	private String cmd;
	private String extension;
	
	private Process process = null;
	
	private static ProgramTester instance = null;
	
	ArrayList<String> errorHint = null;
	
	public static ProgramTester getInstance(String rootPath){
		if(instance == null)
			return instance = new ProgramTester(rootPath);
		else
			return instance;
	}
	
	public ProgramTester(String rootPath){
		this.rootPath = rootPath;
	}
	
	// output the file.
	public void printer(String fileName, String content) throws IOException {
		PrintWriter printer = new PrintWriter(rootPath + "\\" + fileName, "UTF-8");
		printer.println(content);
		printer.close();
	}
	
	// compiler the source code
	public void compiler(String programLanguage, String srcName) throws IOException, InterruptedException {
		if(!programLanguage.equalsIgnoreCase("scheme")) {
			setCompilerCmd(programLanguage, srcName);
			
			File directPath = new File(this.rootPath);
			process = Runtime.getRuntime().exec(cmd + " " + rootPath + "\\" + srcName + extension, null, directPath);
			process.waitFor(); // wait for compiler.
			  
			if (process.exitValue() != 0) {
				isPassCompiler = false;
			} else
				isPassCompiler = true;
		}
	}
	
	private void setCompilerCmd(String programLanguage, String srcName){
		if(programLanguage.equalsIgnoreCase("java")){
			this.cmd = _javac;
			this.extension = _javaExtension;
		} else if(programLanguage.equalsIgnoreCase("c")){
			this.cmd = _cc + srcName + _cExecute;
			this.extension = _cExtension;
		} else if(programLanguage.equalsIgnoreCase("vb")){
			this.cmd = _vbc;
			this.extension = _vbExtension;
		}
	}
	
	private void setRunCmd(String programLanguage){
		if(programLanguage.equalsIgnoreCase("java")){
			this.cmd = _java + " ";
			this.extension = "";
		} else if(programLanguage.equalsIgnoreCase("c")){
			this.cmd = this.rootPath + "\\";
			this.extension = _cExecute; //.exe
		} else if(programLanguage.equalsIgnoreCase("scheme")) {
			this.cmd = _scheme;
			this.extension = _schemeExecute;
		} else if(programLanguage.equalsIgnoreCase("vb")){
			this.cmd = this.rootPath + "\\";
			this.extension = _vbExecute;
		}
	}
	
	// for scheme
	private String forScheme(String srcName, String param)throws IOException{
		// generate new source code
		String tmpName = "_TMP";
		BufferedReader br = new BufferedReader(new FileReader(this.rootPath + "\\" + srcName + _schemeExecute));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			String result = sb.toString() + param;
			printer(srcName + tmpName + _schemeExecute, result);
		} finally {
			br.close();
		}
		
		return srcName + tmpName;
		
	}
	
	// run the binary code (with param) 
	public String executeSrc(String programLanguage, String param, String srcName) throws IOException, InterruptedException {
			setRunCmd(programLanguage);
			
			if(programLanguage.equalsIgnoreCase("scheme")){
				srcName = forScheme(srcName, param);
				isPassCompiler = true;
			}
			
			if(isPassCompiler){
				File directPath = new File(this.rootPath);
				String[] _cmd = {"cmd", "/c", (cmd + srcName + extension)};
				if(programLanguage.equalsIgnoreCase("scheme"))
					process = Runtime.getRuntime().exec(_cmd, null, directPath);
				else
					process = Runtime.getRuntime().exec(cmd + srcName + extension, null, directPath);
				
				InputStream inputStream = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
					
				// give input(s) to process.
				OutputStream out = process.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
				writer.write(param);
				writer.flush();
					
				// print the output from process p.
				int n1;
				char[] c1 = new char[1024];
				StringBuffer standardOutput = new StringBuffer();
				while ((n1 = isr.read(c1)) > 0) {
					standardOutput.append(c1, 0, n1);
				}
						
				if(programLanguage.equalsIgnoreCase("scheme")){
					File delete = new File(this.rootPath + "\\" + srcName + extension);
					delete.delete();
				}
				
				//String _return = new String(standardOutput.toString().getBytes("UTF-8"), "UTF-8");
				
				return standardOutput.toString();
			} else
				return "compile_error...\n";
			
	}
	
	public int getSubmitTime(String src) throws IOException, Exception{
	
		//fileEncrypt = new Encryption();
		//fileEncrypt.decrypt(project.getRootPath() + "\\" + task.getProperty("ExName") + ".cond2.enc");
		//fileEncrypt.decrypt(project.getRootPath() + "\\" + task.getProperty("ExName") + ".exam.enc");
		Encryption fileEncrypt = new Encryption();
		fileEncrypt.decrypt(this.rootPath + "\\" + src + ".enc");
	
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.rootPath + "\\" + src), "utf-8"));
		int _return = 1;
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
						
			while(line != null){
				if(line.trim().startsWith("*")){
					if(isNumber(line.replaceAll("\\*", "").trim()))
						_return = Integer.parseInt(line.replaceAll("\\*", "").trim());
					else if(line.replaceAll("\\*", "").trim().length() == 0)
						_return = -1;
				}
				line = br.readLine();
			}
			return _return;
		} finally {
			br.close();
			File del = new File(this.rootPath + "\\" + src);
			del.delete();
		}		
		
	}
	
	// read exam file for get parameter
	public ArrayList<String> readFile(String src, String flag) throws IOException{
		//submitTime = 1;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.rootPath + "\\" + src), "utf-8"));
	
		try {
			StringBuilder sb = new StringBuilder();
			StringBuilder eb = new StringBuilder();
			String line = br.readLine();
			ArrayList<String> parameter = new ArrayList<String>();
			errorHint = new ArrayList<String>();
			
			while(line != null){
				if(line.equals(flag) || line == null){
					parameter.add(sb.toString());
					sb.setLength(0);
					errorHint.add(eb.toString());
					eb.setLength(0);
				} else if(!line.trim().startsWith("*") && !line.trim().startsWith("%")){
					sb.append(line);
					sb.append("\n");
				} else if(line.trim().startsWith("%")){
					eb.append(line.trim().substring(1));
					eb.append("\n");
				}
				line = br.readLine();
			}
			return parameter;
		} finally {
			br.close();
		}
	}
	
	public ArrayList<String> getErrorHint(){
		return errorHint;
	}
	
	public Boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
		} catch(Exception e){
			return false;
		}
		return true;
	}
	
	public void interrupt(){
		/*Runnable runnable = new Runnable() {
			public void run() {
				process.destroy();
			}
		};
	
		if (this.process != null) {
			new Thread(runnable).start();			
		}*/
		if(process != null) {
			process.destroy();
			process = null;
		}
	}

}