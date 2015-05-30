<% helper = request.get('helper') %>
<div id="system-menu-container" class="webface-container">
	<div class="accordion">
		<h3><a href="#section1">個人功能</a></h3>
		<div>
			<ul class="menu-list">
				<li><a class="default-link" href="#" onclick="WebFace.TabOpen('課程導覽', 'dashboard', 'class');">課程導覽</a></li>
				<li><a href="#" onclick="WebFace.TabOpen('加退選設定', 'registration', 'signup');">加退選設定</a></li>
			</ul>
		</div>
		<h3><a href="#section2">我的課程</a></h3>
		<div>
			<ul class="menu-list">
			<%helper.attr('my_classes').each { row-> %>
				<li>
					<a href="#" title="${row.id} / ${row.school} / ${row.dept}">${row.name}</a>
				</li>
			<% } %>
			</ul>
		</div>
		<% if (helper.attr('show_admin_menu')) { %>
		<h3><a href="#section3">系統狀態</a></h3>
		<div>
			<ul class="menu-list">
				<li><a href="#" onclick="WebFace.TabOpen('資料庫狀態', 'sys_status', 'database');">資料庫狀態</a></li>
			</ul>
		</div>
		<h3><a href="#section3">系統管理</a></h3>
		<div>
			<ul class="menu-list">
				<li><a href="#" onclick="WebFace.TabOpen('教師帳號維護作業', 'sys_admin', 'teacher');">教師帳號維護作業</a></li>
			</ul>
		</div>
		<% } %>
	</div>
	<div class="facebook-widget">
		<iframe src="http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fplweb.org%2F&amp;layout=standard&amp;show_faces=true&amp;width=450&amp;action=like&amp;font=arial&amp;colorscheme=light&amp;height=80" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:220px; height:80px;" allowTransparency="true"></iframe>
	</div>
</div>
