<%
        import groovy.sql.Sql

        helper = request.get('helper')
        def utype       = helper.sess('utype')


        _SQL_CLASS = """
        select a.*
        from ST_CLASS a
        where a.CLASS_ID=?
        """
        def sql = new Sql(helper.connection)

        def class_id = helper.fetch_keep('c')
        the_class = sql.firstRow(_SQL_CLASS, [class_id])

        if(the_class.semester.equals(5) && !utype.equals("T")){
                println """ 權限不足。 """
                return
        }

%>

<input type="hidden" name="module" value="show_lesson" />
<div class="panel-lesson-show-lesson">

	<h2>${helper.attr('title')}</h2>
	
	<div class="lesson-toolbar">
		<div class="lesson-toolbar-date" style="font-size: 13px; line-height: 1.6em; padding: 10px">
			<div>
				<a href="${helper.attr('date_url')}" target="_blank"><img src="icon-16/calendar.png" alt="date" border="0" /></a>
				練習開放日期自
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'begin_date', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('begin_date')}</span>
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'begin_time', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('begin_time')}</span>
				起至
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'due_date', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('due_date')}</span>
				<span class="datetime"<%if (helper.attr('is_teacher')) println " onclick=\"change_datetime(this, 'due_time', '${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}')\"" %>>${helper.attr('due_time')}</span>
				止
			</div>
			
			<!--<%if(helper.attr('close_date')){%>
			<a href="${helper.attr('date_url')}" target="_blank"><img src="icon-16/calendar.png" alt="date" border="0" /></a>
			編輯器開放至 ${helper.attr('close_date')} ${helper.attr('close_time')}
			<%}%>-->
		</div>

		<div class="lesson-toolbar-control">
			<%if (helper.attr('allow_editor')) {%>
			<a class="fancy-button" href="${helper.attr('editor_url')}">
				<span class="icons ss_plugin_edit"></span>
				<span>開始練習</span>
			</a>
			<%}%>
			
			<a class="fancy-button-gray ajax-load-state" href="#" onclick="ajax_load_state('${helper.attr('class_id')}', '${helper.attr('course_id')}', '${helper.attr('lesson_id')}');return false;" title="更新狀態">
				<span class="icons ss_chart_bar"></span>
				<span>更新狀態</span>
			</a>
			
			<%if (helper.attr('is_show_answer') && helper.attr('answer_url')) {%>
				<a class="fancy-button-gray embedded-link" href="${helper.attr('answer_url')}" target="_blank" title="觀看解答">
					<span class="icons ss_chart_bar"></span>
					<span>觀看解答</span>
				</a>
			<%}%>
			
			<%if (helper.attr('is_teacher')) {%>
				<a class="fancy-button-gray embedded-link" href="${helper.attr('report_url')}" target="_blank" title="查看練習進度">
					<span class="icons ss_chart_bar"></span>
					<span>查看練習進度</span>
				</a>
			<%}%>
			
			<%if (helper.attr('isGradeSet')) {%>
				<a style="margin-top:15px;" class="fancy-button-gray embedded-link" href="${helper.attr('gradeSet')}" target="_blank" title="設定配分">
					<span class="icons ss_chart_bar"></span>
					<span>設定配分</span>
				</a>
			<%}%>
		</div>
	</div>
	
	<h3>學習狀態</h3>
	<div class="lesson-state-panel" style="width:560px;height:160px;margin:5px;">
		<div class="html-lesson-state"><img src="img/ajax-loader-1.gif"/></div>
	</div>

	<h3>教材內容</h3>
	<%if (helper.attr('is_teacher')) {%>
		<a href="${helper.attr('edithtml_url')}" class="embedded-link" style="float:right">編輯內容</a>
	<%}%>
	<div class="html-content-string"><div class="prettyhtml">${helper.attr('html_text')}</div></div>

	<div id="disqus_thread"></div>
	<script type="text/javascript">
	    var disqus_shortname = 'myplweb';
	    var disqus_identifier = '${helper.attr('disqus_identifier')}';
	    (function() {
	        var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
	        dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
	        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
	    })();
	</script>
	<noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
</div>
