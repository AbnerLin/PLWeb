PLWEB_COMMON_ONREADY.push(function() {
	$('.content-control button').toggle(function() {
		$('.content-left-inner').hide();
		$('.content-left').addClass('expand');
		$('.content-center').addClass('expand');
		
	}, function() {
		$('.content-left-inner').show();
		$('.content-left').removeClass('expand');
		$('.content-center').removeClass('expand');
	});
});