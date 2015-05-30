$(document).ready(function() {
    WebFace.Layout({
        north: "#main-header-container",
        south: "#main-footer-container",
        west: "#main-menu-container",
        //east: "#main-info-container",
        center: "#main-program-container"
    });

    WebFace.SetStatusSelector('#main-status-bar');

    WebFace.TabInit('#main-tab-container');

    WebFace.LoadAuto('#main-header-sub-container', 'system', 'header');
    WebFace.LoadAuto('#main-menu-container', 'system', 'menu');
});

function open_debugger() {
    WebFace.EnableDebug('div#main-debugger div.main-debugger-output');

    if (WebFace.GetOptimization()) {
        $('span.main-debugger-optimization').html('<font color="green"><b>on</b></font>');
    }
    else {
        $('span.main-debugger-optimization').html('<font color="red"><b>off</b></font>');
    }

    $('div#main-debugger').dialog();

    $('div#main-debugger').bind('dialogclose', function(event, ui) {
        WebFace.DisableDebug();
    });
}

function clear_debugger() {
    $('div#main-debugger div.main-debugger-output').html('');
}

function reload_tab() {
    WebFace.TabReload();
}