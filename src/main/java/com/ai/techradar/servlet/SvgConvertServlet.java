package com.ai.techradar.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
//import com.lowagie.text.Section;

public class SvgConvertServlet extends HttpServlet {

	private static final long serialVersionUID = -6114901392531397724L;

	//private RadarService service = new RadarServiceImpl();

	/** The fonts for the title. */
	public static final Font[] FONT = new Font[4];
	static {
		FONT[0] = new Font(Font.HELVETICA, 24);
		// FONT[1] = new Font(FontFamily.HELVETICA, 18);
		// FONT[2] = new Font(FontFamily.HELVETICA, 14);
		FONT[3] = new Font(Font.HELVETICA, 12, Font.BOLD);
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment;filename=\"export.pdf\"");
			
			final Long id = Long.parseLong(request.getParameter("id"));

			PDFGeneratorSecond test = new PDFGeneratorSecond();
			test.MakePDF(response, id);

			response.flushBuffer();
		} catch (Exception e) {
			// TODO log properly
			e.printStackTrace();
		}

	}

}
