<html><% helper = request.get('helper') %>
<head>
  <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
  <title>Google Chrome Frame</title>
  <!-- Compass -->
  <link href="${helper.basehref}stylesheets/screen.css" media="screen, projection" rel="stylesheet" type="text/css" />
  <link href="${helper.basehref}stylesheets/silk-sprite.css" media="screen, projection" rel="stylesheet" type="text/css" />
  <link href="${helper.basehref}stylesheets/print.css" media="print" rel="stylesheet" type="text/css" />
  <!--[if IE]>
  <link href="${helper.basehref}stylesheets/ie.css" media="screen, projection" rel="stylesheet" type="text/css" />
  <![endif]-->
</head>
<body class="admin-layout">
  <h1>安裝 Chrome Frame 瀏覽器套件</h1>
  
  <p><a href="http://code.google.com/chrome/chromeframe/" target="_blank">Chrome Frame</a> 是由 Google 免費提供的 Internet Explorer 瀏覽器升級套件</p>

  <span>
    <span class="icons ss_arrow_undo"></span>
    <a href="index.groovy" class="fancy-link">返回首頁</a>
  </span>
  
  <hr/>

  <p>安裝 Google Chrome Frame 可以全面提昇舊版 Internet Explorer 瀏覽器的速度及安全性。</p>

  <iframe width="800" height="480" src="http://www.google.com/chromeframe/eula.html?prefersystemlevel=true" />

  <!--<button class="fancy-button" onclick="CFInstall.check({mode: 'overlay', destination: '${helper.basehref}'});">開始安裝</button>
  <a href="index.groovy" class="fancy-button-gray">取消</a>-->

  <script type="text/javascript" 
   src="http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js"></script>
  <style>
   /* 
    CSS rules to use for styling the overlay:
      .chromeFrameOverlayContent
      .chromeFrameOverlayContent iframe
      .chromeFrameOverlayCloseBar
      .chromeFrameOverlayUnderlay
   */
  </style> 

  <script>
   // You may want to place these lines inside an onload handler
   /*
   CFInstall.check({
     mode: "overlay",
     destination: "http://v2.plweb.org/"
   });
   */
  </script>
</body>
</html>