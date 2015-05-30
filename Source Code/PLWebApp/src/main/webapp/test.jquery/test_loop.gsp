<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>test.jquery/test_loop</title>
		<%=helper.htmlhead()%>
		<script type="text/javascript" src="${helper.basehref}test.jquery/test_loop.js"></script>
	</head>
	<body>
		<div id="loop-main"></div>
	</body>
</html>