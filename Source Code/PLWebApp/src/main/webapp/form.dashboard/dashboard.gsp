<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'Dashboard'%></title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="css/jHtmlArea/jHtmlArea.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/jwysiwyg/jquery.wysiwyg.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="panel.class/list_normal.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="panel.class/show_class.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="panel.lesson/show_lesson.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="panel.navigation/page_header.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="form.dashboard/dashboard.css" />
		<script type="text/javascript" src="js/jHtmlArea-0.7.0.min.js"></script>
		<!--<script type="text/javascript" src="js/jquery.wysiwyg.js"></script>-->
		<script type="text/javascript" src="panel.class/list_normal.js"></script>
		<script type="text/javascript" src="panel.class/show_class.js"></script>
		<script type="text/javascript" src="panel.lesson/show_lesson.js"></script>
		<script type="text/javascript" src="form.dashboard/dashboard.js"></script>
		<script type="text/javascript" src="http://widgets.amung.us/tab.js"></script><script type="text/javascript">WAU_tab('kj5l1p82s0bf', 'bottom-left')</script>
	</head>
	<body class="theme">
  
	<div class="page-wrapper">
		<div class="header">
			<% helper.include('../panel.navigation/page_header.groovy') %>
		</div>
		<div class="content-wrapper">
			<div class="content-top"></div>
			<div class="content">
				<div class="content-control">
						<button class="btn-control">
							<img src="icon-16/layout_sidebar.png" alt="" />
							<span>Sidebar On/Off</span>
						</button>
				</div>
				<div class="content-left">
					<div class="content-left-inner">
					<% helper.include('../panel.class/list_normal.groovy') %>
					</div>
				</div>
				<div class="content-center">
					<div class="content-center-inner">
					<% helper.include(helper.attr('content_url')) %>
					</div>
				</div>
				<div class="div-clear"></div>
				<div class="content-dashboard-desc">
				PLWeb Dashboard 1.0.0
				</div>
			</div>
			<div class="content-bottom"></div>
		</div>
	</div>

  </body>
</html>
