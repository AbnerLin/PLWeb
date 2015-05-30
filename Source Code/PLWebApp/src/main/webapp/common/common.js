WebFace = new WebFaceClass();

WebFace.urlPattern = {
    html: "module/{0}/{1}.groovy",
    css: "module/{0}/{1}.css",
    min_css: "module/{0}/{1}.min.css",
    script: "module/{0}/{1}.js",
    min_script: "module/{0}/{1}.min.js",
    handler: "module/{0}/{1}.ajax.groovy"
};

WebFace.Register('global', 'common', 'HookOnLoad', function(module, prog) {
    var ct = $('#'+module+'-'+prog+'-container');
    //$('button.control-button', ct).button({icon:{primary:'ui-icon-gear'}});
    //$('div.webface-auto-tabs', ct).tabs({selected: 0});
    //$('table tr:odd td', ct).addClass('odd');
    //$('table tr:even td', ct).addClass('even');
});