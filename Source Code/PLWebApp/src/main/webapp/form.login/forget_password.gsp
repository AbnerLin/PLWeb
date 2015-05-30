<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - Login</title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="form.login/login.css" />
		<script type="text/javascript" src="form.login/forget_password.js"></script>
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
				<div class="back-to-login div-hide">
					<span><a href="form.login/login.groovy">Login</a></span>
				</div>
				
				<form action="form.login/forget_password.groovy" method="post" class="form-signup">
			 		
			 		<div class="form-description">
			 			<p>Fill out the following form, the system will help you reset password.<br/>
			 			
			 			<% if (helper.attr('mail_send_to')) { %>
			 			<div class="account-list"><ul><li><span class="encoded-email"><font color="#0000ff"><%=helper.attr('mail_send_to')%></font></span> Check your mailbox after about five minutes.</li></ul></div>
			 			<% } %>
			 		</div>
				  	
			 		<div class="field">
			 			<div class="tips"><ul><li>系統將自動產生一組新密碼，並寄送至此信箱。</li></ul></div>
			 			<span class="label font-field">E-Mail Address</span></span>
			  			<input type="text" name="email" class="font-field" value="${helper.fetch('email', '')}" />
			 		</div>
				  	
					<button type="submit" class="btn-control">
						<img src="icon-16/layout_sidebar.png" alt="" />
						<span>Reset Password</span>
					</button>

				 </form>
			</div>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>