WebFace.Register('system', 'login', 'HookInit', function(module, prog) {
	var ct = '#'+module+'-'+prog+'-container';

	WebFace.Register(module, prog, 'HookOnLoad', function() {
		var form = $('form[name="form1"]', ct);
		
		$('button.control-button', ct).button({icons:{primary:'ui-icon-person'}});
		
		$('button.form-submit', form).click(function() {
			WebFace.Invoke(module, prog, 'FormSave');
		});
		
	});
	
	WebFace.Register(module, prog, 'FormSave', function() {
		var form = $('form[name="form1"]', ct);
		WebFace.Submit(form);
	});
	
	WebFace.Register(module, prog, 'AfterLogin', function() {
		WebFace.TabClose('system', 'login');
		WebFace.Invoke('system', 'menu', 'PageReload');
	});
});