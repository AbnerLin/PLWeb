import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Announcement')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
		script(type: 'text/javascript', src: 'class_admin.js', '')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		h2("Announcement")
		hr ()
		h3("Current Messages")
		table (width:"100%") {
			tr {
				th (width: 40, '#')
				th ('Text')
				th (width: 50, 'Drop')
			}
			c = 0
			sql.eachRow('select * from ST_ANNOUNCE order by ANN_DATE') {
				row ->
				tr (class: c%2==0?'even':'odd') {
					td (align:'center', ++c)
					td {
						font (color: "#${row.ann_color}", row.ann_text)
					}
					td (align: 'center') {
						href_remove = "announce_drop.groovy?id=${row.ann_id}"
						a (href: href_remove, onclick: "return confirm('Are you sure???');") {
							img (src: '../icons/delete.png', border: 0)
						}
					}
				}
			}
		}
		
		hr ()
		h3('Post New Message') {
			form (action: 'announce_save.groovy', method: 'post') {
				table {
					tr {
						th (colspan: 2, 'Message')
					}
					tr {
						th ('Text:')
						td {
							textarea (cols: 40, rows: 5, name: 'text', '')
						}
					}
					tr {
						th ('Color:')
						td {
							select (name: 'color') {
								option (value: '0000FF', 'BLUE')
								option (value: 'FF0000', 'RED')
							}
						}
					}
					tr {
						td (colspan: 2, align: 'right') {
							input (type: 'submit', value: 'Post')
						}
					}
				}
			}
		}
	}
}

sql.close()
