package org.plweb.suite.webstart;

import java.awt.Toolkit;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.gjt.sp.jedit.proto.jeditresource.Handler;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

/**
 * PLWeb Editor Startup: Run Editor Procedure
 * 
 * @author Yan-hong Lin
 * 
 */
public class RunEditor {
	public static void main(String args[]) {

		// Unlock Security Management
		System.setSecurityManager(new UnsafeSecurityManager());

		// Set URL Stream Handler for jeditresource (hack/fix for jEdit Web Start)
		try {
			URLStreamHandlerFactory factory;
			factory = new JEditURLStreamHandlerFactory();
			
			if (factory != null) {
				URL.setURLStreamHandlerFactory(factory);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Setup NativeInterface(DJNativeSwing) and Look&Feel

        try {
    		//NativeInterfaceHandler.init();
    		//Here goes the rest of the initialization
    		//NativeInterfaceHandler.runEventPump();
    		UIUtils.setPreferredLookAndFeel();
    		NativeInterface.open();
    		Toolkit.getDefaultToolkit().setDynamicLayout(true);

        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
        
        changeDefaultLocale();

		// Loader Window
		LoaderJFrame loader = new LoaderJFrame();
		loader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loader.autoload();
	}

	private static void changeDefaultLocale() {
		// change locale
        String language = "en";
        String region = "US";
        if (System.getProperty("user.language") != null) {
        	language = System.getProperty("user.language");
        }
        if (System.getProperty("user.region") != null) {
        	region = System.getProperty("user.region");
        }
        Locale locale = new Locale(language, region);
        Locale.setDefault(locale);
	}
}

/**
 * solve linux plug-in problems
 *
 */
class JEditURLStreamHandlerFactory implements URLStreamHandlerFactory {
	public JEditURLStreamHandlerFactory() {
	}

	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (protocol.equals("jeditresource")) {
			return new Handler();
		}
		return null;
	}
}
