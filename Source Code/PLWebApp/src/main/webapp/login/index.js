$(document).ready(function() {
	var __html_css_overflow = '';
	$(document).bind('cbox_open', function () {
		__html_css_overflow = $('html').css('overflow');
		$('html').css({overflow: 'hidden'});
	}).bind('cbox_closed', function () {
		$('html').css({overflow: __html_css_overflow});
	});

	// Use colorbox iframe for all embedded links.
	$('a.embedded-link').colorbox({iframe: true, width: '95%', height: '95%'});
	$('a.embedded-slide').colorbox({slideshow: false});

	var login_init = function() {
		$('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});
		
		var emailfield = $('input[name=email]');
		var passwordfield = $('input[name=password]');

		var check_by_ajax = function(e) {
		    emailfield.removeClass('correct').removeClass('error');
		    
			$.ajax({
				type: 'post',
				dataType: 'json',
				url : $('input[name="ajax_url"]').val(),
				data: {
					action: 'checkemail',
					email: emailfield.val()
				},
				error : function(xhr) {
				},
				success : function(o) {
				    if (emailfield.val() != '') {
				    	emailfield.addClass(o.emailok?'correct':'error');
                    }
				}
			});
		};
		
		$(emailfield).change(check_by_ajax);
		$(emailfield).keyup(function() {
			$(this).removeClass('error').removeClass('correct');
		});
		if (emailfield.val() == '') {
			emailfield.focus();
		}
		else {
			passwordfield.focus();
		}		
		if (emailfield.val() != '') {
		    check_by_ajax();
		}
	};
	
	var signup_init = function() {
		$('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});
		
		$('input[name=roletype]').click(function() {
		        var v = $(this).val();
		        $('.roletype-desc').hide();
		        $('#roletype-desc-'+v).show();
		});

		Recaptcha.create(
			$('input[name="recaptcha_public_key"]').val(),
			"recaptcha_element",
			{
				theme: "white",
				callback: Recaptcha.focus_response_field
			}
		);
		
		var v = $('input[name=roletype]:checked').val();
		$('.roletype-desc').hide();
        $('#roletype-desc-'+v).show();
	};
	
	var account_init = function() {
	    $('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});

	    	$('.pre-encode').each(function() {
            var t = $(this).text();
            $(this).text(t.replace(' * ', '@'));
        });
	};
	
    var password_init = function() {
	    $('input.input-text').tipsy({trigger: 'focus', gravity: 'w'});

	    Recaptcha.create(
			$('input[name="recaptcha_public_key"]').val(),
			"recaptcha_element",
			{
				theme: "white",
				callback: Recaptcha.focus_response_field
			}
		);
	};
	
	var m = $('input[name=module]').val();
	if (m) {
		eval('if ('+m+'_init) '+m+'_init();');
	}	
});
