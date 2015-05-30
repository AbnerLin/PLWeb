import java.awt.*
import java.awt.geom.*
import java.awt.image.*
import java.io.*
import javax.imageio.*

def lightPath = request.getRealPath('/light')

def circle(path, color) {
	  BufferedImage bi = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);

	  Graphics2D ig2 = bi.createGraphics();
	  
	  circle = new Ellipse2D.Double(0, 0, 12, 12)
	  ig2.setColor(Color.decode('#'+color));
	  ig2.fill(circle);

	  ImageIO.write(bi, "PNG", new File(path, "${color}.png"));
}

def line(path, color) {
	
	  BufferedImage bi = new BufferedImage(12, 40, BufferedImage.TYPE_INT_ARGB);

	  Graphics2D ig2 = bi.createGraphics();
	  
	  ig2.setPaint(Color.decode('#555555'));
	  ig2.fill (new Rectangle(0, 0, 12, 40));
	  
	  (0..100).each {
		  height->
		  r_height = ((height/100)*35).toInteger()+5
		  
		  ig2.setPaint(Color.decode('#'+color));
		  ig2.fill (new Rectangle(0, 40-r_height, 12, r_height));
		  ig2.setPaint(Color.decode('#FFFFFF'));
		  ig2.fill (new Rectangle(0, 40-r_height, 12, 1));

		  ImageIO.write(bi, "PNG", new File(path, "${color}_${height}.png"));
	  }
}

def time(path) {
	
	  BufferedImage bi = new BufferedImage(100, 40, BufferedImage.TYPE_INT_ARGB);

	  Graphics2D ig2 = bi.createGraphics();
	  
	  ig2.setPaint(Color.decode('#555555'));
	  ig2.fill (new Rectangle(0, 0, 100, 40));
	  
	  (0..100).each {
		  width->
		  ig2.setPaint(Color.decode('#00FFFF'));
		  ig2.fill (new Rectangle(0, 0, width, 40));
		  ig2.setPaint(Color.decode('#FFFFFF'));
		  ig2.fill (new Rectangle(width, 0, 1, 40));

		  ImageIO.write(bi, "PNG", new File(path, "TIME_${width}.png"));
	  }
}

def time2(path) {
	
	  BufferedImage bi = new BufferedImage(100, 12, BufferedImage.TYPE_INT_ARGB);

	  Graphics2D ig2 = bi.createGraphics();
	  
	  ig2.setPaint(Color.decode('#555555'));
	  ig2.fill (new Rectangle(0, 0, 100, 12));
	  
	  (0..100).each {
		  width->
		  ig2.setPaint(Color.decode('#00FFFF'));
		  ig2.fill (new Rectangle(0, 0, width, 12));
		  ig2.setPaint(Color.decode('#FFFFFF'));
		  ig2.fill (new Rectangle(width, 0, 1, 12));

		  ImageIO.write(bi, "PNG", new File(path, "SMALLTIME_${width}.png"));
	  }
}


try {
circle(lightPath, "00FF00")
circle(lightPath, "556B2F")
circle(lightPath, "FFFF00")
circle(lightPath, "FF0000")
circle(lightPath, "AAAAAA")

line(lightPath, "00FF00")
line(lightPath, "556B2F")
line(lightPath, "FFFF00")
line(lightPath, "FF0000")
line(lightPath, "AAAAAA")

time(lightPath)
time2(lightPath)
}
catch(e) {
	println e
}
