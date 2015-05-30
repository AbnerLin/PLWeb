<%helper=request.get('helper')%>
<div class="panel-navigation-page-header">
	<div class="logo">
		<img src="img/plweb_logo.png" alt="PLWeb Logo" />
	</div>
	
	<div class="top-links">
		<span><%=helper.sess('uname')%>(<%=helper.sess('uemail')%>)</span>
		<a href="<%=helper.make_url('form.compatible/backward.groovy', ['url':'account.groovy'])%>">Account</a>
		<%if(helper.attr('is_admin')){%>
		|
		<a href="<%=helper.attr('admin_url')%>" target="_blank">系統管理</a>
		<%}%>
		|
		<a href="http://help.plweb.org/installation:jdk" target="_blank">JDK安裝說明</a>
		|
		<a href="http://help.plweb.org/technical_support" target="_blank">技術支援</a>
		|
		<a href="${helper.sess('logout_url')}" onclick="return confirm('are you sure?')">Logout</a>
	</div>
	
	<div class="form-links">
		<ul>
			<li><span><a href="form.dashboard/dashboard.groovy" class="link-item">Dashboard</a></span></li>
			<li><span><a href="<%=helper.make_url('form.compatible/backward.groovy', ['url':'class_center.groovy'])%>" class="link-item">Class</a></span></li>			
			<li><span><a href="form.resource/resource.groovy" class="link-item">Resource</a></span></li>
			<li><span><a href="exam/exam.groovy" class="link-item">Exam</a></span></li>
		</ul>
	</div>
	
	<div class="div-clear"></div>
</div>
