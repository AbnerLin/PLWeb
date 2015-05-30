window.onload = showJREsVersion;

function showJREsVersion() { 
	jres = deployJava.getJREs();
	document.getElementById('JREsVersion').innerHTML = (jres.length ? jres : "None");
}