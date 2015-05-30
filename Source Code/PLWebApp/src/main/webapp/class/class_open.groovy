import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendError 403
	return
}

def utype = session.get('utype')

if (!['T', 'M'].contains(utype)) {
	response.sendRedirect('permission_denied.groovy')
}

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title('建立新課程 - PLWeb')
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
	}
	body (class: 'admin-layout') {
		h1 ('建立新課程')

		a (href: 'index.groovy') {
			span (class: 'icons ss_arrow_undo')
			span ('返回課程管理')
		}
		span (' | ')
		a (href: 'javascript:location.reload()') {
			span (class: 'icons ss_arrow_refresh')
			span ('重新整理')
		}
		
		hr()

		form (action: 'class_open_save.groovy', method: 'post') {
			table (width: 600) {
				tr {
					th (colspan: 2, '填寫新課程設定')
				}
				tr {
					th (width: 150, class: 'verticle', '授課年度')
					td {
						this_year = Calendar.getInstance().get(Calendar.YEAR)
						select (name: 'years') {
							option (value: this_year-1, "${this_year-1}年")
							option (value: this_year+0, "${this_year+0}年", selected: '')
							option (value: this_year+1, "${this_year+1}年")
						}
						span ('（西元年）')
					}
				}
				tr {
					th (class: 'verticle', '學期別')
					td {
						select (name: 'semester') {
							option (value: 3, '上學期')
							option (value: 1, '下學期')
							option (value: 2, '夏季班')
							option (value: 4, '冬季班')
							option (value: 5, '測驗專用')
							option (value: 6, '免登入專用')
						}

						span ('（授課年度、學期設定後無法變更）')
					}
				}
				tr {
					th (class: 'verticle', '課程名稱')
					td {
						input (name: 'name', size: 40)
						p ('請填寫課程或測驗名稱 (例：程式設計概論)')
					}
				}
				tr {
					th (class: 'verticle', '授課學校名稱')
					td {
						input (name: 'school', size: 30)
						p ('請填寫學校或主辦單位名稱 (例：國立雲林科技大學)')
					}
				}
				tr {
					th (class: 'verticle', '授課系所名稱')
					td {
						input (name: 'dept', size: 30)
						p ('請填寫系所名稱 (例：資訊管理系)')
					}
				}
				tr {
					th (class: 'verticle', '密碼')
					td {
						input (type: 'password', name: 'password', autocomplete: 'off')
						span (style: 'color:red', '（測驗專用）')
					}
				}
				tr {
					th (class: 'verticle', '教材授權')
					td {
						p {
							input (id: 'course_type_0', type: 'radio', name: 'course_type', value: 0, checked: 'true')
							label (for: 'course_type_0', '無')
						}
						p {
							input (id: 'course_type_4', type: 'radio', name: 'course_type', value: 4)
							label (for: 'course_type_4') {
								strong ('PLWeb Java 練習與題庫')
							}
						}
						p {
							input (id: 'course_type_1', type: 'radio', name: 'course_type', value: 1)
							label (for: 'course_type_1', 'TQC+ Java 6 教材及題庫 (中區技職校院夥伴學校)')
							a (href: "javascript:alert('大同技術學院、中州技術學院、中臺科技大學、仁德醫護管理專科學校、弘光科技大學、吳鳳科技大學、育達商業科技大學、長庚技術學院、南開科技大學、建國科技大學、修平技術學院、國立虎尾科技大學、國立雲林科技大學、國立勤益科技大學、臺中技術學院、國立台中護理專科學校、崇仁醫護管理專科學校、朝陽科技大學、僑光科技大學、亞太創意技術學院、嶺東科技大學、環球科技大學');", '查詢')
						}
						/*div {
							input (id: 'course_type_2', type: 'radio', name: 'course_type', value: 2)
							label (for: 'course_type_2', 'TQC+ Java 6 教材及題庫 (TQC+認證授權學校或系所)')
						}*/
						/*div {
							input (id: 'course_type_3', type: 'radio', name: 'course_type', value: 3)
							label (for: 'course_type_3', 'TQC+ Java 6 教材及題庫 (授課班級已購買TQC+認證書籍)')
						}*/
					}
				}
				tr {
					td (colspan: 2, align: 'center', class: 'center') {
						input (class: 'fancy-button', type: 'submit', value: '確認送出')
						a (class: 'fancy-button-gray', href: 'index.groovy', '取消')
					}
				}
			}
		}
	}
}
