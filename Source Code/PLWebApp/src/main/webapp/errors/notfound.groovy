import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

html.setDoubleQuotes(true)
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title("無法找到資源 - PLWeb")
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
	}
	body (class: 'admin-layout') {
		h1 ('無法找到資源')
		a (href: '/') {
			span ('返回首頁')
		}
	}
}
