import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")

html.html {
	head {
		title('PLWeb - Database Monitor')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css', media: 'all')
		script(type: 'text/javascript', src: 'class_admin.js', '')
	}
	body {
		h2("Database Monitor")
		hr ()
		h3 ('Datasource Properties')
		table (cellpadding: 5, cellspacing: 5) {
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
		h3("Max/Min/Num Counters")
		table (cellpadding: 10, cellspacing: 10) {
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

