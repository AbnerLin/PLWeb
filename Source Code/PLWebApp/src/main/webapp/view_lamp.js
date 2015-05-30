var Tid;
function doLoad(){
		Tid = setTimeout( "refresh()", 60*1000 );
		document.getElementById("doNotLoad").disabled=false;
		document.getElementById("doLoad").disabled=true;
}
function doNotLoad(){
		clearTimeout(Tid);
		document.getElementById("doNotLoad").disabled=true;
		document.getElementById("doLoad").disabled=false;
}
function refresh(){
		window.location.reload( true );
}
