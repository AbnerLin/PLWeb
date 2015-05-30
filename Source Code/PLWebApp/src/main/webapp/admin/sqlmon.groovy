import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def ds = helper.dataSource

html.doubleQuotes = true
html.expandEmptyElements = true
html.omitEmptyAttributes = false
html.omitNullAttributes = false
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title('資料庫連線監控 - PLWeb')
		link(href: "${helper.basehref}stylesheets/screen.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/silk-sprite.css", media: 'screen', rel: 'stylesheet', type: 'text/css')
		link(href: "${helper.basehref}stylesheets/print.css", media: 'print', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<!--[if IE]>')
		link(href: "${helper.basehref}stylesheets/ie.css", media: 'screen, projection', rel: 'stylesheet', type: 'text/css')
		mkp.yieldUnescaped('<![endif]-->')
	}
	body (class: 'admin-layout') {
		h1 ("Database Monitor")
		hr ()
		h2 ('Datasource Properties')
		table (width: '100%', class: 'fancy-table') {
			tr {
				th ('Default Auto-commit')
				td (ds.defaultAutoCommit)
			}

			tr {
				th ('Default Catalog')
				td (ds.defaultCatalog)
			}
			tr {
				th ('Default Read-only')
				td (ds.defaultReadOnly)
			}
			tr {
				th ('Driver Class Name')
				td (ds.driverClassName)
			}
			tr {
				th ('Initial Size')
				td (ds.initialSize)
			}
			tr {
				th ('Pool Prepared Statements')
				td (ds.poolPreparedStatements)
			}
			tr {
				th ('Test On Borrow')
				td (ds.testOnBorrow)
			}
			tr {
				th ('Test On Return')
				td (ds.testOnReturn)
			}
			tr {
				th ('Test While Idle')
				td (ds.testWhileIdle)
			}
			tr {
				th ('Time Between Eviction Runs Millis')
				td (ds.timeBetweenEvictionRunsMillis)
			}
			tr {
				th ('URL')
				td (ds.url)
			}
			tr {
				th ('Username')
				td (ds.username)
			}
			tr {
				th ('Validation Query')
				td (ds.validationQuery)
			}
		}
		
		hr ()

		h3('Max/Min/Num Counters')
		
		table (width: "100%", class: 'fancy-table') {
			tr {
				th ("Type")
				th ("Max.")
				th ("Min.")
				th ("Num.")
			}
			tr {
				td ('Active')
				td (ds.maxActive)
				td ()
				td (ds.numActive)
			}
			tr {
				td ('Idle')
				td (ds.maxIdle)
				td (ds.minIdle)
				td (ds.numIdle)
			}
			tr {
				td ('Wait')
				td (ds.maxWait)
				td ()
				td ()
			}
		}
	}
}

