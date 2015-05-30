import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

sql = new Sql(helper.connection)

//row = sql.firstRow(query1)

sql.close()

helper.attr 'admin_url', helper.make_url('admin.system/entry.groovy', [], true)
helper.attr 'is_admin', helper.sess('is_admin')
helper.attr 'helper',	helper

helper.include('page_header.gsp')