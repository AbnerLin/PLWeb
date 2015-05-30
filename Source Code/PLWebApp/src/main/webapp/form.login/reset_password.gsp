<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - Reset Password</title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="form.login/login.css" />
		<script type="text/javascript" src="form.login/reset_password.js"></script>
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
				
				<form action="form.login/reset_password.groovy" method="post" class="form-signup">
			 		
			 		<input type="hidden" name="u" value="${helper.fetch('u')}"/>
			 		<input type="hidden" name="p" value="${helper.fetch('p')}"/>
			 		
			 		<div class="form-description">
			 			<p>請輸入兩次新密碼，系統將會重新設定您的密碼。</p>
			 		</div>
				  	
				  	<div class="field">
			 			<div class="tips"><ul><li>密碼長度至少需要五個字元，請使用大小寫英文、數字組成的密碼。</li></ul></div>
			 			<span class="label font-field">Password <span class="required">*</span></span>
				  		<input type="password" name="password" class="font-field" value="${helper.fetch('password', '')}" />
				  	</div>
				  	<div class="field">
			 			<span class="label font-field">Confirm Password</span>
				  		<input type="password" name="password2" class="font-field" value="${helper.fetch('password2', '')}" />
				  		<div class="msg-wrapper"><span class="msg-password"></span></div>
				  	</div>
				  	
					<div class="button">
			 			<button type="submit" class="font-field">Reset Password</button>
				  	</div>
				 </form>
			</div>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>