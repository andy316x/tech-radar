package com.ai.techradar.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;

public class CSVExportServlet extends HttpServlet {

	private static final long serialVersionUID = 7125431699341927732L;

	private final static Logger LOGGER = LoggerFactory.getLogger(CSVExportServlet.class);

	private static final String EXPORTED_FILE_NAME = "export.csv";

	private final RadarService radarService = (RadarService) SpringStarter.getContext().getBean("RadarService");

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		Long radarId = null;

		try {
			radarId = Long.parseLong(request.getParameter("id"));
			LOGGER.debug("Request to export data for radar with ID {} to CSV", radarId);

			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + EXPORTED_FILE_NAME + "\"");

			// Retrieve the radar
			final RadarTO radar = radarService.getRadarById(radarId);

			// Print radar CSV data to StringBuilder
			final StringBuilder csvDataStrBuilder = new StringBuilder();
			final CSVPrinter printer = new CSVPrinter(csvDataStrBuilder, CSVFormat.RFC4180.withHeader());
			printer.printRecord("Technology", "Quadrant", "Maturity", "Moved / No Change", "Project Count", "Product URL", "Description",
					"AI URL", "Customer Strategic");

			for (final RadarTechnologyTO technologyTo : radar.getTechnologies()) {
				final List<String> technologyRecord = new ArrayList<String>();
				technologyRecord.add(technologyTo.getTechnology());
				technologyRecord.add(technologyTo.getTechGrouping());
				technologyRecord.add(technologyTo.getMaturity());
				technologyRecord.add("" + true);
				technologyRecord.add("" + technologyTo.getBlipSize());
				technologyRecord.add(technologyTo.getUrl());
				technologyRecord.add(technologyTo.getDescription());
				technologyRecord.add(technologyTo.getDetailUrl());
				if (technologyTo.isCustomerStrategic()) {
					technologyRecord.add("Y");
				}
				else {
					technologyRecord.add("N");
				}

				printer.printRecord(technologyRecord);
			}

			// Close the printer
			printer.close();

			// Write the contents of the StringBuilder to the HTTP response
			response.getWriter().print(csvDataStrBuilder.toString());
			response.flushBuffer();
		}
		catch (final Exception e) {
			LOGGER.error("An error occurred exporting data for radar with ID {} to CSV", radarId, e);
			throw new ServletException("Failed to export data for radar " + radarId + " to CSV", e);
		}

	}
}
