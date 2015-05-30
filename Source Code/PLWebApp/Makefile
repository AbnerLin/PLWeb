server:
	gradle jettyRun

upload:
	s3cmd -P put build/libs/PLWebApp.war s3://s3.plweb.org/webapps/

clean:
	gradle clean

war:
	gradle war
