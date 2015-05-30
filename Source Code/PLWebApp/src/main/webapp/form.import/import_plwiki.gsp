<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<% helper = request.get('helper') %>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
	<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'Import'%></title>
	<%=helper.htmlhead()%>
	<link rel="stylesheet" type="text/css" media="screen" href="form.import/import.css" />
</head>
<body>
<h1>Import HTML from PLWeb Wiki</h1>
<form action="form.import/import_plwiki.groovy" method="post">
	<input type="hidden" name="course_id" value="${helper.attr('course_id')}" />
	<input type="hidden" name="lesson_id" value="${helper.attr('lesson_id')}" />
	
	<label for="title">Page Title</label>
	<input class="text-field" type="text" size="40" id="title" name="title" value="${helper.fetch('title','')}" />
	<br/>
	<label for="section">Section</label>
	<input class="text-field" type="text" id="section" name="section" value="${helper.fetch('section','')}" />
	<br/>
	<button class="btn-control" type="submit"><span>Render</span></button>
	
	<%if(helper.attr('html_preview')){%>
		<button class="btn-control" name="save" type="submit"><span>Save HTML</span></button>
	<%}%>
</form>

<%if(helper.attr('html_preview')){%>
<div class="html-preview"><iframe src="${helper.attr('html_proxy')}" /></div>
<%}%>

</body>
</html>