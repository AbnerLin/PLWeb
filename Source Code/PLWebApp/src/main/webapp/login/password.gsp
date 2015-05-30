<%
helper = request.get('helper')
%>
<h2>忘記密碼</h2>
<form action="${response.encodeURL('/login/password_save.groovy')}" method="post" class="form-password">
    <input type="hidden" name="module" value="password" />
	<table class="form-table" width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="2"><strong>請填寫以下資料</strong><br><p style="font-size: 12px; margin-top: 5px;">請注意郵件有可能被誤判為垃圾郵件。</p></td>
		</tr>
		<tr>
			<th>電子郵件信箱:</th>
			<td>
				<input type="text" name="email" class="input-text" value="${helper.fetch('email', '')}" title="輸入您的電子郵件信箱。" />
            </td>
		</tr>
		<tr>
		    <th>驗證碼:</th>
		    <td>
		        <input type="hidden" name="recaptcha_public_key" value="${helper.attr('recaptcha_public_key')}" />
		        <div id="recaptcha_element"></div>
		    </td>
		</tr>
        <% if (helper.attr('password_errormsg') != null) { %>
        <tr>
            <td colspan="2">
                <div class="errormsg">${helper.attr('password_errormsg')}</div>
            </td>
		</tr>
        <% } %>
		<tr>
			<td colspan="2" align="center" class="center">
				<button class="fancy-button" title="重新設定密碼">重新設定密碼</button>
				<a href="index.groovy" class="fancy-button-gray">取消</a>
			</td>
		</tr>
    </table>
</form>