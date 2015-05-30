<% helper = request.get('helper') %>
<div id="system-login-container" class="webface-container">
	<div class="subtitle">
		<img src="img/plweb_subtitle_01.png" alt="PLWeb Programming Exercise Assistant" />
	</div>
  	<form name="form1" action="system/login" method="post" onsubmit="return false;">
  		<table cellpadding="5">
  			<tr>
  				<th align="right">電子郵件信箱</th>
  				<td><input type="text" name="email" class="field" /></td>
  			</tr>
  			<tr>
  				<th align="right">密碼</th>
  				<td><input type="password" name="password" class="field" /></td>
  			</tr>
  			<tr>
  				<td colspan="2" align="right">
			  		<button class="control-button form-submit" title="立即登入系統">
						<span style="font-weight:bold">登入</span>
					</button>
  				</td>
  			</tr>
  		</table>
  	</form>
  	
  	<div class="news">
  		<img class="news-cover" src="http://lh6.ggpht.com/_6WCtnrJ1NCA/THc-uSoRLRI/AAAAAAAAEMI/pKmFJD_qHx4/s288/plweb-editor.png" />
  		<p>PLWeb是程式設計課程專用的教學平台，讓教師可以更容易製作程式設計的數位教材，學生可以隨時隨地上網練習寫程式，程式碼及狀態都可以保存在網路上。</p> 
  		<div style="clear:both"></div>
  	</div>
</div>
