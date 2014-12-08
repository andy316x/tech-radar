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

import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;

public class CSVExportServlet extends HttpServlet {

	private static final long serialVersionUID = 7125431699341927732L;

	private RadarService service = new RadarServiceImpl("");

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment;filename=\"export.csv\"");

			final Long id = Long.parseLong(request.getParameter("id"));

			final RadarTO radar = service.getRadarById(id);

			final StringBuilder strBuilder = new StringBuilder();

			// CSV Write Example using CSVPrinter
			final CSVPrinter printer = new CSVPrinter(strBuilder, CSVFormat.RFC4180.withHeader());
			printer.printRecord("Technology","Quadrant","Maturity","moved / no change","project Count","Product URL","Description","AI URL","Customer strategic");

			for(final RadarTechnologyTO tech : radar.getTechnologies()) {
				final List<String> empData = new ArrayList<String>();
				empData.add(tech.getTechnology());
				empData.add(tech.getTechGrouping());
				empData.add(tech.getMaturity());
				empData.add("" + true);
				empData.add("" + tech.getBlipSize());
				empData.add(tech.getUrl());
				empData.add(tech.getDescription());
				empData.add(tech.getDetailUrl());
				if(tech.isCustomerStrategic()) {
					empData.add("Y");
				} else {
					empData.add("N");
				}

				printer.printRecord(empData);
			}

			// Close the printer
			printer.close();

			response.getOutputStream().print(strBuilder.toString());

			response.flushBuffer();
		} catch (Exception e) {
			// TODO log properly
			e.printStackTrace();
		}

	}

}
