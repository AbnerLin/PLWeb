<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - Login</title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="form.login/login.css" />
		<script type="text/javascript" src="form.login/signup.js"></script>
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
					<span><a href="login.groovy">Login</a></span>
				</div>
				
				<div class="register-count">
					We have
					<% request.get('user_count').each { number-> %><span>${number}</span><% } %>
					registered members.
				</div>
				
				<form action="form.login/signup_save.groovy" method="post" class="form-signup">
			 		
			 		<div class="form-description">
			 			<p>It's free to sign up new account. Just fill below fields.<br/>
			 			<span class="required">*</span> = required fields.</p>
			 			
			 			<% if (request.get('signup_errmsg')) { %>
			 			Sorry, you need to re-check below <font color="red">problems</font>.
			 			<div class="signup-error"><ul><% request.get('signup_errmsg').each { msg-> %><li>${msg}</li><% } %></ul></div>
			 			<% } %>
			 		</div>
			 		
			 		<div class="field">
			 			<div class="tips"><ul><li>此為日後登入PLWeb的帳號，請填寫您的電子郵件信箱。</li></ul></div>
			 			<span class="label font-field">E-Mail Address <span class="required">*</span></span></span>
			  			<input type="text" name="email" class="font-field" value="${helper.fetch('email', '')}" />
			 		</div>
			 		<div class="field">
			 			<span class="label font-field">Confirm E-Mail Address</span>
			  			<input type="text" name="email2" class="font-field" value="${helper.fetch('email2', '')}" />
			  			<div class="msg-wrapper"><span class="msg-email"></span></div>
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
				  	
				  	<div class="field">
			 			<div class="tips"><ul><li>輸入您的全名(例：王小明)，將用於系統顯示及證書列印等。</li></ul></div>
			 			<span class="label font-field">Full Name <span class="required">*</span></span>
				  		<input type="text" name="name" class="font-field" value="${helper.fetch('name', '')}" />
				  		<div class="msg-wrapper"><span class="msg-name"></span></div>
				  	</div>
				  	
					<div class="field">
						<div class="tips"><ul><li>輸入您的學號、教師或職員編號，部分系統功能需要您的編號才能驗證存取權限。</li></ul></div>
						<span class="label font-field">Enrollment ID <span class="required">*</span></span>
						<input type="text" name="enrollment" class="font-field" value="${helper.fetch('enrollment', '')}" />
						<div class="msg-wrapper"><span class="msg-enrollment"></span></div>
					</div>
					
					<div class="field">
						<div class="tips"><ul><li>若您已取得一組「課程代號」，請在此輸入以立即完成選課作業。</li></ul></div>
						<span class="label font-field">Class ID</span>
						<input type="text" name="classid" class="font-field" value="${helper.fetch('classid', '')}" />
						<div class="msg-wrapper"><span class="msg-classid"></span></div>
					</div>
					
					<div class="field">
						<div class="tips"><ul><li>若您想讓其他學習夥伴或教師可以透過電話聯絡，請輸入行動電話號碼。</li></ul></div>
						<span class="label font-field">Phone</span>
						<input type="text" name="phone" class="font-field" value="${helper.fetch('phone', '')}" />
						<div class="msg-wrapper"><span class="msg-phone"></span></div>
					</div>
				  	
			  		<button type="submit" class="btn-control layout-classic" title="Sign up">
						<img src="icon-16/accept.png" alt="Sign up" />
						<span>Sign up</span>
					</button>
				 </form>
			</div>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>