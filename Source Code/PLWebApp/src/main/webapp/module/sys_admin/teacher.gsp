<% helper = request.get('helper') %>
<div id="sys_admin-teacher-container">
	<h3>教師帳號維護作業</h3>
	<form name="form1" action="sys_admin/teacher?action=new_teacher" method="post" onsubmit="return false;">
		E-Mail <input name="email" size="40" />
		<button class="control-button" onclick="WebFace.Invoke('sys_admin', 'teacher', 'FormSave');">加入新教師</button>
	</form>
	<hr/>
	<table width="100%">
		<tr>
			<th width="40">#</th>
			<th width="70">User ID</th>
			<th>E-Mail</th>
			<th>Name</th>
			<th>Enrollment ID</th>
			<th>Telephone</th>
			<th>Type</th>
			<th>最後登入時間</th>
			<th>功能</th>
		</tr>
		<% c = 0; helper.attr('rows').each { row -> %>
		<tr class="${c%2==0?'even':'odd'}">
			<td align="center">${++c}</td>
			<td>${row.USER_ID}</td>
			<td>${row.email}</td>
			<td>${row.name}</td>
			<td>${row.enrollment}</td>
			<td>${row.telephone}</td>
			<td>${row.type}</td>
			<td>${row.LAST_UPDATE?helper.getDatetimeString(new Date(row.LAST_UPDATE.toLong())):''}</td>
			<td>
				<button onclick="WebFace.Invoke('sys_admin', 'teacher', 'RemoveTeacher', '${row.USER_ID}');">移除教師權限</button>
			</td>
		</tr>
		<% } %>
	</table>
</div>
