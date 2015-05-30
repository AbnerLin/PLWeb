PLWEB_COMMON_ONREADY.push(function() {
	for(var i = 0; i < 100; i ++) {
		$('#loop-main').fadeOut();
		$('#loop-main').load('api/sqlmon.groovy');
		$('#loop-main').fadeIn();
	}
});