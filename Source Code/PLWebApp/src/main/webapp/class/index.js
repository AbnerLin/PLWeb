$(function () {

	var cyear = $('input[name="cyear"]').val();
	$('#year-filter option[value="'+cyear+'"]').attr('selected', 'selected');

	$('#year-filter').change(function () {
		var cyear = $('option:selected', this).val();
		if (cyear) {
			location.href = "index.groovy?cyear="+cyear;
		}
		else {
			location.href= "index.groovy";
		}
	});

	var ctype = $('input[name="ctype"]').val();
	$('#type-filter option[value="'+ctype+'"]').attr('selected', 'selected');

	$('#type-filter').change(function () {
		var ctype = $('option:selected', this).val();
		if (ctype) {
			location.href = "index.groovy?ctype="+ctype;
		}
		else {
			location.href= "index.groovy";
		}
	});
});