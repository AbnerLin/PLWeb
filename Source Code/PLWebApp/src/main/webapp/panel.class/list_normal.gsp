<div class="panel-class-list-normal">
<ul>
<%request.get('classes').each { row-> %>
<li class="root<%=row.selected?' selected':''%>">
	<span><a href="${row.href}" title="${row.school} / ${row.dept}" class="<%=row.selected?'selected':''%>">${row.name} <span class="footprint">${row.id}</span></a></span>
	<%if(row.selected){%><span class="description">${row.school} / ${row.dept}</span><%}%>
	<div class="list-lessons">
	<% if (row.lessons) { %>
	<ul>
		<% row.lessons.each { row2-> %>
		<li class="node<%=row2.selected?' selected':''%>" onclick="location.href='${row2.href}';">
			<span class="title">${row2.title}</span>
			<span class="description">${row2.course}</span>
		</li>
		<% } %>
	</ul>
	<% } %>
	</div>
</li>
<% } %>
</ul>
</div>