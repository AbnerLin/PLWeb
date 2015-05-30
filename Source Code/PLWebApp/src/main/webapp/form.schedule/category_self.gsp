<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <title>PLWeb - Schedule</title>
    <link rel="stylesheet" type="text/css" media="screen" href="../css/ui-darkness/jquery-ui-1.8.4.custom.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="../css/common.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="category.css" />
    <script type="text/javascript" src="../js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="../js/jquery-ui-1.8.4.custom.min.js"></script>
    <script type="text/javascript" src="category_self.js"></script>
  </head>
  <body>

  <% request.get('books').each { book-> %>
  <div class="plweb-schedule-Book">
    <div class="plweb-schedule-BookImage"><img src="../img/book_cover_empty.png" /></div>
    <div class="plweb-schedule-BookCaption"><span>${book.title}</span></div>
  </div>
  <% } %>
  
  </body>
</html>