function lessonPlay(course_id, lesson_id) {
	window.open('/lesson_play.groovy?course_id='+course_id+'&lesson_id='+lesson_id, 'lessonPlay', config='height=600,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no,resizable=yes,location=no,status=no');
}
