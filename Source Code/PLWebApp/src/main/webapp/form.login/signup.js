$(document).ready(function() {
	$('input[name*=email]').change(function(e) {
		$('.msg-email').hide();
		$.ajax({
			url : 'form.login/signup_ajax.groovy',
			data: { field: 'email', value1: $('input[name=email]').val(), value2: $('input[name=email2]').val() },
			error : function(xhr) {
			},
			success : function(response) {
				$('.msg-email').html(response);
				$('.msg-email').fadeIn();
			}
		});
	});
	
	$('input[name*=password]').change(function(e) {
		$('.msg-password').hide();
		$.ajax({
			url : 'form.login/signup_ajax.groovy',
			data: { field: 'password', value1: $('input[name=password]').val(), value2: $('input[name=password2]').val() },
			error : function(xhr) {
			},
			success : function(response) {
				$('.msg-password').html(response);
				$('.msg-password').fadeIn();
			}
		});
	});
	
	$('input[name*=name]').change(function(e) {
		$('.msg-name').hide();
		$.ajax({
			url : 'form.login/signup_ajax.groovy',
			data: { field: 'name', value1: $('input[name=name]').val() },
			error : function(xhr) {
			},
			success : function(response) {
				$('.msg-name').html(response);
				$('.msg-name').fadeIn();
			}
		});
	});
	
	$('input[name*=enrollment]').change(function(e) {
		$('.msg-enrollment').hide();
		$.ajax({
			url : 'form.login/signup_ajax.groovy',
			data: { field: 'enrollment', value1: $('input[name=enrollment]').val() },
			error : function(xhr) {
			},
			success : function(response) {
				$('.msg-enrollment').html(response);
				$('.msg-enrollment').fadeIn();
			}
		});
	});
	
	$('.form-signup input').trigger('change');
	
});
