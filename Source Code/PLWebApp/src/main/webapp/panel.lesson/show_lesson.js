PLWEB_COMMON_ONREADY.push(function() {
	$('button.ajax-load-state').click();
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

/**
 * Load answer content using jQuery Ajax
 * @param course_id
 * @param lesson_id
 */
function ajax_load_answer(course_id, lesson_id) {
	fancybox_link('panel.lesson/ajax_load_answer.groovy?course_id='+course_id+'&lesson_id='+lesson_id);
//	$('div.lesson-answer-panel').show();
//	
//	$.ajax({
//		url : 'panel.lesson/ajax_load_answer.groovy',
//		type : 'POST',
//		data: { 
//			course_id: course_id,
//			lesson_id: lesson_id
//		},
//		error : function(xhr) {
//			alert(xhr);
//		},
//		success : function(response) {
//			$('div.html-lesson-answer').html(response);
//			$('div.html-content-string').hide();
//		}
//	});
}

function ajax_load_state(class_id, course_id, lesson_id) {
	$('div.html-lesson-state').html('<img src="img/ajax-loader-1.gif"/>');
	
	$.ajax({
		url : 'panel.lesson/ajax_load_state.groovy',
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