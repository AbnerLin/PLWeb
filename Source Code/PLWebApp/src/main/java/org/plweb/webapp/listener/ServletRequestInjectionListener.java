package org.plweb.webapp.listener;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import javax.servlet.*;

public class ServletRequestInjectionListener implements ServletRequestListener{
	public void requestInitialized(ServletRequestEvent sre) {
		ServletRequest req = sre.getServletRequest();
		try {
			req.setAttribute("dbconn", ((DataSource) new InitialContext().lookup("java:comp/env/jdbc/plweb")).getConnection());
		}
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		catch (NamingException ex) {
			System.err.println(ex.getMessage());
		}
	}

	public void requestDestroyed(ServletRequestEvent sre) {
		ServletRequest req = sre.getServletRequest();
		Object obj = req.getAttribute("dbconn");
		if (obj != null && obj instanceof Connection) {
			Connection conn = (Connection) obj;
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			}
			catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}
}
