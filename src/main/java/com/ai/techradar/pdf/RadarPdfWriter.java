package com.ai.techradar.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.techradar.pdf.chapter.ChapterWrittenEvent;
import com.ai.techradar.pdf.chapter.RadarChapter;
import com.ai.techradar.pdf.chapter.RadarContentsWriter;
import com.ai.techradar.pdf.chapter.RadarQuadrantChapterWriter;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.QuadrantTO;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Writes a PDF containing all the data for a radar.
 */
public class RadarPdfWriter {

	private static final String[] QUADRANT_COLOURS = new String[] { "#428BCA", "#D9534F", "#5CB85C", "#f0AD4E" };

	private final RadarTO radar;

	private final Map<Integer, QuadrantTO> quadrantByChapterIndex = new HashMap<Integer, QuadrantTO>();

	public RadarPdfWriter(final RadarTO radar) {
		this.radar = radar;
	}

	public void writeTo(final OutputStream outputStream) {

		try {
			// Define chapters
			final List<RadarChapter> radarChapters = defineRadarChapters(radar);

			// Create landscape document
			final Document document = new Document(PageSize.A4.rotate());
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			final PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			pdfWriter.setPageEvent(new ChapterWrittenEvent(radarChapters));

			// Write the PDF document
			document.open();
			addMetadata(radar, document);
			addContent(radarChapters, radar, document, pdfWriter);

			// Add the contents page now that the page numbers have been set (to the end of the document)
			addContentsPage(document, pdfWriter, radarChapters);

			byteArrayOutputStream.flush();
			document.close();

			// The contents page must now be moved to the beginning of the document
			moveContentsPageToStartOfDocument(byteArrayOutputStream, outputStream);

		}
		catch (final Exception e) {
			throw new IllegalStateException("Failed to write radar with ID " + radar.getId() + " to PDF", e);
		}
	}

	private List<RadarChapter> defineRadarChapters(final RadarTO radar) {
		final List<RadarChapter> radarChapters = new ArrayList<RadarChapter>();

		int chapterNumber = 1;

		// Add chapter for each quadrant
		final List<QuadrantTO> quadrants = radar.getQuadrants();
		for (int i = 0; i < quadrants.size(); i++) {
			final QuadrantTO quadrant = quadrants.get(i);

			// For the time being choose the quadrant colour based on the order the quadrants appear in the list
			final Color quadrantColour = Color.decode(QUADRANT_COLOURS[i]);

			final RadarChapter radarChapter = new RadarChapter(chapterNumber, quadrant.getName(), quadrantColour);
			radarChapters.add(radarChapter);
			quadrantByChapterIndex.put(radarChapter.getIndex(), quadrant);

			chapterNumber++;
		}

		return radarChapters;
	}

	private void addMetadata(final RadarTO radar, final Document document) {
		document.addTitle(radar.getName());
		document.addSubject("PDF export of radar " + radar.getName());
		document.addAuthor(toEmptyStringIfNull(radar.getCreatedBy()));
	}

	private void addContent(final List<RadarChapter> radarChapters, final RadarTO radar, final Document document, final PdfWriter pdfWriter)
			throws DocumentException {
		for (final RadarChapter radarChapter : radarChapters) {
			final QuadrantTO quadrant = quadrantByChapterIndex.get(radarChapter.getIndex());

			final RadarQuadrantChapterWriter radarQuadrantChapterWriter = new RadarQuadrantChapterWriter(radarChapter, quadrant,
					radar.getMaturities(), radar.getTechnologies());
			radarQuadrantChapterWriter.writeTo(document, pdfWriter);
		}
	}

	private void addContentsPage(final Document document, final PdfWriter pdfWriter, final List<RadarChapter> contents) throws IOException,
			DocumentException {
		final RadarContentsWriter radarContentsChapterWriter = new RadarContentsWriter(contents);
		radarContentsChapterWriter.writeTo(document, pdfWriter);
	}

	private void moveContentsPageToStartOfDocument(final ByteArrayOutputStream pdfWriterOutputStream, final OutputStream outputStream)
			throws IOException, DocumentException {
		final PdfReader pdfReader = new PdfReader(pdfWriterOutputStream.toByteArray());
		final int numberOfPages = pdfReader.getNumberOfPages();

		// Put the last page at the beginning of the document
		final List<Integer> pageNumbers = new ArrayList<Integer>();
		pageNumbers.add(numberOfPages);
		for (int i = 1; i < numberOfPages; i++) {
			pageNumbers.add(i);
		}
		pdfReader.selectPages(pageNumbers);
		final PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);

		pdfStamper.close();
		pdfWriterOutputStream.close();
	}

	private static String toEmptyStringIfNull(final String value) {
		return (value == null) ? "" : value;
	}
}
