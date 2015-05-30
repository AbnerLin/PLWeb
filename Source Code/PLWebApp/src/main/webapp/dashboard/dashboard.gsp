<%
import org.plweb.webapp.helper.CommonHelper
import groovy.sql.Sql
import javax.naming.InitialContext

def helper = new CommonHelper(request, response, session)

def utype = helper.sess('utype')

isTeacher = false
if(utype.equals("T")){
        isTeacher = true
}


%>

<div>
	<h1 style="font-size:24px">歡迎使用程式設計練習系統</h1>
	
	<div class="content" style="font-size:15px;line-height:1.8em">
		<p>第一次使用的學習夥伴們，以下的文件將幫助您快快樂樂上手！</p>
		<ol>
			<li><a href="../doc/201109-PLWEB-HOWTO-ADD-CLASS.pdf" target="_blank">教師如何建立新課程？</a> (<a href="https://docs.google.com/viewer?url=v2.plweb.org%2Fdoc%2F201109-PLWEB-HOWTO-ADD-CLASS.pdf&embedded=true" class="" target="_blank">線上檢視</a>)</li>
			<li><a href="../doc/201109-PLWEB-HOWTO-USE-CLASS.pdf" target="_blank">教師如何開放課程的練習功能給學生使用？</a> (<a href="https://docs.google.com/viewer?url=v2.plweb.org%2Fdoc%2F201109-PLWEB-HOWTO-USE-CLASS.pdf&embedded=true" class="" target="_blank">線上檢視</a>)</li>
			<li><a href="../doc/201109-PLWEB-HOWTO-JOIN-CLASS.pdf" target="_blank">學生如何加入教師建立的課程？</a> (<a href="https://docs.google.com/viewer?url=v2.plweb.org%2Fdoc%2F201109-PLWEB-HOWTO-JOIN-CLASS.pdf&embedded=true" class="" target="_blank">線上檢視</a>)</li>
			<li><a href="../doc/201109-PLWEB-HOWTO-USE-EDITOR.pdf" target="_blank">學生如何開始練習？</a> (<a href="https://docs.google.com/viewer?url=v2.plweb.org%2Fdoc%2F201109-PLWEB-HOWTO-USE-EDITOR.pdf&embedded=true" class="" target="_blank">線上檢視</a>)</li>
			 <% if(isTeacher){
                        print "<li><a href=\"../doc/PLWeb-Version-3-doc.pdf\" target=\"_blank\" style=\"color: red;\">PLWeb Version 3 適性學習與線上考試功能簡介 </a><a href=\"https://docs.google.com/viewer?url=v2.plweb.org%2Fdoc%2FPLWeb-Version-3-doc.pdf\" target=\"_blank\" style=\"color:red;\">(線上檢視)</a></li>"
                 } %>
		</ol>
	</div>
	
	<p style="font-size:13px;color:#555555;line-height:1.6em">※ 以上文件為PDF格式，您的電腦需要安裝合適的閱讀軟體，如果您無法正常開啟，請下載安裝免費的<a href="http://get.adobe.com/tw/reader/" target="_blank">Adobe Reader</a>。您也可以使用「<b>線上檢視</b>」直接檢視文件內容，此功能需要您登入Google帳號。</p>

	
	<p style="margin: 0 auto;width:400px;background-color:#ffffaa;padding:15px;line-height:1.6em;font-size:15px;">＊＊如果您是教師並且願
意使用PLWeb授課，歡迎您與我們聯絡。<br>E-mail: tungsh[at]yuntech.edu.tw</p>

<script type="text/javascript" src="http://www.google.com/reader/ui/publisher-zh_TW.js"></script>
<script type="text/javascript" src="http://www.google.com/reader/public/javascript/user/08496473347676031859/label/PLWeb 訂閱?n=10&callback=GRC_p(%7Bc%3A%22blue%22%2Ct%3A%22PLWeb%20%5Cu6559%5Cu5B78%5Cu90E8%5Cu843D%5Cu683C%22%2Cs%3A%22false%22%2Cn%3A%22true%22%2Cb%3A%22false%22%7D)%3Bnew%20GRC"></script>

</div>
<div style="height:320px;background:url('../img/plweb_background_editor.png') no-repeat right bottom">
</div>

