import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

def action = helper.fetch('action')

if (action == 'checkemail') {
    def __SQL__ = 'select count(*) cc from ST_USER where EMAIL=?'

    def email = helper.fetch('email')
    def emailok = false

    if (email != null) {
        row = sql.firstRow(__SQL__, [email])
        
        if (row.cc > 0) {
            emailok = true
        }
    }

    def json = new groovy.json.JsonBuilder()
    json emailok: emailok
    println json.toString()    
}

sql.close()