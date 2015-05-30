PLWEB_COMMON_ONREADY.push(function() {
	$('.content-control button.layout-sidebar').toggle(function() {
		$('.content-left-inner').hide();
		$('.content-left').addClass('expand');
		$('.content-center').addClass('expand');
		
	}, function() {
		$('.content-left-inner').show();
		$('.content-left').removeClass('expand');
		$('.content-center').removeClass('expand');
	});
	
	$('button.ajax-resource-list-refresh').click(function() {
		$('button.ajax-resource-list-refresh').attr('disabled', true);
		$('.ajax-resource-list').fadeOut();
		ajax_resource_list();
	});
	
	ajax_resource_list();
});

function ajax_resource_list() {
	$.ajax({
		url : 'form.resource/ajax_resource_list.groovy',
		type : 'POST',
		data: { },
		error : function(xhr) {
			$('.ajax-resource-list').html(xhr);
		},
		success : function(response) {
			$('.ajax-resource-list').html(response);
			$('.ajax-resource-list').fadeIn();
			$('button.ajax-resource-list-refresh').removeAttr('disabled');
		}
	});
}

function ajax_resource_load(id) {
	$('.ajax-resource-load').hide();
	$.ajax({
		url : 'form.resource/ajax_resource_load.groovy',
		type : 'POST',
		data: { course_id: id },
		error : function(xhr) {
			$('.ajax-resource-load').html(xhr);
		},
		success : function(response) {
			$('.ajax-resource-load').html(response);
			$('.ajax-resource-load').show();
			
			$('.ajax-resource-load .title').toggle(function(){
				var target = $(this).parent().parent();
				
				$(target).toggleClass('selected');
				var pid = $('.chapter-attribute .pid', target).text();
				var id = $('.chapter-attribute .id', target).text();
				ajax_resource_html(pid, id);
				$('.chapter-content', target).show();
			}, function() {
				var target = $(this).parent().parent();
				
				$(target).toggleClass('selected');
				$('.chapter-content', target).hide();
			});
			
			// [Import] button
			$('.class-control-import').click(function(){
				var target	= $(this).parent().parent().parent();
				var pid		= $('.chapter-attribute .pid', target).text();
				var id		= $('.chapter-attribute .id', target).text();
				
				ajax_resource_import(pid, id, target);
			});
			
			$('.class-control-reimport').click(function(){
				var target	= $(this).parent().parent().parent();
				var pid		= $('.chapter-attribute .pid', target).text();
				var id		= $('.chapter-attribute .id', target).text();
				
				ajax_resource_reimport(pid, id, target);
			});
		}
	});
}

function ajax_resource_import(pid, id, target) {
	$.ajax({
		url : 'form.resource/ajax_resource_import.groovy',
		type : 'POST',
		data: { course_id: pid, lesson_id: id },
		error : function(xhr) {
			alert(xhr);
		},
		success : function(response) {
			$('.chapter-html-panel', target).html(response);
			$('.chapter-html-string', target).hide();
			$('.chapter-html-panel', target).show();
		}
	});
}

function ajax_resource_reimport(pid, id, target) {
	$.ajax({
		url : 'form.resource/ajax_resource_reimport.groovy',
		type : 'POST',
		data: { course_id: pid, lesson_id: id },
		error : function(xhr) {
			alert(xhr);
		},
		success : function(response) {
			alert(response);
			ajax_resource_html(pid, id);
		}
	});
}

function ajax_resource_html(pid, id) {
	$.ajax({
		url : 'form.resource/ajax_resource_html.groovy',
		type : 'POST',
		data: { course_id: pid, lesson_id: id },
		error : function(xhr) {
			alert(xhr);
		},
		success : function(response) {
			var target = $('#chapter-'+pid+'-'+id);
			$('.chapter-html-string', target).html(response);
			$('.chapter-html-panel', target).hide();
			$('.chapter-html-string', target).show();
		}
	});
}