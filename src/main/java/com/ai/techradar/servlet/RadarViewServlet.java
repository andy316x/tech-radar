package com.ai.techradar.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RadarViewServlet extends HttpServlet {

	private static final long serialVersionUID = 6874524181514526316L;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		if(request.getPathInfo()!=null && request.getPathInfo().length()>0) {
			request.setAttribute("result", request.getPathInfo().substring(1));
		}

		getServletContext().getRequestDispatcher("/radar.jsp").forward(request, response);

	}

}
