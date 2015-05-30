<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb v2.2</title>
		<!--include misc.-->
		<script type="text/javascript" src="lib/misc/swfobject.js"></script>

		<!--include jQuery, jQuery-UI, jQuery-plugins -->
		<link rel="stylesheet" type="text/css" media="screen" href="lib/jquery-ui/css/start/jquery-ui-1.8.6.custom.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="lib/jquery-ex/css/jquery.ipaddress.css" />
		<script type="text/javascript" src="lib/jquery/jquery-1.4.3.js"></script>
		<script type="text/javascript" src="lib/jquery-ui/jquery-ui-1.8.6.custom.min.js"></script>
		<script type="text/javascript" src="lib/jquery-ui/ui.tabs.closable.min.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.layout.min-1.2.0.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.json-2.2.min.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/aop.min.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.transmit.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.localisation-min.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.scrollTo-min.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.caret.js"></script>
		<script type="text/javascript" src="lib/jquery-ex/jquery.ipaddress.js"></script>
		
		<!--inlcude jQuery-WebFace-->
		<link rel="stylesheet" type="text/css" media="screen" href="lib/jquery-webface/jquery.webface.css" />
		<script type="text/javascript" src="lib/jquery-webface/jquery.multiselectplus.js"></script>
		<script type="text/javascript" src="lib/jquery-webface/jquery.webface-1.1.0.js"></script>

		<!--include Common-->
		<link rel="stylesheet" type="text/css" media="screen" href="common/common.css" />
		<script type="text/javascript" src="common/common.js"></script>

		<!--include Attach-->
		<link rel="stylesheet" type="text/css" media="screen" href="dashboard.css" />
		<script type="text/javascript" src="dashboard.js"></script>
	</head>
	<body>
		<div id="main-debugger" title="WebFace Debugger" style="display:none">
			optimization: <span class="main-debugger-optimization"></span>
			<br />
			command: <textarea id="main-debugger-command" style="width:100%;height:100px;">alert('debug');</textarea><button onclick="eval(\$('textarea#main-debugger-command').val())">run</button>
			<br />
			<button onclick="clear_debugger();">Clear Output</button>
			<div class="main-debugger-output"></div>
		</div>

		<div id="main-header-container">
			<div id="main-header-logo-container"><img src="img/plweb_logo.png" width="200" height="62" alt="PLWeb Logo" /></div>
			<div id="main-header-sub-container"></div>
		</div>
		<div id="main-menu-container">尚未登入</div>
		<div id="main-program-container">
		
			<div id="main-tab-container">
				<ul>
					<li><a href="#webface-tab-welcome"><span><!--DEFAULT_TAB--></span></a></li>
				</ul>
				<div id="webface-tab-welcome"><!--DEFAULT_TAB--></div>
			</div>
		</div>
		<!--<div id="main-info-container">Information</div>-->
		<div id="main-footer-container"><div id="main-status-bar">狀態列</div><div id="main-copyright">Copyright &copy; 2010 本網站之圖片與內容皆屬國立雲林科技大學所有，未經書面同意禁止一切形式之複製</div></div>
	</body>
</html>
