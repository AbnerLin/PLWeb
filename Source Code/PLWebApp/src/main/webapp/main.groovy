import groovy.xml.MarkupBuilder

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Programming Learning Web')
	}
	frameset (name: 'fullFrameset', rows:'60,*,25', cols:'*', frameborder: 'no') {
		frame (src: 'top.groovy', scrolling: 'no', noresize: 'noresize')
		
		frameset (name: 'bodyFrameset', rows:'*', cols:'30%,70%', frameborder: 'yes') { 
			frame (src: 'class.groovy', scrolling: 'auto', name: 'leftFrame')
			frame (src: 'manual.html', scrolling: 'auto', name: 'mainFrame')
		}
		
		frame (src: 'refresh.groovy', scrolling: 'auto', name: 'hideFrame', noresize: 'noresize')
	}
	body {
	}
}
