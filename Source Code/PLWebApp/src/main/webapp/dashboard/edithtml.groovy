import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect 403
	return
}

def uid = session.get('uid')

def class_id = helper.fetch('c')
def combine_id = helper.fetch('l')
def course_id = null
def lesson_id = null
def html_text = helper.fetch('html_text')

if (combine_id) {
	def items = combine_id.split(',')
	course_id = items[0]
	lesson_id = items[1]
}

def _SQL_UPDATE_CLASS = """
	update ST_CLASS
	set HTML_TEXT=?
	where CLASS_ID=?
"""

def _SQL_UPDATE_CLASS_COURSE = """
	update CLASS_COURSE
	set HTML_TEXT=?
	where CLASS_ID=?
	and COURSE_ID=?
	and LESSON_ID=?
"""

def _SQL_CLASS = """
	select a.*
	from ST_CLASS a
	where a.CLASS_ID=?
"""

def _SQL_CLASS_COURSE = """
	select a.*
	from CLASS_COURSE a
	where a.CLASS_ID=?
	and a.COURSE_ID=?
	and a.LESSON_ID=?
"""

def sql = new Sql(helper.connection)

def content_saved = false

if (html_text != null) {
	if (course_id && lesson_id) {
		sql.executeUpdate(_SQL_UPDATE_CLASS_COURSE, [html_text, class_id, course_id, lesson_id])
	}
	else {
		sql.executeUpdate(_SQL_UPDATE_CLASS, [html_text, class_id])
	}
	content_saved = true
}

def the_class = sql.firstRow(_SQL_CLASS, [class_id])

def content = the_class.html_text

def the_lesson = null

if (course_id && lesson_id) {
	the_lesson = sql.firstRow(_SQL_CLASS_COURSE, [class_id, course_id, lesson_id])
	content = the_lesson.HTML_TEXT
}

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title("課程內容編輯 - PLWeb")

		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')

		// jQuery + jQuery UI
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js', '')
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js', '')
		link (rel: 'stylesheet', type: 'text/css', href: 'https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.6/themes/flick/jquery-ui.css')

		// jHtmlArea
		script (type: 'text/javascript', src: "${helper.basehref}js/jHtmlArea-0.7.0.min.js", '')
		link (rel: 'stylesheet', type: 'text/css', href: "${helper.basehref}css/jHtmlArea/jHtmlArea.css")

		script(src: "${helper.basehref}dashboard/edithtml.js", type: 'text/javascript', '')
	}
	body (class: 'admin-layout') {
		h1 ("課程內容編輯")

		p (the_class.CLASS_NAME)

		if (the_lesson) {
			p (the_lesson.TITLE)
		}

		if (content_saved) {
			p (style: 'color: blue', '內容已經儲存！請重新整理網頁，修改後的內容才會顯示在頁面上。')
		}

		form (action: "${request.requestURL}?${request.queryString}", method: 'post') {
			div (style: 'border: 1px solid #88ABC2') {
				textarea (style: 'width:100%;height:300px', name: 'html_text', content)
			}
			br()
			button (class: 'fancy-button', '儲存修改')
		}

	}
}
