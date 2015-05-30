<div id="dashboard-class-container">
	<div class="accordion" style="float:left;width: 40%">
	<%request.get('classes').each { row-> %>
		<h3><a href="#lesson-${row.id}">${row.name}</a></h3>
		<div>
			<div style="float:right">
				<a href="#" onclick="WebFace.Invoke('dashboard', 'class', 'ShowClass', '${row.id}');">開啟課程</a>
				<br/>
				<a href="#" onclick="WebFace.Invoke('dashboard', 'class', 'ShowClass', '${row.id}');">修改</a>
			</div>
			<table>
				<tr>
					<th align="right">課程代碼</th>
					<td>${row.id}</td>
				</tr>
				<tr>
					<th align="right">課程名稱</th>
					<td>${row.name}</td>
				</tr>
				<tr>
					<th align="right">學校</th>
					<td>${row.school}</td>
				</tr>
				<tr>
					<th align="right">系所</th>
					<td>${row.dept}</td>
				</tr>
			</table>
		</div>
	<% } %>
	</div>
	<div style="margin-left:40%;padding:1em" class="class-show"></div>
	<div class="div-clear"></div>
</div>