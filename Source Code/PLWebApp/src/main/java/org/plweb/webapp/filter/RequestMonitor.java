package org.plweb.webapp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RequestMonitor implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		long t1 = System.currentTimeMillis();

		HttpServletResponseWrapper httpRes = new HttpServletResponseWrapper(res);

		httpRes.addHeader("X-UA-Compatible", "chrome=1");

		chain.doFilter(req, httpRes);

		long diff = System.currentTimeMillis() - t1;

		if (diff >= 1000) {
			StringBuilder sb = new StringBuilder();
			sb.append("*** request (");
			sb.append(req.getRequestURI());
			sb.append(") finished with time(ms): ");
			sb.append(diff);

			System.out.println(sb.toString());
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
}
