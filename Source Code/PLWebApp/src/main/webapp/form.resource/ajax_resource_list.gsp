<%helper=request.get('helper')%>
<ul>
<%helper.attr('resources').each{row->%>
	<li onclick="ajax_resource_load('${row.id}')">
		<span><img src="icon-16/book.png" border="0" /></span>
		<span class="title">${row.title}</span>
		<span class="num">(${row.num})</span>
	</li>
<%}%>
</ul>