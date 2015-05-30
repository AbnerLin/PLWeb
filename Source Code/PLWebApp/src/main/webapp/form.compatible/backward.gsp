<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'Dashboard'%></title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="panel.navigation/page_header.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="form.compatible/backward.css" />
		<script type="text/javascript" src="form.compatible/backward.js"></script>
	</head>
	<body class="theme">
  
	<div class="page-wrapper">
		<div class="header">
			<% helper.include('../panel.navigation/page_header.groovy') %>
		</div>
		<div class="content-top"></div>
		<div class="content">
			<iframe width="100%" height="500" src="<%=helper.fetch('url')%>"></iframe>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>