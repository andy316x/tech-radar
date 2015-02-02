package com.ai.techradar.pdf.chapter;

import java.awt.Color;
import java.util.List;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Writes a chapter containing the contents page for a radar PDF document.
 */
public class RadarContentsWriter {

	private static final Font CONTENTS_HEADER_FONT = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
	private static final Font CONTENTS_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
	private static final String DELIMITER = "      ";

	private static final String CONTENTS_HEADER_TITLE = "CONTENTS";

	private final List<RadarChapter> contents;

	public RadarContentsWriter(final List<RadarChapter> contents) {
		this.contents = contents;
	}

	public void writeTo(final Document document, final PdfWriter pdfWriter) throws DocumentException {
		addHeading(document);
		addContents(document);
	}

	private void addHeading(final Document document) throws DocumentException {
		final Chunk chunk = new Chunk(CONTENTS_HEADER_TITLE, CONTENTS_HEADER_FONT);
		chunk.setLocalDestination(CONTENTS_HEADER_TITLE);

		final Paragraph paragraph = new Paragraph(chunk);

		document.add(paragraph);
	}

	private void addContents(final Document document) throws DocumentException {
		for (final RadarChapter radarChapter : contents) {
			final Chunk chapterTitle = new Chunk(radarChapter.getIndex() + DELIMITER + radarChapter.getHeading(), CONTENTS_FONT);
			final Chunk pageNumber = new Chunk(DELIMITER + radarChapter.getPdfPageNumber() + "\n", CONTENTS_FONT);
			chapterTitle.setLocalGoto(radarChapter.getHeading());

			document.add(chapterTitle);
			document.add(pageNumber);
		}
	}
}
