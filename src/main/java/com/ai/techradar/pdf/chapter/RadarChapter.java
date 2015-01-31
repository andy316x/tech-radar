package com.ai.techradar.pdf.chapter;

import java.awt.Color;

/**
 * Contains the contents of a chapter within the PDF document describing a radar.
 */
public class RadarChapter {

	private final int index;

	private final String heading;

	private final Color highlightColour;

	private String pdfTitle;

	private Integer pdfPageNumber;

	public RadarChapter(final int index, final String heading, final Color highlightColour) {
		this.index = index;
		this.heading = heading;
		this.highlightColour = highlightColour;
	}

	public int getIndex() {
		return index;
	}

	public String getHeading() {
		return heading;
	}

	public Color getHighlightColour() {
		return highlightColour;
	}

	public String getPdfTitle() {
		return pdfTitle;
	}

	public void setPdfTitle(final String pdfTitle) {
		this.pdfTitle = pdfTitle;
	}

	public Integer getPdfPageNumber() {
		return pdfPageNumber;
	}

	public void setPdfPageNumber(final Integer pdfPageNumber) {
		this.pdfPageNumber = pdfPageNumber;
	}
}
