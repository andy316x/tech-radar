package com.ai.techradar.pdf.chapter;

import java.awt.Color;
import java.util.List;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Writes a chapter containing the contents page for a radar PDF document.
 */
public class RadarContentsChapterWriter extends RadarChapterWriter {

	private static final Font CONTENTS_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
	private static final String DELIMITER = "      ";

	private final List<RadarChapter> contents;

	public RadarContentsChapterWriter(final RadarChapter radarChapter, final List<RadarChapter> contents) {
		super(radarChapter);
		this.contents = contents;
	}

	@Override
	public void writeTo(final Document document, final PdfWriter pdfWriter) throws DocumentException {
		final Chapter chapter = new Chapter(radarChapter.getIndex());
		addHeading(chapter);
		addContents(chapter);

		document.add(chapter);
	}

	private void addContents(final Chapter chapter) throws DocumentException {
		for (final RadarChapter radarChapter : contents) {
			final Chunk chapterTitle = new Chunk(radarChapter.getIndex() + DELIMITER + radarChapter.getHeading(), CONTENTS_FONT);
			final Chunk pageNumber = new Chunk(DELIMITER + radarChapter.getPdfPageNumber() + "\n", CONTENTS_FONT);
			chapterTitle.setLocalGoto(radarChapter.getHeading());

			chapter.add(chapterTitle);
			chapter.add(pageNumber);
		}
	}
}
