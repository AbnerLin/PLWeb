import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

ann_text = helper.fetch('text')
ann_color = helper.fetch('color')
ann_date = new Date().time

query1 = """
insert into ST_ANNOUNCE (ANN_TEXT,ANN_COLOR,ANN_DATE) values(?,?,?)
"""
sql.executeInsert(query1, [ann_text, ann_color, ann_date])

sql.close()

response.sendRedirect('announce.groovy')