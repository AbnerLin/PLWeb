$(document).ready(function() {
	$('div.login-message').spotlight();
	
	$('input[name=email]').change(function(e) {
		$('.msg-email').hide();
		$.ajax({
			url : 'form.login/login_ajax.groovy',
			data: { field: 'email', value: $(e.target).val() },
			error : function(xhr) {
			},
			success : function(response) {
				$('.msg-email').html(response);
				$('.msg-email').fadeIn();
			}
		});
	});
	if ($('input[name=email]').val() == '') {
		$('input[name=email]').focus();
	}
	else {
		$('input[name=password]').focus();
	}
	
//	$('button.form-button-login').click(function(e) {
//		$(this).attr('disabled', true);
//	});
});
