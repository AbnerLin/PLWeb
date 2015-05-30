import javax.naming.InitialContext
import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.util.HashMap
import java.util.Map
import org.json.simple.JSONValue
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.plweb.suite.common.xml.XmlFactory

helper = new CommonHelper(request, response, session)

action = helper.fetch('action')
classId = helper.fetch('classId')
courseId = helper.fetch('courseId')
lessonId = helper.fetch('lessonId')
userId = helper.fetch('userId')

sql = new Sql(helper.connection)

switch(action){
	case 'init':
		checkTime = """ SELECT COUNT(*) AS A FROM CLASS_COURSE WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND DUEDATE > ? AND BEGINDATE < ?"""

        Date date = new Date();
        row = sql.firstRow(checkTime, [classId, courseId, lessonId, date.getTime(), date.getTime()])
        if(row.A == 0){
            print "timeout"
            return
        }

		checkSql = """ SELECT GRADE FROM ST_GRADE WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """
		try {
			row = sql.firstRow(checkSql, [classId, courseId, lessonId, userId])
			if(!row.equals(null)){
				print row.grade
			} else {
				selectSql = """ SELECT GRADE_SET FROM GRADE_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """
				gradeSet = sql.firstRow(selectSql, [courseId, lessonId]).grade_set
				
				JSONParser parser = new JSONParser()
				JSONObject _gradeSet = (JSONObject) parser.parse(gradeSet)
				
				JSONObject _return = new JSONObject();
				for(i = 0; i < _gradeSet.size(); i++){
					_return.put(i + 1, false)
				}
				
				insertSql = """ INSERT INTO ST_GRADE(CLASS_ID, COURSE_ID, LESSON_ID, USER_ID, GRADE) VALUES(?, ?, ?, ?, ?) """
				sql.executeInsert(insertSql, [classId, courseId, lessonId, userId, _return.toString()])
				
				print _return.toString()
			}
		} catch(e){

		} catch(ParseException e) {
			e.printStackTrace()
		}
		break;
	case 'saveGrade':
		checkTime = """ SELECT COUNT(*) AS A FROM CLASS_COURSE WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND DUEDATE > ? AND BEGINDATE < ?"""

        Date date = new Date();
        row = sql.firstRow(checkTime, [classId, courseId, lessonId, date.getTime(), date.getTime()])
		
        if(row.A == 0){
            //print "timeout"
            return
        }
		
		updateSql = """ UPDATE ST_GRADE SET GRADE=? WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """
		grade = request.getParameter('grade')
		sql.executeUpdate(updateSql, [grade, classId, courseId, lessonId, userId])	
		break;
	
	case 'saveMasteryGrade':
		updateSql = """ UPDATE ST_MASTERY SET MASTERY_GRADE=?, ISNEEDHELP=? WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """
		masteryGrade = request.getParameter('masteryGrade')
		isNeedHelp = request.getParameter('isNeedHelp')
		sql.executeUpdate(updateSql, [masteryGrade, isNeedHelp, classId, courseId, lessonId, userId])
		break;
		
	case 'setGrade':
		utype = helper.sess('utype')
		if(!session || utype != 'T'){
			response.sendRedirect('/permission_denied.groovy')
			return;
		}
		gradeSet = request.getParameter('gradeSet')
		totalSet = request.getParameter('totalSet')
		updateSql = """ UPDATE GRADE_SETTING SET GRADE_SET=?, TOTAL_SET=? WHERE COURSE_ID=? AND LESSON_ID=? """
		sql.executeUpdate(updateSql, [gradeSet, totalSet, courseId, lessonId])
		break;
	case 'updateManualGrade':
		utype = helper.sess('utype')
		if(!session || utype != 'T'){
			response.sendRedirect('/permission_denied.groovy')
			return;
		}
		manualGrade = request.getParameter('manualGrade')

		updateSql = """ UPDATE ST_GRADE SET MANUAL_GRADE=? WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """
		
		sql.executeUpdate(updateSql, [manualGrade, classId, courseId, lessonId, userId])
		break;
	case 'getMasterySet':
		checkSql = """ SELECT MASTERY_SETTING FROM MASTERY_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """
		try{
			row = sql.firstRow(checkSql, [courseId, lessonId])
			print row.MASTERY_SETTING			
		} catch(e) {
		}
		
		
		break;
		
	case 'getStuMastery':
	
		allAvgTime = """ SELECT QUESTION_ID, AVG(TIME_USED) as AVG FROM ST_REPORTS WHERE COURSE_ID=? AND LESSON_ID=? AND TEST_OK=1 GROUP BY QUESTION_ID """
        /* get last year */
        lastAvgTime = """ SELECT QUESTION_ID, AVG(TIME_USED) as AVG FROM ST_REPORTS, CLASS_COURSE WHERE ST_REPORTS.CLASS_ID=CLASS_COURSE.CLASS_ID AND ST_REPORTS.COURSE_ID=? AND ST_REPORTS.LESSON_ID=? AND CLASS_COURSE.BEGINDATE>? AND TEST_OK=1 GROUP BY QUESTION_ID """

        try {
			Date tmp = new Date("1/1/" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1))
            Calendar cal = Calendar.getInstance()
            cal.setTime(tmp);
            long millis = cal.getTimeInMillis()

            JSONObject _return = new JSONObject()
            JSONObject masteryTime = new JSONObject()
            JSONObject stuRecord = new JSONObject()

            lastAvgTimeRows = sql.rows(lastAvgTime, [courseId, lessonId, millis])
            allAvgTimeRows = sql.rows(allAvgTime, [courseId, lessonId])

            int x = 2
            if((lastAvgTimeRows.size() == 0 && allAvgTimeRows.size() > 0) || (lastAvgTimeRows.size() > 0 && allAvgTimeRows.size() == 0))
                x = 1
            else if(lastAvgTimeRows.size() == 0 && allAvgTimeRows.size() == 0)
                x = 0

            for(i = 0; i < allAvgTimeRows.size(); i++){

                if(lastAvgTimeRows.AVG[i] == null)
                    lastAvg = 0
                else
                    lastAvg = (int)lastAvgTimeRows.AVG[i]
                if(allAvgTimeRows.AVG[i] == null)
                    allAvg = 0
                else
                    allAvg = (int)allAvgTimeRows.AVG[i]

                masteryTime.put(allAvgTimeRows.QUESTION_ID[i], (int)((lastAvg + allAvg)/x/1000));
            }

            if(masteryTime.size() > 0)
                _return.put("MasteryTime", masteryTime)
            else
                _return.put("MasteryTime", "false")

            checkSql = """ SELECT MASTERY_GRADE FROM ST_MASTERY WHERE CLASS_ID=? AND COURSE_ID=? AND LESSON_ID=? AND USER_ID=? """
            row = sql.firstRow(checkSql, [classId, courseId, lessonId, userId])
            getMastery = """ SELECT MASTERY_SETTING FROM MASTERY_SETTING WHERE COURSE_ID=? AND LESSON_ID=? """
            _row = sql.firstRow(getMastery, [courseId, lessonId])
            JSONParser parser = new JSONParser()
            _return.put("seq", (JSONObject) parser.parse(_row.MASTERY_SETTING))
            if(!row.equals(null)){
                _return.put("stuRecord", (JSONObject) parser.parse(row.MASTERY_GRADE))
            } else {
                JSONObject _masterySet = (JSONObject) parser.parse(_row.MASTERY_SETTING)

                for(i = 1; i <= _masterySet.size(); i++){
                    tmpString = _masterySet.get(String.valueOf(i))
                    String[] tmpArray = tmpString.split(", ")
                    JSONObject content = new JSONObject()
                    for(j = 0; j < tmpArray.size(); j++){
                        content.put(tmpArray[j], "false")
                    }
                    content.put("isPass", "false")
                    stuRecord.put(i, new JSONObject(content))
                }
                _return.put("stuRecord", stuRecord)
                insertMasterySet = """ INSERT INTO ST_MASTERY(CLASS_ID, COURSE_ID, LESSON_ID, USER_ID, MASTERY_GRADE, ISNEEDHELP) VALUES(?, ?, ?, ?, ?, ?) """
                sql.executeInsert(insertMasterySet, [classId, courseId, lessonId, userId, stuRecord.toString(), "n"])
            }

            print _return.toString()
            } catch(e){
                        } catch(ParseException e){
            }
		break;
		case 'getSolution':
            taskSeq = request.getParameter('taskSeq')
            solutionSql = "SELECT TEXT_XML FROM COURSE_FILE WHERE COURSE_ID=? AND LESSON_ID=?"
            if(!classId[4..4].equals('6'))
                return

            solution = sql.firstRow(solutionSql, [courseId, lessonId]).TEXT_XML
            is = new ByteArrayInputStream(solution.getBytes("UTF-8"))
            project = XmlFactory.readProject(is)

            files = []

            project.tasks.each{
                task ->
                file_name = project.getTaskPropertyEx(task, 'file.main')
                file_main = project.getFile(file_name)

                files << [
                    name: file_name,
                    content: new String(file_main.decodedContent, 'UTF-8')
					//content: new String(file_main.decodedContent)
                ]
            }
			String strBase64 = files[Integer.valueOf(taskSeq)].content
			strBase64 = strBase64.bytes.encodeBase64().toString()
			
			
            print strBase64
            break;
		
		
}
sql.close()