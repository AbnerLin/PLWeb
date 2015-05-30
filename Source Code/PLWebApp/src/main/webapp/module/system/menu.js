WebFace.Register('system', 'menu', 'HookInit', function(module, prog) {
	var ct = '#'+module+'-'+prog+'-container';

	WebFace.Register(module, prog, 'HookOnLoad', function() {
		$('div.accordion', ct).accordion({
			autoHeight: false,
			navigation: true
		});
		
		WebFace.Invoke(module, prog, 'ClickDefaultLink');
	});
	
	WebFace.Register(module, prog, 'PageReload', function() {
		WebFace.LoadAuto($(ct).parent(), module, prog);
		
		WebFace.Invoke(module, prog, 'ClickDefaultLink');
	});
	
	WebFace.Register(module, prog, 'ClickDefaultLink', function() {
		$('a.default-link', ct).click();
	});
	
});