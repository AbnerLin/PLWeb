<% helper = request.get('helper') %>
<div class="panel-class-show-class">

	<div class="class-toolbar-control">
		<%if (helper.attr('is_teacher')) {%>
		<button class="btn-control" onclick="location.href='${helper.attr('schedule_url')}'">
			<img src="icon-16/calendar.png" alt="calendar.png"/>
			<span>Schedule</span>
		</button>
		<%}%>
		<%if (helper.attr('is_student')) {%>
		<button class="btn-control" onclick="fancybox_link('${helper.attr('status_url')}', {type: 'iframe', overlayShow: true, autoScale: true, width: '90%', height: '90%'})">
			<img src="icon-16/calendar.png" alt="calendar.png"/>
			<span>My Status</span>
		</button>
		<%}%>
	</div>
	
	<div class="class-display">
		<div class="class-display-title">${helper.attr('class_name')} ${helper.attr('class_id')}</div>
		<div class="class-display-content">
			<div class="class-html-content">
				<%if(helper.attr('is_teacher')){%>
				<div class="class-control">
					<button class="class-control-edit btn-control">
						<img src="icon-16/application_form_edit.png" alt="Edit HTML Content" />
						<span>Edit</span>
					</button>
				</div>
				<%}%>
				<div class="class-html-string">${helper.attr('html_text')}</div>
			</div>
			
			<%if(helper.attr('is_teacher')){%>
			<div class="class-html-editor">
				<form action="${helper.attr('class_html_form_action')}"></form>
				<div class="class-control">
					<button class="class-control-save btn-control">
						<img src="icon-16/page_save.png" alt="Save HTML Content" />
						<span>Save</span>
					</button>
				</div>
				<label class="textarea-container">
					<textarea class="class-html-textarea">${helper.attr('html_text')}</textarea>
				</label>
				<div class="class-control-advanced">
					<span class="class-control-cancel">cancel</span>
				</div>
				<div class="class-control-height">
					<span class="class-control-height-add">+</span>
					<span class="class-control-height-sub">-</span>
				</div>
			</div>
			<%}%>
		</div>
	
	</div>
	
	<hr/>
	<h3><%print helper.attr('students').size()?helper.attr('students').size():'No ' %> Students</h3>
	<div class="class-student-list">
	<%if(helper.attr('is_teacher')){%>
		<ul>
		<%c=0;helper.attr('students').each{row->print c++>0?'':''%>
		<li><span class="name">${row.name}</span></li>
		<%}%>
		</ul>
	<%}%>
	</div>

</div>
