package com.ai.techradar.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.techradar.pdf.RadarPdfWriter;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.web.service.to.RadarTO;

/**
 * Servlet responsible for exporting a radar to PDF.
 */
public class PDFExportServlet extends HttpServlet {

	private static final long serialVersionUID = -6114901392531397724L;

	private final static Logger LOGGER = LoggerFactory.getLogger(PDFExportServlet.class);

	private static final String EXPORTED_FILE_NAME = "export.pdf";

	private final RadarService radarService = (RadarService) SpringStarter.getContext().getBean("RadarService");

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		Long radarId = null;

		try {
			radarId = Long.parseLong(request.getParameter("id"));
			LOGGER.debug("Request to export data for radar with ID {} to PDF", radarId);

			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + EXPORTED_FILE_NAME + "\"");

			// Retrieve the radar
			final RadarTO radar = radarService.getRadarById(radarId);

			// Write the PDF to the HTTP response
			final RadarPdfWriter radarPdfWriter = new RadarPdfWriter(radar);
			radarPdfWriter.writeTo(response.getOutputStream());
			response.flushBuffer();
		}
		catch (final Exception e) {
			LOGGER.error("An error occurred exporting data for radar with ID {} to PDF", radarId, e);
			throw new ServletException("Failed to export data for radar " + radarId + " to PDF", e);
		}
	}
}
