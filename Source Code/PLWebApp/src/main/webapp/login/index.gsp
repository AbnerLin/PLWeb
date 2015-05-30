<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<% helper = request.get('helper') %>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
	<title>PLWeb 程式設計練習系統 2.0</title>
	${helper.htmlhead()}

	<!--reCAPTCHA-->
	<script type="text/javascript" src="http://www.google.com/recaptcha/api/js/recaptcha_ajax.js"></script>

	<!-- Compass -->
	<link href="${helper.basehref}stylesheets/screen.css" media="screen, projection" rel="stylesheet" type="text/css" />
	<link href="${helper.basehref}stylesheets/print.css" media="print" rel="stylesheet" type="text/css" />
	<!--[if IE]>
	<link href="${helper.basehref}stylesheets/ie.css" media="screen, projection" rel="stylesheet" type="text/css" />
	<![endif]-->

	<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}css/jquery.tipsy.css" />
	<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}css/colorbox-style1/colorbox.css" />
	<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}css/jquery.jgrowl.css" />
	<script type="text/javascript" src="${helper.basehref}js/jquery.spotlight.min.js"></script>
	<script type="text/javascript" src="${helper.basehref}js/jquery.colorbox.min.js"></script>
	<script type="text/javascript" src="${helper.basehref}js/jquery.tipsy.js"></script>
	<script type="text/javascript" src="${helper.basehref}js/jquery.jgrowl_minimized.js"></script>
	<script type="text/javascript" src="${helper.basehref}login/index.js"></script>
	<!--<script type="text/javascript" src="http://widgets.amung.us/tab.js"></script><script type="text/javascript">WAU_tab('kj5l1p82s0bf', 'bottom-left')</script>-->
</head>
<body class="fancy-layout">
	<div class="page-wrapper"><div class="page login-page">
		<div class="header">
			<a href="/" id="logo"><img class="plweb-logo" src="${helper.basehref}img/plweb_logo.png" alt="PLWeb Logo" border="0" /></a>
			<div class="topnav">
				<div class="topLinks">
					<a href="http://help.plweb.org/installation:jdk" class="embedded-link">Java 安裝設定</a>
					|
					<a href="http://help.plweb.org/technical_support" class="embedded-link">技術支援</a>
					|
					<a href="http://help.plweb.org/ppt" class="embedded-link">檔案下載</a>
				</div>
				
				<div class="formLinks">
					<ul>
						<li><span><a href="http://help.plweb.org/demo" class="link-item embedded-link">功能展示</a></span></li>
						<li><span><a href="http://help.plweb.org/progress" class="link-item embedded-link">開課流程</a></span></li>
						<li><span><a href="http://help.plweb.org/workshop" class="link-item embedded-link">研習活動</a></span></li>
					</ul>
				</div>
			</div>
			
		</div>
		<div class="content-wrapper"><div class="content">
			<div class="content-left"><div class="content-left-inner">
				<img src="${helper.basehref}img/plweb_subtitle.png" />
				<div class="sidebar">
					<ul>
						<li>
							<span><a class="sidebar-link" href="${response.encodeUrl('/login/index.groovy?m=login')}"><strong>登入</strong></a></span>
							<div class="desc">線上有 ${helper.attr('ucount')} 位使用者！</div>
						</li>
						<li>
							<span><a class="sidebar-link" href="${response.encodeUrl('/login/index.groovy?m=signup')}"><strong>註冊</strong></a></span>
							<div class="desc">免費申請 PLWeb 帳號，加入我們的程式設計教學社群！</div>
						</li>
						<li>
							<div style="text-align:right;line-height:1.25em;">
								<a class="sidebar-link-small" href="${response.encodeUrl('/login/index.groovy?m=account')}">忘記帳號？</a><br/>
								<a class="sidebar-link-small" href="${response.encodeUrl('/login/index.groovy?m=password')}">忘記密碼？</a>
							</div>
						</li>
						<li>
							<!-- Facebook Like Box -->
							<iframe src="//www.facebook.com/plugins/likebox.php?href=http%3A%2F%2Fwww.facebook.com%2Fpages%2FPLWeb-%25E7%25A8%258B%25E5%25BC%258F%25E8%25AA%259E%25E8%25A8%2580%25E5%25AD%25B8%25E7%25BF%2592%25E7%25B6%25B2%2F120529308003800&amp;width=240&amp;height=62&amp;colorscheme=light&amp;show_faces=false&amp;border_color&amp;stream=false&amp;header=false&amp;appId=175500369215845" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:240px; height:62px;" allowTransparency="true"></iframe>
							<div class="desc">
							請訂閱 PLWeb 的 Facebook 專頁，提供您更多資訊及協助。
							</div>
						</li>
					</ul>
				</div>
			</div></div>
			<div class="content-center"><div class="content-center-inner">
				<% helper.include "${helper.fetch('m', 'login')}.groovy" %>
			</div></div>
			<div class="content-footer">
			<div class="content-footer-inner" style="font-size: 14px;">
                PLWeb 2.0 Copyright &copy; 國立雲林科技大學
               <br><br><a href="/PLWeb_ApplicationVersion.zip">PLWeb Desktop Version</a>
            </div></div>
		</div></div>
	</div></div>
	
<!-- Google Analytics -->
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try{
var pageTracker = _gat._getTracker("UA-18484833-1");
pageTracker._trackPageview();
} catch(err) {}
</script>
		
</body>
</html>
