package com.ai.techradar.pdf.chapter;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Writes a chapter for a radar PDF document.
 */
public abstract class RadarChapterWriter {

	private static final int HEADER_FONT_FAMILY = Font.HELVETICA;
	private static final int HEADER_FONT_SIZE = 14;
	private static final int HEADER_FONT_STYLE = Font.BOLD;

	protected final RadarChapter radarChapter;
	protected final Chapter chapter;

	public RadarChapterWriter(final RadarChapter radarChapter) {
		this.radarChapter = radarChapter;
		chapter = new Chapter(radarChapter.getIndex());
	}

	public abstract void writeTo(final Document document, final PdfWriter pdfWriter) throws DocumentException;

	protected void addHeading(final Document document) throws DocumentException {
		final String chapterTitle = radarChapter.getHeading().toUpperCase();
		final Font headerFont = new Font(HEADER_FONT_FAMILY, HEADER_FONT_SIZE, HEADER_FONT_STYLE, radarChapter.getHighlightColour());

		final Chunk chunk = new Chunk(chapterTitle, headerFont);
		chunk.setLocalDestination(chapterTitle);

		final Paragraph paragraph = new Paragraph(chunk);
		chapter.setTitle(paragraph);

		radarChapter.setPdfTitle(paragraph.toString());

		document.add(chapter);
	}

}
