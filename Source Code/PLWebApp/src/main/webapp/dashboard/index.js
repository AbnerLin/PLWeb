$(document).ready(function() {
	
	var __html_css_overflow = '';
	$(document).bind('cbox_open', function () {
		__html_css_overflow = $('html').css('overflow');
		$('html').css({overflow: 'hidden'});
	}).bind('cbox_closed', function () {
		$('html').css({overflow: __html_css_overflow});
	});

	$('a.embedded-link').colorbox({iframe: true, width: '95%', height: '95%'});	

	$('a.new-window-link').click(function() {
		var win1 = window.open($(this).attr('href'), 'plwindow', 'width=800,height=480,resizable=yes,location=no,menubar=no,status=no,toolbar=no,scrollbars=yes');
		//alert($(this).attr('href'));
		if (win1.focus) {
			win1.focus();
		}
		return false;
	});

	var show_lesson_init = function() {
		
		$('.ajax-load-state').click();
	};

	var m = $('input[name=module]').val();
	if (m) {
		eval('if ('+m+'_init) '+m+'_init();');
	}

	$('a.button-show-students').click(function() {
		$('span.head-students').hide();
		$('span.hide-students').show();
		return false;
	});
});

function change_datetime(target, type, class_id, course_id, lesson_id) {
	var date_string = $(target).text();
	var date_input = prompt('Change date '+date_string, date_string);
	
	if (date_input) {
		$.ajax({
			url : 'panel.lesson/ajax_change_datetime.groovy',
			type : 'POST',
			data: { 
				type: type,
				class_id: class_id,
				course_id: course_id,
				lesson_id: lesson_id,
				datetime: date_input
			},
			error : function(xhr) {
				alert(xhr);
			},
			success : function(response) {
				$(target).text(date_input);
			}
		});
	}
}

function ajax_load_state(class_id, course_id, lesson_id) {
	$('div.html-lesson-state').html('<img src="img/ajax-loader-1.gif"/>');
	
	$.ajax({
		url : 'dashboard/ajax_load_state.groovy',
		type : 'POST',
		data: { 
			class_id:	class_id,
			course_id:	course_id,
			lesson_id:	lesson_id
		},
		error : function(xhr) {
			alert(xhr);
		},
		success : function(response) {
			$('div.html-lesson-state').html(response);
		}
	});
}
