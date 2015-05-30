<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - Login</title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="form.login/login.css" />
		<script type="text/javascript" src="form.login/signup_ok.js"></script>
	</head>
	
	<body class="theme">
  
	<div class="page-wrapper">
		<div class="header">
			<img src="img/plweb_logo.png" alt="PLWeb Logo" />
		</div>
		<div class="content-top"></div>
		<div class="content">
			<div class="content-left">
				<% helper.include('left_menu.groovy') %>
			</div>
			<div class="content-center">
				<div class="register-count">
					Thanks!<br/>
					You're the
					<% request.get('user_count').each { number-> %><span>${number}</span><% } %>
					registered member.
				</div>
				<div class="signup-ok-login">
					Welcome!<br/>
					Your account is <strong><%=helper.sess('signup_email')%></strong> .<br/>
					<a href="form.login/login.groovy">Login</a> now, and feel free to explore PLWeb online resources.
				</div>
				
			</div>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>