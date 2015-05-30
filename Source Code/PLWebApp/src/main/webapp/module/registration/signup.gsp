<% helper = request.get('helper') %>
<div id="registration-signup-container">
	<p>目前已經有 ${helper.attr('user_count')} 位使用者註冊PLWeb。</p>
	
	<form name="form1" action="registration/signup" method="post" onsubmit="return false;">
		<p>標示 <span class="required">*</span> 欄位為必填資料</p>
		<table width="100%">
			<tr>
				<td class="required">電子郵件信箱</td>
				<td><input type="text" name="email" value="" /></td>
				<td>此為日後登入PLWeb的帳號，請填寫您的電子郵件信箱。</td>
			</tr>
			<tr>
				<td class="required">電子郵件信箱(再輸入一次)</td>
				<td><input type="text" name="email2" value="" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="required">密碼</td>
				<td><input type="password" name="password" value="" /></td>
				<td>密碼長度至少需要五個字元，請使用大小寫英文、數字組成的密碼。</td>
			</tr>
			<tr>
				<td class="required">密碼(再輸入一次)</td>
				<td><input type="password" name="password2" value="" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="required">姓名</td>
				<td><input type="text" name="name" value="" /></td>
				<td>輸入您的中文真實姓名(例：王小明)，將用於系統顯示及證書列印等。</td>
			</tr>
			<tr>
				<td class="required">學號或編號</td>
				<td><input type="text" name="enrollment" value="" /></td>
				<td>輸入您的學號、教師或職員編號，部分系統功能需要您的編號才能驗證存取權限。</td>
			</tr>
			<tr>
				<td>課程代號</td>
				<td><input type="text" name="class_id" value="" /></td>
				<td>若您已向任課教師取得一組「課程代號」，請在此輸入以立即完成選課作業。</td>
			</tr>
			<tr>
				<td>聯絡電話</td>
				<td><input type="text" name="phone" value="" /></td>
				<td>若您想讓其他學習夥伴或教師可以透過電話聯絡，請輸入行動電話號碼。</td>
			</tr>
		</table>
	  	
  		<button class="control-button form-submit" title="立即完成註冊">
			<span style="font-weight:bold;">我要註冊</span>
		</button>
	 </form>
</div>