<% helper = request.get('helper') %>
<div id="sys_status-database-container">
	<h3>資料庫狀態監視器</h3>
	<table>
		<tr>
			<th>Default Auto-commit</th>
			<td>${helper.attr('ds').defaultAutoCommit}</td>
		</tr>
		<tr>
			<th>Default Catalog</th>
			<td>${helper.attr('ds').defaultCatalog}</td>
		</tr>
		<tr>
			<th>Default Read-only</th>
			<td>${helper.attr('ds').defaultReadOnly}</td>
		</tr>
		<tr>
			<th>Driver Class Name</th>
			<td>${helper.attr('ds').driverClassName}</td>
		</tr>
		<tr>
			<th>Initial Size</th>
			<td>${helper.attr('ds').initialSize}</td>
		</tr>
		<tr>
			<th>Pool Prepared Statements</th>
			<td>${helper.attr('ds').poolPreparedStatements}</td>
		</tr>
		<tr>
			<th>Test On Borrow</th>
			<td>${helper.attr('ds').testOnBorrow}</td>
		</tr>
		<tr>
			<th>Test On Return</th>
			<td>${helper.attr('ds').testOnReturn}</td>
		</tr>
		<tr>
			<th>Test While Idle</th>
			<td>${helper.attr('ds').testWhileIdle}</td>
		</tr>
		<tr>
			<th>Time Between Eviction Runs Millis</th>
			<td>${helper.attr('ds').timeBetweenEvictionRunsMillis}</td>
		</tr>
		<tr>
			<th>URL</th>
			<td>${helper.attr('ds').url}</td>
		</tr>
		<tr>
			<th>Username</th>
			<td>${helper.attr('ds').username}</td>
		</tr>
		<tr>
			<th>Validation Query</th>
			<td>${helper.attr('ds').validationQuery}</td>
		</tr>
	</table>
	<h3>資料庫使用率</h3>
	<table>
		<tr>
			<th>類型</th>
			<th>最大數量</th>
			<th>最小數量</th>
			<th>目前數量</th>
		</tr>
		<tr>
			<th>作用中(Active)</th>
			<td>${helper.attr('ds').maxActive}</td>
			<td></td>
			<td>${helper.attr('ds').numActive}</td>
		</tr>
		<tr>
			<th>閒置中(Idle)</th>
			<td>${helper.attr('ds').maxIdle}</td>
			<td>${helper.attr('ds').minIdle}</td>
			<td>${helper.attr('ds').numIdle}</td>
		</tr>
		<tr>
			<th>等待中(Wait)</th>
			<td>${helper.attr('ds').maxWait}</td>
			<td></td>
			<td></td>
		</tr>
	</table>
</div>
