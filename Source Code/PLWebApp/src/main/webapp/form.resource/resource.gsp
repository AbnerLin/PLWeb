<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'Resource'%></title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="css/jHtmlArea/jHtmlArea.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="panel.navigation/page_header.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="form.resource/resource.css" />
		<script type="text/javascript" src="js/jHtmlArea-0.7.0.min.js"></script>
		<script type="text/javascript" src="form.resource/resource.js"></script>
	</head>
	<body class="theme">
  
	<div class="page-wrapper">
		<div class="header">
			<% helper.include('../panel.navigation/page_header.groovy') %>
		</div>
		<div class="content-top"></div>
		<div class="content">
			<div class="content-control">
					<button class="btn-control layout-sidebar" title="Turn on/off sidebar layout display">
						<img src="icon-16/layout_sidebar.png" alt="Turn on/off sidebar layout display" />
						<span>Sidebar On/Off</span>
					</button>
					<button class="btn-control layout-classic" title="Switch to classic interface" onclick="location.href='${helper.attr('classic_url')}'">
						<img src="icon-16/application.png" alt="Switch to classic interface" />
						<span>Classic</span>
					</button>
			</div>
			<div class="content-left">
				<div class="content-left-inner">
					<div class="ajax-resource-list">Loading</div>
					<button class="btn-control ajax-resource-list-refresh" title="Refresh" disabled>
						<img src="icon-16/arrow_refresh.png" alt="Refresh" />
						<span>Refresh</span>
					</button>
				</div>
			</div>
			<div class="content-center">
				<div class="content-center-inner">
					<div class="ajax-resource-load">Select a resource from right side category.</div>
				</div>
			</div>
			<div class="div-clear"></div>
			<div class="content-dashboard-desc">
			PLWeb Resource Manager 2.0.0
			</div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>