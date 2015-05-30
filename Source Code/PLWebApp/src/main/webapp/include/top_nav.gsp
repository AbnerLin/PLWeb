<%
helper = request.get('helper')
%>
<img class="plweb-logo" src="${helper.basehref}img/plweb_logo.png" alt="PLWeb Logo" />

<div class="topNav">
	<div class="topLinks">
		<span><%=helper.sess('uname')%>(<%=helper.sess('uemail')%>)</span>
		<a href="account.groovy" class="embedded-link">個人帳號</a>
		<%if(helper.attr('is_admin')){%>
		|
		<a href="<%=helper.attr('admin_url')%>" target="_blank">系統管理</a>
		<%}%>
		|
		<a href="http://help.plweb.org/installation:jdk" class="embedded-link" target="_blank">JDK安裝說明</a>
		|
		<a href="http://help.plweb.org/technical_support" class="embedded-link" target="_blank">技術支援</a>
		|
		<a href="${helper.sess('logout_url')}" onclick="return confirm('確定要登出系統嗎?')">登出</a>
	</div>
	
	<div class="formLinks">
		<ul>
			<li><span><a href="${response.encodeUrl('dashboard/index.groovy')}" class="link-item">我的課程</a></span></li>
			<li><span><a href="<%=helper.make_url('form.compatible/backward.groovy', ['url':'class_center.groovy'])%>" class="link-item">班級管理</a></span></li>			
			<li><span><a href="form.resource/resource.groovy" class="link-item">教材管理</a></span></li>
			<li><span><a href="exam/exam.groovy" class="link-item">測驗模式</a></span></li>
		</ul>
	</div>
</div>
