var PLWEB_COMMON_ONREADY = [];

$(document).ready(function() {
	$.each(PLWEB_COMMON_ONREADY, function(index, value) {
		value();
	});
});


function fancybox_link(url, options) {
	$('<a href="'+url+'"></a>').fancybox(options).click();
}

function embedded_link(url) {
	$('<a href="'+url+'"></a>').colorbox({iframe: true, width: '90%', height: '90%'}).click();
}