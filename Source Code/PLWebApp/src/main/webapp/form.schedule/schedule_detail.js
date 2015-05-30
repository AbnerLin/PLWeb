$(document).ready(function() {

	// Click [TabLabel01], [TabLabel02] ...
	$('.tab-labels').click(function(e) {
		
		$('.tab-groups').hide();
		$('#'+this.id.replace('tab-label', 'tab-item')).fadeIn();

	});

});