/**
 * @author Yan-hong Lin (lyhcode at gmail.com)
 */

var linkTable;

function resizePageBody() {
	$('div.plweb-schedule-PageBody').height(0);

	$('div.plweb-schedule-PageBody').height(
		$(document).height() - (
			$('div.plweb-schedule-PageSeperator').height() +
			( $('div.plweb-schedule-PageHeader:hidden').length?0:$('div.plweb-schedule-PageHeader').height() ) +
			$('div.plweb-schedule-PageTitle').height()
	));
}

$(document).ready(function() {
	
	$('div.plweb-schedule-PageSeperator').toggle(function() {
		$('div.plweb-schedule-PageHeader').hide(0, resizePageBody);
	}, function() {
		$('div.plweb-schedule-PageHeader').show(0, resizePageBody);
	});

	// Click [Schedule], [My Resources], ...
	$('li.plweb-schedule-VerticalTabPanelLabel').click(function(e) {
		$('li.plweb-schedule-VerticalTabPanelLabel').removeClass('selected');
		$(this).addClass('selected');
		
		links = linkTable[this.id.replace('label-','')];

		for (var i = 0; i < links.length; i++) {
			$("iframe[name='"+links[i].target+"']").attr('src', links[i].href);
		}
	});
	
	$(window).resize(resizePageBody);
	
	resizePageBody();
});

