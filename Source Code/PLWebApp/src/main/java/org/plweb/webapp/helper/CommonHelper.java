package org.plweb.webapp.helper;

import groovy.sql.Sql;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class CommonHelper {

	private static final String JNDI_JDBC_PLWEB = "java:comp/env/jdbc/plweb";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	public CommonHelper(HttpServletRequest request, HttpServletResponse response) {
		super();

		this.request = request;
		this.response = response;

		// set self to helper attribute
		this.attr("helper", this);
	}

	public CommonHelper(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		super();

		this.request = request;
		this.response = response;
		this.session = session;

		// set self to helper attribute
		this.attr("helper", this);
	}

	public void forward(String path) throws ServletException, IOException {
		request.getRequestDispatcher(path).forward(request, response);
	}

	public void include(String path) throws ServletException, IOException {
		request.getRequestDispatcher(path).include(request, response);
	}

	public void redirect(String url) throws IOException {
		response.sendRedirect(url);
	}

	public void redirect_keep(String url) {
		// response.setStatus(response.SC_MOVED_TEMPORARILY)
		// response.setHeader('Location' ,url)
		response.setHeader("Refresh", "0; URL=".concat(url));
	}

	public String getServerName() {
		return request.getServerName();
	}

	public int getServerPort() {
		return request.getServerPort();
	}

	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	public String getRemoteHost() {
		return request.getRemoteHost();
	}

	public int getRemotePort() {
		return request.getRemotePort();
	}

	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	public String getServletPath() {
		return request.getServletPath();
	}

	public String getHeader(String name) {
		return request.getHeader(name);
	}

	public Connection getConnection() throws SQLException, NamingException {

		// Retrieve Connection from request attributes
		Object obj = request.getAttribute("dbconn");

		if (obj instanceof Connection) {
			Connection conn = (Connection) obj;
			if (!conn.isClosed()) {
				return (Connection) obj;
			}
		}

		return ((DataSource) new InitialContext().lookup(JNDI_JDBC_PLWEB))
				.getConnection();
	}

	public DataSource getDataSource() throws NamingException {
		return ((DataSource) new InitialContext().lookup(JNDI_JDBC_PLWEB));
	}

	public String fetch(String name) throws UnsupportedEncodingException {
		return fetch(name, null);
	}

	public String fetch(String name, String defaultValue)
			throws UnsupportedEncodingException {
		if (request.getParameter(name) != null) {
			if ("UTF-8".equalsIgnoreCase(request.getCharacterEncoding())) {
				return request.getParameter(name);
			} else if (request.getCharacterEncoding() == null) {
				// return request.getParameter(name);
				String parameter = String.valueOf(request.getParameter(name));
				return new String(parameter.getBytes("ISO8859-1"), "UTF-8");
			} else {
				String parameter = String.valueOf(request.getParameter(name));
				return new String(parameter.getBytes("ISO8859-1"), "UTF-8");
			}
		}
		return defaultValue;
	}

	/**
	 * Fetch GET/POST data from URI or kept in sessions.
	 * 
	 * @param name
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String fetch_keep(String name) throws UnsupportedEncodingException {
		return fetch_keep(name, null);
	}

	public String fetch_keep(String name, String defaultValue)
			throws UnsupportedEncodingException {
		String result;

		// from GET/POST
		result = fetch(name);
		if (result != null) {
			this.sess(request.getServletPath().concat(name), result);
			return result;
		}

		// from SESSION
		Object oresult = sess(request.getServletPath().concat(name));
		if (oresult != null) {
			return String.valueOf(oresult);
		}

		return defaultValue;
	}

	public Object attr(String name) {
		return request.getAttribute(name);
	}

	public void attr(String name, Object value) {
		request.setAttribute(name, value);
	}

	public String sess_id() {
		return session == null ? null : session.getId();
	}

	public Object sess(String name) {
		if (session == null) {
			session = request.getSession(true);
		}
		if (session.getAttribute(name) != null) {
			return session.getAttribute(name);
		}
		return null;
	}

	public void sess_new() {
		session = request.getSession(true);
		Enumeration<?> name = session.getAttributeNames();
		while (name.hasMoreElements()) {
			String key = (String) name.nextElement();
			session.removeAttribute(key);
		}
	}

	public Object sess_fetch(String name) {
		return sess_fetch(name, null);
	}

	public Object sess_fetch(String name, Object defaultValue) {
		if (session == null) {
			session = request.getSession(true);
		}
		if (session.getAttribute(name) != null) {
			return session.getAttribute(name);
		}
		return defaultValue;
	}

	public void sess(String name, Object value) {
		if (session == null) {
			session = request.getSession(true);
		}
		if (value == null) {
			session.removeAttribute(name);
		} else {
			session.setAttribute(name, value);
		}
	}

	public String htmlhead() {
		return htmlhead("");
	}

	public String htmlhead(String pathOfRoot) {
		String basehref = getBasehref();

		StringBuffer sb = new StringBuffer();
		sb.append("<base href=\"").append(basehref).append("\" />");
		sb.append(
				"<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"")
				.append(basehref).append(pathOfRoot)
				.append("css/ui-darkness/jquery-ui-1.8.4.custom.css\" />");
		sb.append(
				"<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"")
				.append(basehref).append(pathOfRoot)
				.append("css/fancybox/jquery.fancybox-1.3.1.css\" />");
		sb.append("<script type=\"text/javascript\" src=\"").append(basehref)
				.append(pathOfRoot)
				.append("js/jquery-1.4.2.min.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\"").append(basehref)
				.append(pathOfRoot)
				.append("js/jquery-ui-1.8.4.custom.min.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\"").append(basehref)
				.append(pathOfRoot)
				.append("js/jquery.fancybox-1.3.1.pack.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\"").append(basehref)
				.append(pathOfRoot).append("js/common.js\"></script>");

		return sb.toString();
	}

	public String getBasehref() {
		return basehref();
	}

	public String basehref() {
		if (this.getServerPort() == 80) {
			return "http://".concat(getServerName()).concat("/");
		} else {
			return "http://".concat(getServerName()).concat(":")
					.concat(String.valueOf(getServerPort())).concat("/");
		}
	}

	public String makeStyleLink(String href) {
		return "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\""
				.concat(href).concat("\" />\n");
	}

	public String makeScriptLink(String href) {
		return "<script type=\"text/javascript\" src=\"".concat(href).concat(
				"\"></script>\n");
	}

	/**
	 * SMTP Configuration for System Administration (default using GMail SMTP
	 * with user authentication)
	 */
	
	private String adminMailHost = "smtp.gmail.com";
	private String adminMailPort = "465";
	private String adminSSL = "on";
	private String adminSMTPUser = "plweb.manager";
	private String adminSMTPPassword = "stainma221";
	private String adminMailFrom = "\"PLWeb Manager\" <plweb.manager@gmail.com>";
	/* original
	private String adminMailHost = "smtp.gmail.com";
	private String adminMailPort = "465";
	private String adminSSL = "on";
	private String adminSMTPUser = "admin@plweb.org";
	private String adminSMTPPassword = "stainma221";
	private String adminMailFrom = "\"PLWeb\" <admin@plweb.org>";
	*/

	public String getAdminMailHost() {
		return adminMailHost;
	}

	public String getAdminMailPort() {
		return adminMailPort;
	}

	public String getAdminSSL() {
		return adminSSL;
	}

	public String getAdminSMTPUser() {
		return adminSMTPUser;
	}

	public String getAdminSMTPPassword() {
		return adminSMTPPassword;
	}

	public String getAdminMailFrom() {
		return adminMailFrom;
	}

	public String getStdDatetime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public String http_fetch(String url) throws IOException {
		return http_fetch(url, false);
	}

	public String http_fetch(String url, boolean prefix) throws IOException {
		URL urlobj = new URL(prefix ? getBasehref().concat(url) : url);
		URLConnection conn = urlobj.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setDoOutput(true);
		conn.connect();
		return conn.getContent().toString();
	}

	public String make_url(String path) throws UnsupportedEncodingException {
		return make_url(path, new HashMap<Object, Object>());
	}

	public String make_url(String path, HashMap<Object, Object> param)
			throws UnsupportedEncodingException {
		return make_url(path, param, false);
	}

	/**
	 * 相容參數不正確的呼叫
	 * 
	 * @param path
	 * @param param
	 * @param prefix
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String make_url(String path, ArrayList<Object> param, boolean prefix)
			throws UnsupportedEncodingException {
		return make_url(path, new HashMap<Object, Object>(), prefix);
	}

	public String make_url(String path, HashMap<Object, Object> param,
			boolean prefix) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		if (prefix) {
			result.append(getBasehref());
		}

		result.append(path);

		if (param.size() > 0) {
			int c = 0;

			for (Object key : param.keySet()) {
				Object value = param.get(key);

				result.append(c > 0 ? "&" : "?");
				result.append(key);
				result.append("=");
				result.append(URLEncoder.encode(String.valueOf(value), "UTF-8"));

				c++;
			}
		}
		return result.toString();
	}

	public boolean getIsTeacher() {
		return "T".equalsIgnoreCase(String.valueOf(sess("utype")));
	}

	public String getDatetimeFormat() {
		return "yyyy/MM/dd HH:mm:ss";
	}

	public String getDateFormat() {
		return "yyyy/MM/dd";
	}

	public String getTimeFormat() {
		return "HH:mm:ss";
	}

	public String getDatetimeString() {
		return getDatetimeString(new Date());
	}

	public String getDatetimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(getDatetimeFormat());
		return sdf.format(new Date());
	}

	/**
	 * 
	 * @param tbname
	 * @param data
	 * @param where
	 * @param sql
	 * @return the number of rows updated or 0 for SQL statements that return
	 *         nothing
	 * @throws SQLException
	 */
	public int simpleSqlUpdate(String tbname, HashMap<Object, Object> data,
			HashMap<Object, Object> where, Sql sql) throws SQLException {
		List<Object> l1 = new ArrayList<Object>();
		List<Object> l2 = new ArrayList<Object>();
		List<Object> l3 = new ArrayList<Object>();

		for (Object k : data.keySet()) {
			Object v = data.get(k);

			l1.add(String.valueOf(k).concat("=?"));
			l2.add(v);
		}

		for (Object k : where.keySet()) {
			Object v = where.get(k);

			l3.add(String.valueOf(k).concat("=?"));
			l2.add(v);
		}

		StringBuilder sqlss = new StringBuilder();
		sqlss.append("update ");
		sqlss.append(tbname);
		sqlss.append(" set ");

		int c = 0;
		for (Object v : l1) {
			if (c > 0)
				sqlss.append(", ");
			sqlss.append(v);
			c++;
		}

		sqlss.append(" where ");

		c = 0;
		for (Object v : l3) {
			if (c > 0)
				sqlss.append(" and ");
			sqlss.append(v);
			c++;
		}

		return sql.executeUpdate(sqlss.toString(), l2);
	}

	/**
	 * 
	 * @param tbname
	 * @param data
	 * @param sql
	 * @return the number of rows updated or 0 for SQL statements that return
	 *         nothing
	 * @throws SQLException
	 */
	public List<List<Object>> simpleSqlInsert(String tbname,
			HashMap<Object, Object> data, Sql sql) throws SQLException {
		List<Object> l1 = new ArrayList<Object>();
		List<Object> l2 = new ArrayList<Object>();
		List<Object> l3 = new ArrayList<Object>();

		for (Object k : data.keySet()) {
			Object v = data.get(k);

			l1.add(k);
			l2.add(v);
			l3.add("?");
		}

		StringBuilder sqlss = new StringBuilder();
		sqlss.append("insert into ");
		sqlss.append(tbname);
		sqlss.append(" (");

		int c = 0;
		for (Object v : l1) {
			if (c > 0)
				sqlss.append(", ");
			sqlss.append(v);
			c++;
		}

		sqlss.append(" ) values(");

		c = 0;
		for (Object v : l3) {
			if (c > 0)
				sqlss.append(", ");
			sqlss.append(v);
			c++;
		}

		sqlss.append(")");

		return sql.executeInsert(sqlss.toString(), l2);
	}
}
