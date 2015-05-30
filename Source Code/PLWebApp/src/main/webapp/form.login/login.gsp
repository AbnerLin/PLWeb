<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb 程式設計練習系統</title>
		<%=helper.htmlhead()%>
		<script type="text/javascript" src="${helper.basehref}js/jquery.spotlight.min.js"></script>
		<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}form.login/login.css" />
		<script type="text/javascript" src="${helper.basehref}form.login/login.js"></script>
	</head>
	<body class="theme">
  
	<div class="page-wrapper">
		<div class="header">
			<img src="img/plweb_logo.png" alt="PLWeb Logo" />
		</div>
		<div class="content-top"></div>
		<div class="content">
			<div class="content-left">
				<% request.get('helper').include('left_menu.groovy') %>
			</div>
			<div class="content-center">
				<div class="subtitle">
					<img src="img/plweb_subtitle_01.png" alt="PLWeb Programming Exercise Assistant" />
				</div>
			  	<form action="form.login/login_auth.groovy" method="post" class="form-login">
			  		<div class="photo">
			  			<img src="img/plweb_anonymous.png" alt="Your Photo" />
			  		</div>
			  		<div class="field">
			  			<span class="label font-field">E-Mail Address</span>
				  		<input type="text" name="email" class="font-field" value="<%=helper.sess_fetch('login_email', '')%>" />
				  		<div class="msg-wrapper"><span class="msg-email"></span></div>
			  		</div>
			  		<div class="field">
			  			<span class="label font-field">Password</span>
				  		<input type="password" name="password" class="font-field" value="" />
				  		<div class="msg-wrapper"><span class="msg-password"></span></div>
				  	</div>
					<div class="login-control-section">
			  			
				  		<button type="submit" class="btn-control form-button-login layout-classic" title="Login">
							<img src="icon-16/accept.png" alt="Login" />
							<span>Login</span>
						</button>
			  			
			  			<div class="login-message">
			  			<% if (request.get('login_error') != null) { %>
			  			<div class="login-error"><%=request.get('login_error')%></div>
			  			<% } %>
			  			</div>
				  	</div>
			  	</form>
			</div>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>