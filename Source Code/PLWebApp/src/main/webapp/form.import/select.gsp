<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'Import'%></title>
		<%=helper.htmlhead()%>
		<link rel="stylesheet" type="text/css" media="screen" href="form.import/import.css" />
	</head>
	<body>
	<h1>PLWeb HTML Importer</h1>
	<ul>
		<li>
			<img src="img/plweb_wiki_screen.png" alt="PLWeb Wiki" />
			<h2>PLWeb Wiki</h2>
			<span>
			採用維基相同平台(<a href="http://www.mediawiki.org/wiki/MediaWiki">MediaWiki</a>)為基礎建立的網站，不僅可以同時多人共同寫作，對於程式語言教材的撰寫亦提供良好支援。
			
			</span>
			<div class="control-set">
				<a href="http://wiki.plweb.org/" target="_blank">Site</a>
				|
				<a href="form.import/import_plwiki.groovy?course_id=${helper.attr('course_id')}&lesson_id=${helper.attr('lesson_id')}" class="import">Import</a>
			</div>
			<div class="div-clear"></div>
		</li>
	</ul>
	<div class="footer">
		PLWeb HTML Importer v1.0.1
	</div>
	</body>
</html>