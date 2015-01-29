package com.ai.techradar.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import com.lowagie.text.Paragraph;
//import com.lowagie.text.Section;

public class SvgConvertServlet extends HttpServlet {

	private static final long serialVersionUID = -6114901392531397724L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment;filename=\"export.pdf\"");

			final Long id = Long.parseLong(request.getParameter("id"));

			final PDFGeneratorSecond test = new PDFGeneratorSecond();
			test.makePDF(response, id);

			response.flushBuffer();
		} catch (final Exception e) {
			// TODO log properly
			e.printStackTrace();
		}

	}

}
