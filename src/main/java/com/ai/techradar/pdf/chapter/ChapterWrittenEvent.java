package com.ai.techradar.pdf.chapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * onChapter() will be called when a chapter is written to the PDF document.
 */
public class ChapterWrittenEvent extends PdfPageEventHelper {

	private final Map<Integer, RadarChapter> chaptersByIndex;

	public ChapterWrittenEvent(final List<RadarChapter> chapterList) {
		chaptersByIndex = new HashMap<Integer, RadarChapter>();

		for (final RadarChapter chapter : chapterList) {
			chaptersByIndex.put(chapter.getIndex(), chapter);
		}
	}

	@Override
	public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition, final Paragraph title) {
		final RadarChapter chapter = chaptersByIndex.get(getIndexFromTitleParagraph(title));
		chapter.setPdfTitle(title.toString());
		chapter.setPdfPageNumber(writer.getPageNumber());
	}

	private int getIndexFromTitleParagraph(final Paragraph title) {
		final String titleString = title.toString();
		final char[] titleCharArray = titleString.toCharArray();

		Integer startIndex = null;
		Integer endIndex = null;
		for (int i = 0; i < titleCharArray.length; i++) {
			final char character = titleCharArray[i];

			if (startIndex == null && Character.isDigit(character)) {
				// Found start of number characters
				startIndex = i;
			}
			else if (character == '.') {
				// Found end of number characters
				endIndex = i;
				break;
			}
		}

		final String indexString = titleString.substring(startIndex, endIndex);
		return Integer.valueOf(indexString);
	}
}
