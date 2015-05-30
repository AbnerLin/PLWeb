WebFace.Register('sys_admin', 'teacher', 'HookInit', function(module, prog) {
	var ct = '#'+module+'-'+prog+'-container';

	WebFace.Register(module, prog, 'HookOnLoad', function() {
		var form = $('form[name="form1"]', ct);
		$('button.control-button', ct).button({icons:{primary:'ui-icon-person'}});
	});
	
	WebFace.Register(module, prog, 'FormSave', function() {
		var form = $('form[name="form1"]', ct);
		WebFace.Submit(form);
	});
	
	WebFace.Register(module, prog, 'PageReload', function() {
		WebFace.LoadAuto($(ct).parent(), module, prog);
	});
	
	WebFace.Register(module, prog, 'RemoveTeacher', function(uid) {
		WebFace.Execute(module, prog+'?action=remove_teacher', {user_id: uid});
	});
});