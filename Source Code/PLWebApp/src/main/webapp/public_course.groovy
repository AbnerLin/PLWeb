import groovy.sql.Sql
import javax.naming.InitialContext

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)


querySelect = """ SELECT content FROM PUBLIC_COURSE """

content = sql.firstRow(querySelect).content

print content