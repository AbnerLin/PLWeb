$(document).ready(function() {
	
	$("ul.sortable-chapters").sortable({
		opacity: 0.6,
		axis: 'y',
		update: function(event, ui) {
			//nothing
			ui.item.toggleClass('changed');
			$("span.changed-hints").show();
		}
	});
	
	$('button.btn-control-save').click(function(){
		var order = $("ul.sortable-chapters").sortable('serialize');

		$.post($("form[name='sortable']").attr('action'), order, function(data) {
			alert(data);
			$("span.changed-hints").hide();
			$("ul.sortable-chapters li").removeClass('changed');
		});
	});
});
