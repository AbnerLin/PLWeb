import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.general.DefaultPieDataset
import org.jfree.chart.encoders.KeypointPNGEncoderAdapter
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper     = new CommonHelper(request, response, session)
sql        = new Sql(helper.connection)
piedataset = new DefaultPieDataset()

class_id = helper.fetch('class_id')

query = """
select
	distinct COURSE_ID,
	(select COURSE_TITLE from COURSE b where b.COURSE_ID=a.COURSE_ID) as COURSE_TITLE,
	(select count(COURSE_ID) from CLASS_COURSE c where c.COURSE_ID=a.COURSE_ID) as COURSE_COUNT
from CLASS_COURSE a
where CLASS_ID=?
"""

sql.eachRow(query, [class_id]) {
	row->
	piedataset.setValue(row.COURSE_TITLE, row.COURSE_COUNT)
}

sql.close()

chart = ChartFactory.createPieChart('', piedataset, *[true, true, true])
//chart.textAntiAlias = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
chart.title.font = new Font('Sans', Font.PLAIN, 20)
//chart.legend.itemFont = new Font('Sans', Font.BOLD, 12)
chart.removeLegend()
chart.borderVisible = false
chart.borderPaint = new Color(255,255,255,0)
chart.backgroundPaint = new Color(255,255,255,0)
chart.plot.labelFont = new Font('Sans', Font.PLAIN, 14)
chart.plot.backgroundPaint = new Color(255,255,255,0)

encoder = new KeypointPNGEncoderAdapter()
encoder.encodingAlpha = true

cb = encoder.encode(chart.createBufferedImage(560, 200, BufferedImage.BITMASK, null))

response.setContentType('image/png')		 
response.setContentLength(cb.length)
response.getOutputStream().write(cb)
