package org.plweb.webapp.filter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CustomServletResponseWrapper extends HttpServletResponseWrapper {

	public CustomServletResponseWrapper(HttpServletResponse response) {
		super(response);
		// TODO Auto-generated constructor stub
	}

}
