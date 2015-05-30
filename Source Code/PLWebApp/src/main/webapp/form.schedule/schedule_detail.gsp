<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <title>PLWeb - Schedule Detail</title>
    <%=request.get('htmlhead')%>
    <link rel="stylesheet" type="text/css" media="screen" href="category.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="schedule_detail.css" />
    <script type="text/javascript" src="schedule_detail.js"></script>
  </head>
  <body>
  
  <% row = request.get('class_row')%>
  
  <div class="plweb-schedule-DetailTabLabel">
  	<ul>
  		<li id="tab-label01" class="tab-labels"><span>Resources Usage (%)</span></li>
  		<li id="tab-label02" class="tab-labels"><span>Class Information</span></li>
  	</ul>
  </div>
  
  <div id="tab-item01" class="tab-groups plweb-schedule-DetailUsebookGraph">
    <img src="schedule_usebook_graph.groovy?class_id=${row.id}" />
  </div>
  
  <div id="tab-item02" class="tab-groups plweb-schedule-DetailTable div-hide">
	  <table>
	    <tr>
	      <td>ID</td>
	      <td>${row.id}</td>
	    </tr>
	    <tr>
	      <td>NAME</td>
	      <td>${row.name}</td>
	    </tr>
	    <tr>
	      <td>SCHOOL</td>
	      <td>${row.school}</td>
	    </tr>
	    <tr>
	      <td>DEPT.</td>
	      <td>${row.dept}</td>
	    </tr>
	    <tr>
	      <td>Years</td>
	      <td>${row.year} / ${row.sems}</td>
	    </tr>
	  </table>
  </div>
  
  </body>
</html>