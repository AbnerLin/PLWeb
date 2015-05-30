import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

ann_id = request.getParameter('id')

query1 = """
delete from ST_ANNOUNCE where ANN_ID=?
"""
sql.execute(query1, [ann_id])

sql.close()

response.sendRedirect('announce.groovy')