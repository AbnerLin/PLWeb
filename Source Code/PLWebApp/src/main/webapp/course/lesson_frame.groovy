import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def class_id = request.getParameter('class_id')
def course_id = request.getParameter('course_id')
def lesson_id = request.getParameter('lesson_id')

html.setDoubleQuotes(true)
html.html {
	frameset (rows:'60,*', framespacing:'0', frameborder:'no', border:'0') {
		frame (name:'lessonHead', src:"lesson_view.groovy?class_id=${class_id}&course_id=${course_id}&lesson_id=${lesson_id}", frameborder:'no', scrolling:'no', noresize:'noresize', marginwidth:'0')
		frame (name:'lessonBody', src:'lesson_loading.groovy', frameborder:'no', scrolling:'auto', noresize:'noresize', marginwidth:'0')
	}
}
