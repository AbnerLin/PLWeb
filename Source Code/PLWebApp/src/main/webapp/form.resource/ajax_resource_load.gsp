<%helper=request.get('helper')%>
<%row=helper.attr('resource')%>
<div class="resource-header">
	<div style="float:right">
		<button class="btn-control layout-classic" title="Switch to classic interface" onclick="fancybox_link('${helper.attr('sort_url')}', {type: 'iframe', width: '60%', height: '90%'})">
			<img src="icon-16/application.png" alt="Switch to classic interface" />
			<span>Sort</span>
		</button>
	</div>
	<form action="form.resource/ajax_resource_save.groovy" method="post">
		<input type="hidden" name="course_id" value="${row.id}" />
		<div class="resource-title">${row.title}</div>
	</form>
</div>
<hr/>
<ul class="root">
<%helper.attr('chapters').each{row->%>
	<li id="chapter-${row.pid}-${row.id}" class="chapter">
		<div class="chapter-attribute">
			<span class="pid">${row.pid}</span>
			<span class="id">${row.id}</span>
		</div>
		<div class="chapter-header">
			<span class="title">${row.title}</span>
		</div>
		<div class="chapter-content">
			<div class="chapter-control">
				<button class="btn-control" onclick="location.href='${row.editor_url}'">
					<img src="icon-16/book_edit.png" alt="Open PLWeb resource editor." />
					<span>Open Editor</span>
				</button>
			
				<button class="btn-control class-control-import">
					<img src="icon-16/page_link.png" alt="Import HTML from URL" />
					<span>Import</span>
				</button>
				
				<button class="btn-control class-control-reimport">
					<img src="icon-16/page_link.png" alt="Re-Import HTML from URL" />
					<span>Re-Import</span>
				</button>
			</div>
			<div class="chapter-html-string prettyhtml">Loading</div>
			<div class="chapter-html-panel"></div>
		</div>
		<div class="chapter-footer">
			<span class="num">${row.num} exercise<%=row.num>1?'s':''%></span>
			,
			<span class="updated">last update: ${row.updated}</span>
		</div>
	</li>
<%}%>
</ul>