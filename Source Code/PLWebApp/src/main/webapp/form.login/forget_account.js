$(document).ready(function() {
	
	$('.encoded-email').each(function() {
		var t = $(this).text();
		$(this).text(t.replace(' * ', '@'));
	});
	
});
