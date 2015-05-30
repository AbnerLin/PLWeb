<% helper = request.get('helper') %>
<div id="system-menu-container" class="webface-container">
	<div class="accordion">
		<h3><a href="#">使用者註冊</a></h3>
		<div>
			<ul class="menu-list">
				<li><a class="default-link" href="#" onclick="WebFace.TabOpen('登入', 'system', 'login');">登入</a></li>
				<li><a href="#" onclick="WebFace.TabOpen('註冊新帳號', 'registration', 'signup');">註冊新帳號</a></li>
				<li><a href="#" onclick="WebFace.TabOpen('忘記帳號', 'registration', 'forget_account');">忘記帳號</a></li>
				<li><a href="#" onclick="WebFace.TabOpen('忘記密碼', 'registration', 'forget_password');">忘記密碼</a></li>
			</ul>
		</div>
	</div>
</div>
