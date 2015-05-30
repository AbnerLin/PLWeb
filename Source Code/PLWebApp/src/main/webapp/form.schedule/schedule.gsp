<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <title>PLWeb - Schedule</title>
    <%=request.get('htmlhead')%>
    <link rel="stylesheet" type="text/css" media="screen" href="schedule.css" />
    <script type="text/javascript" src="schedule.js"></script>
    <script type="text/javascript">
	linkTable = [
		[
			{"target":"plweb_iframe_category",  "href":"${request.getAttribute('category_url')}"},
			{"target":"plweb_iframe_preview",   "href":"${request.getAttribute('preview_url')}"}
		],
		[{"target":"plweb_iframe_category", "href":"category_self.groovy"}],
		[{"target":"plweb_iframe_category", "href":"category_self.groovy"}],
		[{"target":"plweb_iframe_category", "href":"category_self.groovy"}],
		[{"target":"plweb_iframe_category", "href":"category_self.groovy"}]
	];
    </script>
  </head>
  <body>

  <div class="plweb-schedule-PageTitle">
  	<h1>PLWeb Class Scheduling Tool</h1>
  </div>

  <div class="plweb-schedule-PageHeader">
	  <div class="plweb-schedule-BookCategory">
	  	<ul class="plweb-schedule-VerticalTabPanel">
	  	  <li id="label-0" class="plweb-schedule-VerticalTabPanelLabel selected"><span>Schedule</span></li>
	  	  <li id="label-1" class="plweb-schedule-VerticalTabPanelLabel"><span>My Resources</span></li>
	  	  <li id="label-2" class="plweb-schedule-VerticalTabPanelLabel"><span>Favorites</span></li>
	  	  <li id="label-3" class="plweb-schedule-VerticalTabPanelLabel"><span>TQC+ Java</span></li>
	  	  <li id="label-4" class="plweb-schedule-VerticalTabPanelLabel"><span>Other</span></li>
	  	</ul>
	  </div>
	  
	  <div class="plweb-schedule-BookFrame">
	  	<iframe name="plweb_iframe_category" src="${request.getAttribute('category_url')}"></iframe>
	  </div>
	  
	  <div class="div-clear"></div>
  </div>

  <div class="plweb-schedule-PageSeperator">Expand Book Category</div>

  <div class="plweb-schedule-PageBody">
	  
	  <div class="plweb-schedule-BookPreview">
	  	<iframe name="plweb_iframe_preview" src="${request.getAttribute('preview_url')}"></iframe>
	  </div>
	  
  </div>

  </body>
</html>