import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	helper.redirect('../form.login/login.groovy')
	return
}

course_id = helper.fetch('course_id')
lesson_id = helper.fetch('lesson_id')

title   = helper.fetch('title', '').trim()
section = helper.fetch('section', '0').trim()

html_preview = false
html_proxy = ''

if (title && section) {
	
	if (helper.fetch('save')!=null) {
		course_id
		lesson_id
		
		sql = new Sql(helper.connection)
		
		html_proxy = helper.make_url('proxy/plweb_wiki.groovy', [title: title, section: section])
		html_text = helper.http_fetch(html_proxy, true)
		
		sql.executeUpdate("""
			update COURSE_FILE
			set HTML_TEXT=?,
			HTML_LINK=?
			where COURSE_ID=?
			and LESSON_ID=?
		""", [html_text, html_proxy, course_id, lesson_id])
		
		sql.close()
		
		println """
		<script type="text/javascript">
		parent.ajax_resource_html('${course_id}', '${lesson_id}');
		</script>
"""
		
		return
	}
	else {
		html_preview = true	
		html_proxy = helper.make_url('proxy/plweb_wiki.groovy', [title: title, section: section])
	}
}

helper.attr 'helper', helper
helper.attr 'course_id', course_id
helper.attr 'lesson_id', lesson_id
helper.attr 'html_preview', html_preview
helper.attr 'html_proxy', html_proxy

helper.forward 'import_plwiki.gsp'
