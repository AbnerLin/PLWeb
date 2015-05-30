WebFace.Register('dashboard', 'class', 'HookInit', function(module, prog) {
	var ct = '#'+module+'-'+prog+'-container';

	WebFace.Register(module, prog, 'HookOnLoad', function() {
		$('div.accordion', ct).accordion({
			autoHeight: false,
			navigation: true
		});
	});
	
	WebFace.Register(module, prog, 'ShowClass', function(class_id) {
		WebFace.EffectLoading($('div.class-show', ct), '01');
		WebFace.LoadAuto($('div.class-show', ct), module, 'class_show', {class_id: class_id});
	});
});