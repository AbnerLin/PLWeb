<%
import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.util.HashMap
import java.util.Map
import org.json.simple.JSONValue
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException


helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')
utype = helper.sess('utype')
course_id = request.getParameter('course_id')
lesson_id = request.getParameter('lesson_id')

if(!session || utype != 'T'){
        response.sendRedirect('/permission_denied.groovy')
        return;
}

sql = new Sql(helper.connection)

row = sql.firstRow("SELECT GRADE_SET, TOTAL_SET FROM GRADE_SETTING WHERE COURSE_ID=? AND LESSON_ID=?", [course_id, lesson_id])
//print row.GRADE_SET
//print "<br>"

JSONParser parser = new JSONParser()
JSONObject _gradeSet = (JSONObject) parser.parse(row.GRADE_SET)


if(row.TOTAL_SET){
        total = (JSONObject) parser.parse(row.TOTAL_SET)
} else {
        total = new JSONObject();
        for(i = 0; i < _gradeSet.size(); i++){
                total.put(i + 1, 0)
        }
}

%>


<html>
<head>
<link rel="stylesheet" type="text/css" href="/css/setGrade.css">
<script type="text/javascript" src="/js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="/js/setGrade.js"></script>
<script language="javascript" type="text/javascript">
<%
	print 'var course_id = ' + course_id + ';'
	print 'var lesson_id = ' + lesson_id + ';'
%>
</script>
</head>
<body>
<table id="grade_setting">
    <tr><td>#</td><td>參數配分</td><td>總配分</td></tr>
        <%for(i = 0; i < _gradeSet.size(); i++){
           _obj = (JSONObject) parser.parse(_gradeSet.get(String.valueOf(i+1)).toString())
          %>
          <tr>
                <td>${i + 1}</td>
                <td class="paramClass">
                <% for(j = 0; j < _obj.size(); j++){ %>
                        參數 ${j + 1}. <input type="text" no=${j + 1} value=${_obj.get(String.valueOf(j + 1))}>%
                        <br>
                <% } %>
                </td>
                <td><input class="totalGrade" type="text" no=${i + 1} value=${total.get(String.valueOf(i+1))}>%</td>
          </tr>
        <%}%>
</table>

<a id="autoSet" href="#">自動配分</a> <a id="gradeSubmit" href="#">儲存</a>
</body>
</html>
