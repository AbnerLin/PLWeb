<%
helper = request.get('helper')

email = helper.fetch('email', '')
email2 = helper.fetch('email2', '')
password = helper.fetch('password', '')
password2 = helper.fetch('password2', '')
name = helper.fetch('name', '')
enrollment = helper.fetch('enrollment', '')
classid = helper.fetch('classid', '2014300025')
phone = helper.fetch('phone', '')
school = helper.fetch('school', '')
department = helper.fetch('department', '')
roletype = helper.fetch('roletype', 'S')

roleOptions = []
roleOptions << [value: 'S', display: '學生', checked: 'S'.equals(roletype), desc: '學生帳號於註冊成功後可立即使用']
roleOptions << [value: 'T', display: '教師', checked: 'T'.equals(roletype), desc: '教師帳號於註冊後三日內完成審核，並以電子郵件通知']
roleOptions << [value: 'A', display: '作者', checked: 'A'.equals(roletype), desc: '作者帳號於註冊後七日內完成審核，並以電子郵件通知']
%>
<div class="signup-userSize">
	我們已經有
	<% request.get('totalUserSize').each { number-> %><span>${number}</span><% } %>
	位使用者註冊專屬帳號，歡迎您加入！
</div>

<form action="${response.encodeUrl('/login/signup_save.groovy')}#errormsg" method="post" class="form-signup" autocomplete="off">
	<input type="hidden" name="module" value="signup" />
	
	<table class="form-table" width="100%">
	
		<tr>
            <td colspan="2" class="formDesc">環境測試</td>
        </tr>
        <tr>
            <th>Java 安裝設定:</th>
            <td>
                <p class="more-about-fields">請進入<a target="_blank" href=http://help.plweb.org/installation:jdk>連結</a>，並按照指示安裝JAVA環境。<br><a target="_blank" href=http://help.plweb.org/installation:jdk>Java 安裝設定教學</a></p>
            </td>
            </tr>

            <tr>
                <th>下載練習編輯器：</th>
                <td>
                   <p class="more-about-fields"><a href=/Test.jnlp download>Test.jnlp</a></p>
                </td>
            </tr>


            <tr>
                <th>常見問題(FAQ):</th>
                <td>
                    <p class="more-about-fields">如果在使用PLWeb時需要協助，請瀏覽以下連結，如還仍未找到所需答案，請聯絡我們。<br><a target="_blank" href=http://help.plweb.org/technical_support>技術支援</a><br><a target="_blank" href=http://help.plweb.org/faq:editor_startup>常見問題</a></p>
                </td>
            </tr>
	
	
	
		<tr>
			<td colspan="2" class="formDesc">必填</td>
		<!--</tr>
		<tr>
			<th width="120">註冊身分:</th>
			<td> -->
			<% roleOptions.each { role-> %>
			    <input type="hidden" id="roletype-${role.value}" name="roletype" value="${role.value}"${role.checked?' checked':''}/> <label for="roletype-${role.value}">${role.display}</label>
			<% } %>
		<!--	<% roleOptions.each { role-> %>
			    <div id="roletype-desc-${role.value}" class="roletype-desc" style="${role.checked?'':'display:none'}">${role.desc}</div>
			<% } %>
			</td> -->
		</tr>
		<tr>
			<th>電子郵件:</th>
			<td>
				<input type="text" name="email" class="input-text" value="${email}" title="此為日後登入PLWeb的帳號，請填寫您的電子郵件信箱。" />
				<!--<img id="email-check-flag" src="icon-16/accept.png" alt="check" border="0" />-->
            </td>
		</tr>
		<tr>
			<th>確認電子郵件:</th>
			<td>
				<input type="text" name="email2" class="input-text" value="${email2}" title="再輸入一次電子郵件信箱。" />
			</td>
		</tr>
		<tr>
			<th>密碼:</th>
			<td>
				<input type="password" name="password" class="input-text" value="${password}" title="密碼長度至少需要五個字元，請使用大小寫英文、數字組成的密碼。" />
			</td>
		</tr>
		<tr>
			<th>確認密碼:</th>
			<td>
				<input type="password" name="password2" class="input-text" value="${password2}" title="再輸入一次密碼。" />
			</td>
		</tr>
		<tr>
			<th>姓名:</th>
			<td>
				<input type="text" name="name" class="input-text" value="${name}" title="輸入您的全名(例：王小明)，將用於系統顯示及證書列印等。" />
			</td>
		</tr>
		<tr>
			<th>學號 / 教職員編號:</th>
			<td>
				<input type="text" name="enrollment" class="input-text" value="${enrollment}" title="輸入您的學號、教師或職員編號，部分系統功能需要您的編號才能驗證存取權限。" />
				<p class="more-about-fields">請盡量填寫真實資料，以方便任課教師辨識，若您不小心忘記密碼，必須填入此項資料才能申請密碼回復。 (若無編號請填寫：0000)</p>
			</td>
		</tr>
		<tr>
		    <th>驗證碼:</th>
		    <td>
	        	<input type="hidden" name="recaptcha_public_key" value="${helper.attr('recaptcha_public_key')}" />
		        <div id="recaptcha_element"></div>
				<p class="more-about-fields">若無法辨識文字，請按 <img src="http://www.google.com/recaptcha/api/img/white/refresh.gif" /> 按鈕更換驗證碼。</p>
		    </td>
		</tr>
		<tr>
			<td colspan="2" class="formDesc">選填</td>
		</tr>
		<tr>
			<th>課程代號:</th>
			<td>
				<input type="text" name="classid" class="input-text" style="color:blue" value="${classid}" title="若您已取得一組「課程代號」，請在此輸入以立即完成選課作業。" />
				<p class="more-about-fields">若無則保留預設值。</p>
				<% helper.include "/public_course.groovy" %>
			</td>
		</tr>
        <% if (helper.attr('signup_errormsg') != null) { %>
        <tr>
            <td colspan="2">
				<a name="errormsg"></a>
                <div class="errormsg">申請失敗，請修正以下錯誤！ ${helper.attr('signup_errormsg')}</div>
            </td>
		</tr>
        <% } %>
		<tr>
			<td colspan="2" align="center" class="center">
				<button class="fancy-button" title="點選後完成註冊">送出申請</button>
				<a href="index.groovy" class="fancy-button-gray">取消</a>
			</td>
		</tr>
	</table>
</form>
