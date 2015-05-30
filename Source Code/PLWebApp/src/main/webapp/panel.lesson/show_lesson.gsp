<% helper = request.get('helper') %>
<div class="panel-lesson-show-lesson">

	<div class="lesson-toolbar">
	</div>
	
	<div class="lesson-toolbar">
		<div class="lesson-toolbar-date">
			<div>
				<a href="${helper.attr('date_url')}" target="_blank"><img src="icon-16/calendar.png" alt="date" border="0" /></a>
				繳交日期自
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'begin_date', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('begin_date')}</span>
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'begin_time', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('begin_time')}</span>
				起至
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'due_date', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('due_date')}</span>
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'due_time', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('due_time')}</span>
				止
			</div>
			
			<%if(helper.attr('close_date')){%>
			<a href="${helper.attr('date_url')}" target="_blank"><img src="icon-16/calendar.png" alt="date" border="0" /></a>
			編輯器開放至 ${helper.attr('close_date')} ${helper.attr('close_time')}
			<%}%>
		</div>

		<div class="lesson-toolbar-control">
			<%if (helper.attr('allow_editor')) {%>
			<button class="btn-control" onclick="location.href='${helper.attr('editor_url')}'">
				<img src="icon-16/plugin_edit.png" alt="editor"/>
				<span>開始練習</span>
			</button>
			<%}%>
			
			<button class="btn-control ajax-load-state" onclick="ajax_load_state('${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')" title="Refresh states">
				<img src="icon-16/chart_bar.png" alt="chart_bar"/>
				<span>更新狀態</span>
			</button>
			
			<%if (helper.attr('is_show_answer') && helper.attr('answer_url')) {%>
			<button class="btn-control" onclick="ajax_load_answer('${helper.attr('course_id')}', '${helper.attr('lesson_id')}')">
				<img src="icon-16/plugin_edit.png" alt="answer"/>
				<span>觀看解答</span>
			</button>
			<%}%>
			
			<%if (helper.attr('is_teacher')) {%>
			<button class="btn-control" onclick="fancybox_link('${helper.attr('report_url')}', {type: 'iframe', overlayShow: true, autoScale: true, width: '90%', height: '90%'})">
				<img src="icon-16/chart_bar.png" alt="report"/>
				<span>Class Report</span>
			</button>
			<%}%>
		</div>
	</div>
	
	<div class="lesson-state-panel">
		<div class="html-lesson-state"><img src="img/ajax-loader-1.gif"/></div>
	</div>
	<div class="lesson-answer-panel">
		<div class="html-lesson-answer"><img src="img/ajax-loader-1.gif"/></div>
	</div>
	<div class="html-content-string"><div class="prettyhtml">${helper.attr('html_text')}</div></div>

</div>