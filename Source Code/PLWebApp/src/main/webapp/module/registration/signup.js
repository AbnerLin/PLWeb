WebFace.Register('registration', 'signup', 'HookInit', function(module, prog) {
	var ct = '#'+module+'-'+prog+'-container';

	WebFace.Register(module, prog, 'HookOnLoad', function() {
		var form = $('form[name="form1"]', ct);
		
		$('button.control-button', ct).button({icons:{primary:'ui-icon-person'}});
		
		$('td.required', ct).append($('<span class="required">*</span>'));
		
		$('button.form-submit', form).click(function() {
			WebFace.Invoke(module, prog, 'FormSave');
		});
		
	});
	
	WebFace.Register(module, prog, 'FormSave', function() {
		var form = $('form[name="form1"]', ct);
		WebFace.Submit(form);
	});
});