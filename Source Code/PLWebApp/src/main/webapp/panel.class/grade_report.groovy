import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper
import org.json.simple.JSONValue
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException

//2014 10 14 


def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

def utype       = helper.sess('utype')

class_id = request.getParameter('class_id')

class_name = sql.firstRow(""" SELECT CLASS_NAME FROM ST_CLASS WHERE CLASS_ID=?""", [class_id]).CLASS_NAME

if (!utype.equals("T")) {
        println """權限不足，請重新<a href=\"${helper.basehref}login/\">登入</a>。"""
        return
}

// GET ALL EXAM LIST
_SQL_EXAM_LIST = """SELECT B.GRADE_SET, B.TOTAL_SET, C.TITLE, B.COURSE_ID, B.LESSON_ID FROM CLASS_COURSE AS A, GRADE_SETTING AS B, COURSE_FILE AS C WHERE A.CLASS_ID=? AND A.COURSE_ID=B.COURSE_ID AND A.LESSON_ID=B.LESSON_ID AND B.COURSE_ID=C.COURSE_ID AND B.LESSON_ID=C.LESSON_ID"""

EXAMLIST = []
sql.eachRow(_SQL_EXAM_LIST, [class_id]){
        EXAMLIST << [courseId:it.COURSE_ID, lessonId:it.LESSON_ID, title:it.TITLE, gradeSet:it.GRADE_SET, totalSet:it.TOTAL_SET]
}


//GET ALL MANUAL's GRADE
manualGrade = new HashMap()
_SQL_STUDENT_MANUAL_GRADE = """ SELECT COURSE_ID, LESSON_ID, USER_ID, MANUAL_GRADE FROM ST_GRADE WHERE CLASS_ID=?  """
sql.eachRow(_SQL_STUDENT_MANUAL_GRADE, [class_id]){
        manualGrade.put((String.valueOf(it.USER_ID)).concat(":").concat(String.valueOf(it.COURSE_ID)).concat(":").concat(String.valueOf(it.LESSON_ID)), it.MANUAL_GRADE)
}

// GET ALL STUDENT'S GRADE
stGrade = new HashMap()
_SQL_STUDENT_EXAM = """ SELECT COURSE_ID, LESSON_ID, USER_ID, GRADE FROM ST_GRADE WHERE CLASS_ID=? """
sql.eachRow(_SQL_STUDENT_EXAM, [class_id]){
        stGrade.put((String.valueOf(it.USER_ID)).concat(":").concat(String.valueOf(it.COURSE_ID)).concat(":").concat(String.valueOf(it.LESSON_ID)), it.GRADE)
}

// GET ALL STUDENT LIST
_SQL_USER_CLASS = """ SELECT U.USER_ID, U.NAME, U.ENROLLMENT FROM ST_USER AS U, USER_CLASS AS UC WHERE CLASS_ID=? AND U.USER_ID=UC.USER_ID AND UC.IS_TEACHER != 'y' order by U.ENROLLMENT"""

STUDENTLIST = []
sql.eachRow(_SQL_USER_CLASS, [class_id]){
        STUDENTLIST << [id:it.USER_ID, name:it.NAME, enrollment:it.ENROLLMENT]
}



html.setDoubleQuotes(true)
html.html{
        head {
                title('GRADE')
                base(href: helper.basehref())
                script(type: 'text/javascript', src: 'view_lamp.js', '')
                script(type: 'text/javascript', src: 'js/jquery-1.11.1.js', '')
                script(type: 'text/javascript', src: 'js/alertEffect.js', '')
                link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
        }
        body{
                input(type: "button", id:"refresh", value:"Refresh Now", class:"button", onclick:'javascript:refresh()')
                h2('Student\'s Grade')

                div{
                        span("${class_name} (${class_id})")
                        br()
                }
				
				a(href: 'javascript:void(0)', id: 'hideInfo', class: '_hideInfo'){
					span('[Hide student\'s information]')
				}
				a(href: 'javascript:void(0)', id: 'showInfo', class: '_showInfo'){
					span('[Show student\'s information]')
				}	
				a(href: 'javascript:void(0)', id: 'finalGrade'){
                    span('　[Final Grade]')
                }
                br()
				div(id: "finalGradeColumn")
				div{
                        span("調分： ")
                        input(type: 'text', id: 'adjustGrade')
                }
                br()

				
                table(class: 'classroom-report', id: 'reportTable'){
                        tr {
                                th (class: 'small', width: '20px', '#')
                                th (class: 'small', width: '90px', 'Enrollment ID')
                                th (class: 'small stuInfo', width: '80px', 'Student Name')
                                th (class: 'small stuInfo', width: '80px', 'User ID')
                                EXAMLIST.each{
                                        examList ->
                                                th (class: 'small', width: '100px', examList.title)
                                }
                                th (class: 'small', width: '80px', 'Average')
								th (class: 'small', width: '90px', 'Enrollment ID')
                        }
						
						tr(){
                                td(align: 'center', "設定配分", class: 'hideTd', colspan: '4')
								inputCount = 1
                                EXAMLIST.each{
                                        examList ->
                                        td(align: 'center'){
                                                input(type: 'text', class: 'finalGradeInput', id: "input_"+inputCount++, value: (int)(100/EXAMLIST.size()))
                                                print "%"
                                        }
                                }
                                td(align: 'center', "調分"){
                                        input(type: 'text', class: 'finalGradeInput', value: 100, id:'average')
                                        print "%"
                                }
								td("-", align: 'center')

                        }



                        stuCount = 0
                        STUDENTLIST.each {
                                student ->
                                        stuCount++
                                        tr(class: stuCount%2==0?'even start':'odd start'){
                                                td(align: 'center', "${stuCount}")
                                                td(align: 'center', "${student.enrollment}", class: 'finalColumn')
                                                td(align: 'center', class: 'stuInfo', "${student.name}")
                                                td(align: 'center', class: 'stuInfo', "${student.id}")
                                                examCount = 0
                                                averageGrade = 0
												
												EXAMLIST.each{
                                                        examList ->
                                                        _stGrade = stGrade.get(String.valueOf(student.id).concat(":").concat(String.valueOf(examList.courseId)).concat(":").concat(String.valueOf(examList.lessonId)))
                                                        _finalGrade = 0
														
														 _stuManualGrade = manualGrade.get(String.valueOf(student.id).concat(":").concat(String.valueOf(examList.courseId)).concat(":").concat(String.valueOf(examList.lessonId)))
                                                        if(_stuManualGrade != null){
                                                                _finalGrade = _stuManualGrade
																averageGrade += _finalGrade
                                                        } else if(_stGrade != null) {
                                                                JSONObject _stuJsonGrade = (JSONObject) new JSONParser().parse(_stGrade)
                                                                JSONObject gradeSet = (JSONObject) new JSONParser().parse(examList.gradeSet)
                                                                if(!examList.totalSet.equals(null)){
                                                                        for(i = 0; i < _stuJsonGrade.size(); i++){
                                                                                calcGrade = 0
                                                                                if(!_stuJsonGrade.get(String.valueOf(i+1)).toString().equals("false")){
                                                                                        _stuJsonGradeDetail = (JSONObject) new JSONParser().parse(_stuJsonGrade.get(String.valueOf(i+1)).toString())
                                                                                        for(j = 0; j < _stuJsonGradeDetail.size(); j++){
                                                                                                if(!_stuJsonGradeDetail.get(String.valueOf(j+1)).toString().equals("false")){
                                                                                                        _tmpGradeSet = gradeSet.get(String.valueOf(i+1)).toString()
                                                                                                        grade = (JSONObject) new JSONParser().parse(_tmpGradeSet)
                                                                                                        calcGrade += Float.valueOf(grade.get(String.valueOf(j+1)).toString())
                                                                                                }
                                                                                        }
                                                                                        totalSet = (JSONObject) new JSONParser().parse(examList.totalSet)
                                                                                        calcGrade = Float.valueOf(totalSet.get(String.valueOf(i+1)).toString()) * calcGrade / 100
                                                                                        _finalGrade += calcGrade
                                                                                }
                                                                        }
                                                                }
                                                                 averageGrade += _finalGrade
                                                                if(_finalGrade > 0)
                                                                        _finalGrade = String.format("%.2f", _finalGrade)
                                                                else
                                                                        _finalGrade = 0

                                                        }
                                                        examCount++
                                                        td(align: 'center', "${_finalGrade}", class: 'columnGrade', param: examCount)
                                                }
                                                 if(averageGrade > 0)
                                                        _finalGrade = String.format("%.2f", averageGrade / examCount)
                                                td(align: 'center', "${_finalGrade}", class: 'stuAverage finalColumn')
												td(align: 'center', "${student.enrollment}")
                                        }
                        }
                }

        }
}
