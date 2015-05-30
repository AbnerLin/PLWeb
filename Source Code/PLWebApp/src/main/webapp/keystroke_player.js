var temps = [];
var buf = '', buf1, buf2, time1 = 0;
var delays = [];

function init(type, offset, string, length, time) {
	if (type=='i') {
		buf1 = buf.substring(0, offset);
		buf2 = buf.substring(offset, buf.length);
		buf = buf1 + (string.replace(/&gt;/g, '>').replace(/&gt;/g, '<').replace(/&amp;/g, '<')) + buf2;
	}
	else if (type=='r') {
		buf1 = buf.substring(0, offset);
		buf2 = buf.substring(parseInt(offset)+parseInt(length), buf.length);
		buf = buf1 + buf2;
	}
	temps.push(buf);
	if (time1 > 0) {
		delay = time-time1;
		if (delay > 2000) delay = 2000;
		delays.push(delay);
	}
	time1 = time;
}

function show() {
	var idx = parseInt(document.getElementById('idxnum').value);
	
	if (idx >= 0 && idx < temps.length) {
		document.getElementById('buf').value = temps[idx];
		
		nextidx = idx + 1;
		
		document.getElementById('idxnum').value = nextidx;
		
		if (nextidx < temps.length) {
			window.setTimeout(show, delays[idx]);
		}
	}
}

function reset() {
	document.getElementById('idxnum').value = 0;
}

