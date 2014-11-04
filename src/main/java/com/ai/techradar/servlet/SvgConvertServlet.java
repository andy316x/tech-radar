package com.ai.techradar.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fop.cli.Main;

import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Section;

public class SvgConvertServlet extends HttpServlet {

	private static final long serialVersionUID = -6114901392531397724L;

	private RadarService service = new RadarServiceImpl();

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

			PDFGeneratorSecond test = new PDFGeneratorSecond();
			test.MakePDF(response);

			response.flushBuffer();
		} catch (Exception e) {
			// TODO log properly
			e.printStackTrace();
		}

	}

	private static void addEmptyLine(Section section, int number) {
		for (int i = 0; i < number; i++) {
			section.add(new Paragraph(" "));
		}
	}

}
