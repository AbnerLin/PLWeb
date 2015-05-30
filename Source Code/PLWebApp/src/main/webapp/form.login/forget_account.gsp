<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - Login</title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="form.login/login.css" />
		<script type="text/javascript" src="form.login/forget_account.js"></script>
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
				
				<form action="form.login/forget_account.groovy" method="post" class="form-signup">
			 		
			 		<div class="form-description">
			 			<p>Fill out the following form, the system will help you retrieve account.<br/>
			 			
			 			<% if (request.get('accounts')) { %>
			 			We've found these <strong>account(s)</strong>.
			 			<div class="account-list"><ul><% request.get('accounts').each { acc-> %><li><span class="encoded-email">${acc.EMAIL}</span> (${acc.NAME} / ${acc.ENROLLMENT})</li><% } %></ul></div>
			 			<% } %>
			 		</div>
				  	
				  	<div class="field">
			 			<div class="tips"><ul><li>輸入您註冊使用的全名。</li></ul></div>
			 			<span class="label font-field">Full Name</span>
				  		<input type="text" name="name" class="font-field" value="${helper.fetch('name', '')}" />
				  		<div class="msg-wrapper"><span class="msg-name"></span></div>
				  	</div>
				  	
					<div class="field">
						<div class="tips"><ul><li>輸入您的學號、教師或職員編號。</li></ul></div>
						<span class="label font-field">Enrollment ID</span>
						<input type="text" name="enrollment" class="font-field" value="${helper.fetch('enrollment', '')}" />
						<div class="msg-wrapper"><span class="msg-enrollment"></span></div>
					</div>
				  	
					<div class="button">
			 			<button type="submit" class="font-field">Find Account</button>
				  	</div>
				 </form>
			</div>
			<div class="div-clear"></div>
		</div>
		<div class="content-bottom"></div>
	</div>

  </body>
</html>