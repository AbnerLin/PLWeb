<%
helper = request.get('helper')
%>
<h2>忘記帳號</h2>
<form action="${response.encodeURL('/login/?m=account')}" method="post" class="form-signup">
    <input type="hidden" name="module" value="account" />
	<table class="form-table" width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="2" class="formDesc"><b>以下欄位擇一填寫</b></td>
		</tr>
		<tr>
			<th width="100">姓名:</th>
			<td>
				<input type="text" name="name" class="input-text" value="${helper.fetch('name', '')}" title="輸入您註冊使用的全名。" />
				<img id="name-check-flag" src="icon-16/accept.png" alt="check" border="0" />
            </td>
		</tr>
		<tr>
			<th>學號:</th>
			<td>
				<input type="text" name="enrollment" class="input-text" value="${helper.fetch('enrollment', '')}" title="輸入您的學號、教師或職員編號。" />
				<img id="enrollment-check-flag" src="icon-16/accept.png" alt="check" border="0" />
            </td>
		</tr>
		<% if (helper.attr('accounts')) { %>
		<tr>
		    <td colspan="2">
                <ul>
                <% helper.attr('accounts').each { row-> %> 
                    <li><span class="pre-encode">${row.email}</span>, ${row.name}, ${row.enrollment}</li>
                <% } %>
                </ul>
		    </td>
		</tr>
		<% } %>
		<tr>
			<td colspan="2" align="center" class="center">
				<button class="fancy-button" title="查詢帳號">查詢帳號</button>
				<a href="index.groovy" class="fancy-button-gray">取消</a>
			</td>
		</tr>
    </table>
</form>
