package com.ai.techradar.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityFilter implements Filter {

	private String role;

	public void doFilter(
			final ServletRequest req, 
			final ServletResponse res,
			final FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		if(!request.isUserInRole(role)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			chain.doFilter(req, res);
		}

	}

	public void init(final FilterConfig config) throws ServletException {
		role = config.getInitParameter("role");
	}

	public void destroy() {

	}

}
