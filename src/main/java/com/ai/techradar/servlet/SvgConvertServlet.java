package com.ai.techradar.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

public class SvgConvertServlet extends HttpServlet {

	private static final long serialVersionUID = -6114901392531397724L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final String data = request.getParameter("data");
		
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition","attachment;filename=\"export.pdf\"");
		
		final Transcoder transcoder = new PDFTranscoder();
		transcoder.addTranscodingHint(PDFTranscoder.KEY_XML_PARSER_VALIDATING, new Boolean(false));
        final TranscoderInput transcoderInput = new TranscoderInput(new ByteArrayInputStream(data.getBytes()));
        final TranscoderOutput transcoderOutput = new TranscoderOutput(response.getOutputStream());
        try {
			transcoder.transcode(transcoderInput, transcoderOutput);
		} catch (final TranscoderException e) {
			System.out.println("TranscoderException: " + e.getMessage());
		}
	}
	
}
