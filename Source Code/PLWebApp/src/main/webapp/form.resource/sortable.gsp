<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'Sortable'%></title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}form.resource/sortable.css" />
		<script type="text/javascript" src="${helper.basehref}form.resource/sortable.js"></script>
	</head>
	<body>
		<form name="sortable" action="${helper.basehref}form.resource/ajax_sortable.groovy?course_id=${helper.attr('course_id')}"></form>
		<button class="btn-control btn-control-save" title="Save changes">
			<img src="icon-16/page_save.png" alt="page_save.png" />
			<span>Save changes</span>
		</button>
		
		<span class="changed-hints">尚未儲存</span>
		
		<ul class="sortable-chapters">
		<%helper.attr('chapters').each{row->%>
		<li id="lessonMap_${row.id}">${row.title}</li>
		<%}%>
		</ul>
	</body>
</html>