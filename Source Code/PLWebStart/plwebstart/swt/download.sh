
href='http://eclipse.stu.edu.tw/eclipse/downloads/drops/R-3.7.1-201109091335'

function d {
	wget $href/$1.zip
	mkdir tmp
	unzip $1.zip -d tmp
	mv tmp/swt.jar $1.jar
	rm -rf tmp
	rm $1.zip
}

d swt-3.7.1-win32-win32-x86
d swt-3.7.1-win32-win32-x86_64
d swt-3.7.1-gtk-linux-x86
d swt-3.7.1-gtk-linux-x86_64
d swt-3.7.1-cocoa-macosx
d swt-3.7.1-cocoa-macosx-x86_64
