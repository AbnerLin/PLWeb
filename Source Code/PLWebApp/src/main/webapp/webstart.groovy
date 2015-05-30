import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendError 403
	return
}

def uid	  = session.get('uid')
def uname = session.get('uname')
def utype = session.get('utype')
def department = session.get('department')
def enrollment = session.get('enrollment')

if (!uid) {
	response.sendError 403
	return
}

def course_id = request.getParameter('course_id')
def class_id  = request.getParameter('class_id')
def lesson_id = request.getParameter('lesson_id')
def mode      = request.getParameter('mode')
def isExam    = false

if(request.getParameterMap().containsKey("isExam")){
  isExam = true
}

def cdn = helper.fetch('cdn')!=null

// 80 port防止存取課號為5(測驗課號)
/*
if (class_id != null) {
	if (class_id[4..4].equals('5')) {
		response.sendRedirect('permission_denied.groovy')
		return;
	}
}
*/

if (['T'].contains(utype) && mode == 'teacher') {
	if (request.getParameter('user_id')) {
		uid = request.getParameter('user_id')
	}
}

deployPath = request.getRealPath('/plwebstart')
packPath = request.getRealPath('/suite')
plugPath = request.getRealPath('/suite/plugins')
dataPath = request.getRealPath('/data')

def sql = new Sql(helper.connection)

def server_host = request.serverName
def server_port = request.serverPort

def codebase = "http://${server_host}:${server_port}/plwebstart/"

/*
if (cdn && server_host != 'localhost') {
	codebase = "http://cloud1.plweb.org/plwebstart/"
}
*/

version  = "suite-20080918001"

//產生WS_TICKET
session_id  = session.id
ticket_no   = new Date().time
ticket_date = new Date().time

try {
	
	data = [
		'SESSION_ID':	session_id,
		'TICKET_NO':	ticket_no,
		'TICKET_DATE':	ticket_date,
		'USER_ID':		uid,
		'COURSE_ID':	course_id,
		'CLASS_ID':		class_id,
		'LESSON_ID':	lesson_id,
		'LESSON_MODE':	mode,
		'REMOTE_AGENT':	helper.getHeader('User-Agent'),
		'REMOTE_IP':	helper.remoteAddr,
		'REMOTE_HOST':	helper.remoteHost,
		'SERVER_HOST':	helper.serverName,
		'SERVER_PORT':	helper.serverPort
	]

	// write WS_TICKET
	// insert
	helper.simpleSqlInsert 'WS_TICKET', data, sql
}
catch (e) {
	println e
}

course_name = sql.firstRow("""
	select COURSE_NAME
	from COURSE
	where COURSE_ID=?
""", [course_id]).course_name

sql.close()

lesson_xml  = "http://${server_host}:${server_port}/ServerLesson.groovy?s=${session_id}&t=${ticket_no}"
request_url = "http://${server_host}:${server_port}/ServerRequest.groovy?s=${session_id}&t=${ticket_no}"
dataSettingUrl = "http://${server_host}:${server_port}/dataSetting.groovy"

props = [
 	"javaws.plweb.urlpackage"	    : "${codebase}core/jedit-package-4.5.zip",
 	"javaws.plweb.urlpackage_asc"	: new Date().time,
    "javaws.plweb.urllesson"	    : "${lesson_xml}",
 	"javaws.plweb.urlrequest"	    : "${request_url}",
	"javaws.plweb.urlDataSetting"	: "${dataSettingUrl}",
 	"javaws.plweb.lessonpath"	    : "${uid}/${class_id}/${course_name}/${lesson_id}",
 	"javaws.plweb.lessonfile"	    : "lesson${lesson_id}.xml",
 	"javaws.plweb.lessonxml"	    : "${uid}/${class_id}/${course_name}/lesson${lesson_id}.xml",
 	"javaws.plweb.lessonid"	    : "${lesson_id}",
 	"javaws.plweb.lessonmode"	    : "${mode}",
 	"javaws.plweb.jeditpath"	    : "jEdit",
 	"javaws.plweb.pluginpath"	    : "jEdit/workspace/jars",
 	"javaws.plweb.adimage"			: "http://${server_host}:${server_port}/ad-image/plweb-ad.png",
 	"javaws.plweb.adurl"			: "http://${server_host}:${server_port}/",
	
	"javaws.plweb.department"		: CH_encoding(department),
	"javaws.plweb.enrollment"		: "${enrollment}",
	"javaws.plweb.uname"			: CH_encoding(uname),
	"javaws.plweb.isExam"			: isExam,
	
 	"javaws.plweb.var.user_id"		: uid,
 	"javaws.plweb.var.user_name"	: uname,
 	"javaws.plweb.var.course_id"	: course_id,
 	"javaws.plweb.var.lesson_id"	: lesson_id,
 	"javaws.plweb.var.class_id"	: class_id,

    "file.encoding"         : 'UTF-8',
    "user.language"         : 'zh',
    "user.region"           : 'TW',
    "javaws.user.language"         : 'zh',
    "javaws.user.region"           : 'TW'
]

props_windows = [
	"javaws.plweb.diskroot"	    : "C:/myplweb",
	"javaws.plweb.shell"			: "cmd /C",
	"javaws.plweb.explorer"		: "start explorer /root, \${root}"
]

props_linux = [
	"javaws.plweb.diskroot"	    : "myplweb",
	"javaws.plweb.shell"			: "bash -c",
	"javaws.plweb.explorer"		: "nautils \${root}"
]

props_mac = [
    "javaws.plweb.diskroot"	    : "myplweb",
	"javaws.plweb.shell"			: "bash -c",
	"javaws.plweb.explorer"		: "nautils \${root}",
    'sun.awt.disableMixing' : 'true'
]

c = 0
new File(deployPath, 'plugins').eachFileMatch(~/.*\.jar/) {
    file->
	props.put("javaws.plweb.plugins.${c}", "${codebase}plugins/${file.name}")
	props.put("javaws.plweb.plugins_asc.${c}", new Date().time)
	c++
}

if (request.getParameter('debug')==null) {
    response.setContentType('application/x-java-jnlp-file; charset=UTF-8')
    response.setHeader("Content-disposition", "inline; filename=webstart.jnlp")
}
else {
    response.setContentType('text/xml; charset=UTF-8')
}

//response.setContentType("text/plain; charset=UTF-8")

println '<?xml version="1.0" encoding="UTF-8"?>';

xml = new MarkupBuilder(response.getWriter())
xml.setDoubleQuotes(true);
xml.jnlp(spec: '1.6+', codebase: codebase) {
	information() {
		title("Programming Teaching Assistant")
		vendor("PLWeb")
		description("Programming Teaching Assistant")
	}
	security() {
		"all-permissions"()
	}
	resources() {
		j2se(version: '1.6+')
		jar (href: 'core/plwebstart-1.0.jar')
		//jar(href: 'suite-webstart.jar')
		//jar(href: 'suite-common.jar')
		//jar(href: 'suite-jedit.jar')
		
		/* Apache Ant Library */
		//jar(href: 'ant.jar')
		//jar(href: 'ant-launcher.jar')
		
		/* Apache Commons Library */
		//jar(href: 'commons-httpclient-3.1.jar')
		//jar(href: 'commons-io-1.4.jar')
		//jar(href: 'commons-lang-2.5.jar')
		//jar(href: 'commons-logging-1.1.1.jar')
		//jar(href: 'commons-logging-api-1.1.1.jar')
		//jar(href: 'commons-logging-adapters-1.1.1.jar')
		//jar(href: 'commons-codec-1.3.jar')

		//jar(href: 'DJNativeSwing.jar')
		//jar(href: 'jna.jar')
		//jar(href: 'jna_WindowUtils.jar')
		//jar(href: 'FCKeditor_2.6.zip')
		//jar(href: 'DJNativeSwingDemo.jar')
		
        new File(deployPath, 'libs').listFiles().each {
            file->
            if (file.name.endsWith('.jar')) {
                jar (href: "libs/${file.name}")
            }
        }

		props.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Windows', arch: 'amd64') {
		jar (href: 'swt/swt-3.7.1-win32-win32-x86_64.jar')
		props_windows.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Windows', arch: 'x86_64') {
		jar (href: 'swt/swt-3.7.1-win32-win32-x86_64.jar')
		props_windows.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Windows', arch: 'x86') {
		jar (href: 'swt/swt-3.7.1-win32-win32-x86.jar')
		props_windows.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Linux', arch: 'x86_64') {
		jar (href: 'swt/swt-3.7.1-gtk-linux-x86_64.jar')
		props_linux.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Linux', arch: 'amd64') {
		jar (href: 'swt/swt-3.7.1-gtk-linux-x86_64.jar')
		props_linux.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Linux', arch: 'x86') {
		jar (href: 'swt/swt-3.7.1-gtk-linux-x86.jar')
		props_linux.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Mac', arch: 'x86_64') {
        j2se (version: '1.6+', 'java-vm-args': '-XstartOnFirstThread -Xdebug')
		jar (href: 'swt/swt-3.7.1-cocoa-macosx-x86_64.jar')
		props_mac.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Mac', arch: 'amd64') {
        j2se (version: '1.6+', 'java-vm-args': '-XstartOnFirstThread')
		jar (href: 'swt/swt-3.7.1-cocoa-macosx-x86_64.jar')
		props_mac.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Mac', arch: 'x86') {
        j2se (version: '1.6+', 'java-vm-args': '-XstartOnFirstThread')
		jar (href: 'swt/swt-3.7.1-cocoa-macosx.jar')
		props_mac.each {
			property(name: it.key, value: it.value)
		}
	}
    'application-desc' ('main-class': 'org.plweb.suite.webstart.RunEditor') {
		argument('-nosplash')
		argument('-Xnosplash')
        argument('-XstartOnFirstThread')
        argument('-Xdock:name=PLWebStart')
	}
}

def CH_encoding(str){
        String _encoding;
        if(str != null)
                _encoding = str.bytes.encodeBase64().toString();
        else {
                String _str = "null";
                _encoding = _str.bytes.encodeBase64().toString();
        }
        return _encoding;
}

