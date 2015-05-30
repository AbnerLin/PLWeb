import org.jfree.chart.*
import org.jfree.chart.encoders.*
import org.jfree.data.general.*
import java.awt.image.*
import java.awt.*

piedataset = new DefaultPieDataset();
piedataset.with {
	 setValue 'C/C++', 10
	 setValue 'PHP', 30
	 setValue 'Java', 60
}

chart = ChartFactory.createPieChart('程式語言比例', piedataset, *[true, true, true])
chart.title.font = new Font('細明體', 12, 20)
chart.backgroundPaint = new Color(255,255,255,0)

encoder = new KeypointPNGEncoderAdapter()
encoder.encodingAlpha = true

cb = encoder.encode(chart.createBufferedImage(320, 240, BufferedImage.BITMASK, null))

response.contentType = 'image/png'
response.contentLength = cb.length
response.outputStream.write(cb)