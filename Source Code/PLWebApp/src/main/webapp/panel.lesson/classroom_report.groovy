import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.json.simple.JSONValue
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.text.SimpleDateFormat

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

helper = new CommonHelper(request, response, session)

uid   = session.get('uid')
uname = session.get('uname')
utype	= session.get('utype')

class_id  = request.getParameter('class_id')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

time = request.getParameter('time')
showTimeCol = (request.getParameter('time')=='col')
showTimeRow = (request.getParameter('time')=='row')

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.connection)

query1 = """
select TEXT_XML
from COURSE_FILE
where COURSE_ID=?
and LESSON_ID=?
"""

query2 = """
select U.USER_ID, U.NAME, U.ENROLLMENT
from ST_USER U
inner join USER_CLASS UC
on U.USER_ID=UC.USER_ID
where CLASS_ID=?
and UC.IS_TEACHER=?
order by U.ENROLLMENT
"""

stGrade = new HashMap()

query = """ SELECT USER_ID, GRADE FROM ST_GRADE WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? """

sql.eachRow(query, [class_id, course_id, lesson_id]){
        stGrade.put(it.USER_ID, it.GRADE)
}

text_xml = sql.firstRow(query1, [course_id, lesson_id]).text_xml

dataXml = new XmlParser().parseText(text_xml)

course_title = dataXml.title[0].text()

class_name = sql.firstRow('SELECT CLASS_NAME from ST_CLASS WHERE CLASS_ID=?', [class_id]).CLASS_NAME.toString()

duedate = sql.firstRow("""
	select DUEDATE
	from CLASS_COURSE
	where CLASS_ID=?
	and COURSE_ID=?
	and LESSON_ID=?
""", [
	class_id,
	course_id,
	lesson_id
]).DUEDATE

if (!duedate) {
	duedate = 0
}

STUDENTLIST = []
sql.eachRow(query2, [class_id, 'n']) {
	STUDENTLIST << [id:it.USER_ID, name:it.NAME, enrollment:it.ENROLLMENT]
}

QUERYTIME = 0
SQLDATA = new HashMap()
L_TIMEDATA = new HashMap()
Q_TIMEDATA = new HashMap()
TOTAL_TIME_USED = 0
MAX_TIME_USED = 0
AVG_TIME_USED = 0

try {
	QUERYTIME = new Date().time
	
	sql.eachRow("""
	select a.USER_ID,
		a.QUESTION_ID,
		a.IS_PASSED,
		a.TYPE,
		a.TIME_PASSED,
		a.TIME_UPDATED,
		a.TIME_USED,
		a.IS_REVIEWED,
		COUNT(QUESTION_ID) AS COUNT
	from (SELECT * FROM ST_REPORTS WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? ORDER BY REPORT_ID DESC) a
	where a.CLASS_ID=?
	and a.COURSE_ID=?
	and a.LESSON_ID=?
	GROUP BY USER_ID, QUESTION_ID
	order by a.USER_ID, a.QUESTION_ID
""", [class_id, course_id, lesson_id, class_id, course_id, lesson_id]) {
		if (!SQLDATA.containsKey(it.USER_ID)) {
			SQLDATA.put(it.USER_ID, new HashMap())
		}
		
		qid = it.QUESTION_ID.toInteger()
		
		COURSEDATA = SQLDATA.get(it.USER_ID)
		COURSEDATA.put(qid, [TYPE: it.TYPE, TIME_PASSED: it.TIME_PASSED, IS_PASSED: it.IS_PASSED, TIME_UPDATED: it.TIME_UPDATED, TIME_USED: it.TIME_USED, IS_REVIEWED: it.IS_REVIEWED, COUNT: it.COUNT])
		
		TIME_USED = it.TIME_USED?it.TIME_USED.toInteger():0
		
		SPEC_TIME_USED = TIME_USED
		
		if (!L_TIMEDATA.containsKey(it.USER_ID)) {
			L_TIMEDATA.put(it.USER_ID, TIME_USED)
		}
		else {
			SPEC_TIME_USED = L_TIMEDATA.get(it.USER_ID) + TIME_USED
			L_TIMEDATA.put(it.USER_ID, SPEC_TIME_USED)
		}
		
		if (SPEC_TIME_USED > MAX_TIME_USED) {
			MAX_TIME_USED = SPEC_TIME_USED
		}
		
		TOTAL_TIME_USED += TIME_USED
		
		if (!Q_TIMEDATA.containsKey(qid)) {
			Q_TIMEDATA.put(qid, TIME_USED)
		}
		else {
			if (TIME_USED > Q_TIMEDATA.get(qid)) {
				Q_TIMEDATA.put(qid, TIME_USED)
			}
		}
	}

	QUERYTIME = new Date().time - QUERYTIME
}
catch(e) {
	print e.message
}

if (TOTAL_TIME_USED > 0 && L_TIMEDATA.size() > 0) {
	AVG_TIME_USED = (TOTAL_TIME_USED/L_TIMEDATA.size()).toInteger()
}
else {
	AVG_TIME_USED = 0
}

row = sql.firstRow(""" SELECT * FROM GRADE_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """, [course_id, lesson_id])
if(row)
        isGradeSet = true;
else
        isGradeSet = false;

sdf1 = new SimpleDateFormat('yyyy/MM/dd, HH:mm:ss')

duringTime = sql.firstRow(""" SELECT BEGINDATE, DUEDATE FROM CLASS_COURSE WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? """, [class_id, course_id, lesson_id])

beginDate = ''
if(duringTime.BEGINDATE)
    beginDate = sdf1.format(new Date(duringTime.BEGINDATE.toLong()))

dueDate = ''
if(duringTime.DUEDATE)
    dueDate = sdf1.format(new Date(duringTime.DUEDATE.toLong()))

dNow = new Date()
ft = new SimpleDateFormat("yyyy/MM/dd, hh:mm:ss")
dNow = ft.format(dNow)

sql.close()

html.setDoubleQuotes(true)
html.html {
	head {
		title('查看狀態')
		base(href: helper.basehref())
		script(type: 'text/javascript', src: 'view_lamp.js', '')
		script(type: 'text/javascript', src: 'js/jquery-1.11.1.js', '')
        script(type: 'text/javascript', src: 'js/alertEffect.js', '')
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {
		if (utype == 'T') {
  		input(type: "button", id:"refresh", value:"Refresh Now", class:"button", onclick:'javascript:refresh()')
		  
		h2('Classroom Learning Report')
		
		div {
			span("${class_name} (${class_id})")
			br()
		}

  		div {
  			href_time_col	= "${helper.basehref}panel.lesson/classroom_report.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}&time=col"
  			href_time_row	= "${helper.basehref}panel.lesson/classroom_report.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}&time=row"
  			href_time_none	= "${helper.basehref}panel.lesson/classroom_report.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}"
  			
  			span ('Time Detail: ')
  			span ('[')
  			if (showTimeCol) {
  				Strong ('Column')	  			
  			}
  			else {
  				a (href: href_time_col) {
	  				span ('Column')
	  			}
  			}
  			span (' | ')
  			if (showTimeRow) {
  				Strong ('Row')	  			
  			}
  			else {
  				a (href: href_time_row) {
	  				span ('Row')
	  			}
  			}
  			span (' | ')
  			if (!showTimeCol && !showTimeRow) {

				strong ('None')
  			}
  			else {
  	  			a (href: href_time_none) {
  	  				span ('None')
  	  			}
  			}
  			span (']')
  		}
  		
  		h3("${course_title}")
		
		span("${beginDate}　~　${dueDate}", style: 'color: red; font-size: 16px')
        br()
		span("Current time: ${dNow}", style: 'font-size: 16px')
        br()
		
		
		a(href: 'javascript:void(0)', id: 'hideInfo'){
            span('[Hide student\'s information]')
        }
        a(href: 'javascript:void(0)', id: 'showInfo'){
            span('[Show student\'s information]')
        }
		div(id: 'detail') {
        }
		div (class: 'light-color') {
			img(src:'light/00FF00.png')
			span('on time')
			img(src:'light/556B2F.png')
			span('late')
			img(src:'light/FFFF00.png')
			span('editing')
			img(src:'light/FF0000.png')
			span('error')
			img(src:'light/AAAAAA.png')
			span('havn\'t tried')
		}

		table (class: 'classroom-report', width: '100%') {
			tr {
				th (class: 'small', width: 20, '#')
				th (class: 'small', width: 90, 'Enrollment ID')
				if(isGradeSet){
					th(class: 'small', width: 90, 'System Grade')
					th(class: 'small', width: 90, 'Manual Grade')
				}
				th (class: 'small stuInfo', width: 50, 'Student Name')
				th (class: 'small stuInfo', width: 90, 'User ID')
				th (class: 'small stuInfo', width: 40, 'Edit')
				th ('Status Report')
				th (width: 110, 'Time')
				/*
				th (width:60, '#total')
				th (width:60, '#tested')
				th (width:60, '#test_ok')
				*/
			}
			
			//task id cache
			task_id_list = []
			dataXml.task.each {
				task ->
				task_id_list << task.id[0].text().toInteger()
			}
			
			c = 0
			STUDENTLIST.each {
				student ->
				
				href_view = "${helper.basehref}panel.class/student_report.groovy?class_id=${class_id}&user_id=${student.id}"
				href_edit = "${helper.basehref}webstart.groovy?mode=teacher&course_id=${course_id}&lesson_id=${lesson_id}&class_id=${class_id}&user_id=${student.id}"
				
				c++
				tr(class: c%2==0?'even':'odd') {
					td(align: 'center', "${c}")
					td(align: 'center', "${student.enrollment}")
					
					if(isGradeSet){
                        sql = new Sql(ds.connection)

                        gradeSet = sql.firstRow(""" SELECT GRADE_SET, TOTAL_SET FROM GRADE_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """, [course_id, lesson_id])
                        stuGrade = sql.firstRow(""" SELECT GRADE, MANUAL_GRADE FROM ST_GRADE WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """, [class_id, course_id, lesson_id, student.id])
                        sql.close()

                        finalStuGrade = 0
                        JSONParser parser = new JSONParser()
                        JSONObject _gradeSet = (JSONObject) parser.parse(gradeSet.GRADE_SET)
                        totalFlag = true;
                        if(!stuGrade.equals(null)){
                            JSONObject _stuGrade = (JSONObject) parser.parse(stuGrade.GRADE)
                                if(!gradeSet.TOTAL_SET.equals(null)){
                                    //finalStuGrade = _totalSet.get(String.valueOf(1)).toString()
                                }else
                                    totalFlag = false;
                                for(i = 0; i < _stuGrade.size(); i++){
                                    tmpGrade = 0
                                    tmpObj = _stuGrade.get(String.valueOf(i+1)).toString()
                                    if(tmpObj.equals("false"))
                                        continue;
                                    else {
                                        _tmpObj = (JSONObject) parser.parse(tmpObj)
										for(int j = 0; j < _tmpObj.size(); j++){
											if(_tmpObj.get(String.valueOf(j+1)).toString().equals("false")){
												continue;
											} else {
												_tmpGradeSet = _gradeSet.get(String.valueOf(i+1)).toString()
												grade = (JSONObject) parser.parse(_tmpGradeSet)
												tmpGrade += Float.valueOf(grade.get(String.valueOf(j+1)).toString())
											}
										}
                                    }
									if(totalFlag){
										_totalSet = (JSONObject) parser.parse(gradeSet.TOTAL_SET)
										tmpGrade = Float.valueOf(_totalSet.get(String.valueOf(i + 1)).toString()) * tmpGrade / 100
									}else
										tmpGrade = 0

                                        finalStuGrade += tmpGrade
                                }
								if(finalStuGrade != 0)
									finalStuGrade = String.format("%.2f", finalStuGrade)
								
						}	
						td(align: 'center', "${finalStuGrade}")
						if(stuGrade.equals(null) || (stuGrade.MANUAL_GRADE) == null)
							manualGrade = finalStuGrade
                        else
                            manualGrade = stuGrade.MANUAL_GRADE
							
                        td(align: 'center', width: '100'){
                            input(type: 'text', value: "${manualGrade}", class: 'manualGrade')
                            input(type: 'button', class: 'submitStuGrade', value: 'OK', classId: class_id, courseId: course_id, lessonId: lesson_id, userId: student.id)
                        }

					}
					
					td (class: 'stuInfo'){
						a (href: href_view, student.name)
					}
					td (align: 'center', "${student.id}", class: 'stuInfo')
					td (align: 'center', class: 'stuInfo') {
						a (href: href_edit, class: 'icon') {
							img (src: 'icons/plugin_edit.png', border: 0)
						}
					}

					total = 0
					test = 0
					ok = 0
					
					td {			
						task_id_list.each {
							qid ->
							
							font_color = '000000'
							
							height = 0
							
							time_used = 0
							
							description = new StringBuilder()
							
							if (!SQLDATA.get(student.id) || !(q = SQLDATA.get(student.id).get(qid))) {
								description.append "未練習"
								font_color = 'AAAAAA'
							}
							else if ('test_ok'.equalsIgnoreCase(q.TYPE)) {
								if (q.TIME_PASSED <= duedate) {
									description.append "通過測試 "
									font_color = '00FF00'
								}
								else {
									description.append " 遲交  "
									description.append helper.getDatetimeString(new Date(q.TIME_PASSED.toLong()))

									font_color = '556B2F'
								}
								ok ++
								test ++
								
								time_used = q.TIME_USED
							}
							else {
								description.append " 狀態 "
								description.append q.TYPE
								
								font_color = 'FF0000'
								test ++
								
								time_used = q.TIME_USED
							}
							
							href_player = sprintf("${helper.basehref}report/code_detail.groovy?class_id=%s&course_id=%s&lesson_id=%s&task_id=%s&user_id=%s",
									class_id,
									course_id,
									lesson_id,
									qid,
									student.id)
									
							a (href: href_player, target: '_blank', style: 'text-decoration: none', title: description.toString()) {
								
								if (showTimeCol) {
									if (!time_used || !Q_TIMEDATA.get(qid)) {
										pect = 0
									}
									else {
										pect = ((time_used/Q_TIMEDATA.get(qid))*100).toInteger()
									}
									img (src: "light/${font_color}_${pect}.png", border: 0)
								}
								else if (showTimeRow) {
									if (!time_used || !L_TIMEDATA.get(student.id)) {
										pect = 0
									}
									else {
										pect = ((time_used/L_TIMEDATA.get(student.id))*100).toInteger()
									}
									img (src: "light/${font_color}_${pect}.png", border: 0)
								}
								else {
									img(src:"light/${font_color}.png", border:0)
								}
							}
							
							if(!isGradeSet) {
								if (!SQLDATA.get(student.id) || !(q = SQLDATA.get(student.id).get(qid))) {
									print "0"
								} else
									print q.COUNT
							}
							
							total ++
						}
						
						
						int _studentId = student.id
						if(isGradeSet){
							print "<br><hr>"

							if(stGrade.containsKey(_studentId)){
								JSONParser parser = new JSONParser()
								try {
									JSONObject _grade = (JSONObject) parser.parse(stGrade.get(_studentId));

									for(int i = 1; i <= _grade.size(); i++){
										if(_grade.get(String.valueOf(i)).toString().equals("false")) {
											font_color = 'AAAAAA'
										} else {
											Boolean flag = true;
											JSONObject _tmp = (JSONObject) parser.parse(_grade.get(String.valueOf(i)).toString());
											for(int j = 1; j <= _tmp.size(); j++){
												if(_tmp.get(String.valueOf(j)).toString().equals("false")){
													font_color = 'FF0000'
													flag = false;
													break;
												}
											}
											if(flag && _tmp.size() != 0)
												font_color = '00FF00'
										}
										img(src:"light/${font_color}.png", border:0)
									}

									System.out.println(_grade.size());
								} catch (ParseException e) {
										e.printStackTrace();
								}

							} else {
								print "未開啟編輯器"
							}
						}
						
						sql = new Sql(ds.connection)
                        row = sql.firstRow(""" SELECT ISNEEDHELP FROM ST_MASTERY WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """, [class_id, course_id, lesson_id, student.id])
                        if(row && (row.ISNEEDHELP).equalsIgnoreCase("y"))
							img (src: "../icons/exclamation.png", align: 'right', class: 'needHelp')
                        sql.close()
								
					}	
					
					td {
						if (!L_TIMEDATA.get(student.id) || !MAX_TIME_USED || !AVG_TIME_USED) {
							pect = 0
						}
						else {
							avg_dist = L_TIMEDATA.get(student.id) - AVG_TIME_USED
							if (avg_dist == 0) {
								pect = 50
							}
							else if (avg_dist > 0) {
								pect = 50+((avg_dist/(MAX_TIME_USED-AVG_TIME_USED))*50).toInteger()
							}
							else /* < 0 */ {
								//pect = ((L_TIMEDATA.get(student.id)/AVG_TIME_USED)*50).toInteger()
								pect = 50+((avg_dist/AVG_TIME_USED)*50).toInteger()
							}
						}
						a (href: '#') {
							img (src: "light/SMALLTIME_${pect}.png", border: 0)
						}
					}
					/*
					td (align:'center', total)
					td (align:'center', test)
					td (align:'center', ok)
					*/
				}
			}
		}
		
		span ("Query Time: ${QUERYTIME} ms")
		}
		else {
			h2("※請勿作弊!!")
		}
	}
}
