PLWEB_COMMON_ONREADY.push(function() {	
	
	$('.class-control-edit').click(function() {
		$('.class-html-editor').show();
		$('.class-html-textarea').width($('.class-html-string').width());
		$('.class-html-textarea').height($('.class-html-string').height()+100);
		$('.class-html-textarea').val($('.class-html-string').html());
		$('.class-html-content').hide();
		$('.class-html-textarea').htmlarea();
	});
	
	$('.class-display-title').toggle(function() {
		$('.class-display-content').hide();
	}, function() {
		$('.class-display-content').show();
	});
	
	$('.class-control-height-add').click(function() {
		var o = $('.class-html-editor .jHtmlArea iframe');
		
		if (o.height() < o.contents().height()) {
			o.height(o.contents().height()+100);
		}
		else {
			o.height(o.height()+100);
		}
	});
	
	$('.class-control-height-sub').click(function() {
		var o = $('.class-html-editor .jHtmlArea iframe');
		
		if (o.height() >= 100) {
			o.height(o.height()-100);
		}
	});
	
	$('.class-control-save').click(function() {
		var htmlString = $('.class-html-textarea').htmlarea('toHtmlString');
		var actionUrl = $('.class-html-editor form').attr('action');
		
		$.ajax({
			url : actionUrl,
			type : 'POST',
			data: { field: 'html_text', value: htmlString },
			error : function(xhr) {
			},
			success : function(response) {
				//alert(response);
			}
		});
		
		$('.class-html-string').html(htmlString);
		$('.class-html-textarea').htmlarea('dispose');
		$('.class-html-editor').hide();
		$('.class-html-content').show();
	});
	
	$('.class-control-cancel').click(function() {
		if (confirm('are you sure?')) {
			$('.class-html-textarea').htmlarea('dispose');
			$('.class-html-editor').hide();
			$('.class-html-content').show();
		}
	});
});