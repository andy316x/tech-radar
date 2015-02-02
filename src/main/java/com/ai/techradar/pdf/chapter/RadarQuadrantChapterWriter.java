package com.ai.techradar.pdf.chapter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.ai.techradar.pdf.Arc;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.ai.techradar.web.service.to.TechGroupingTO;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Writes a chapter containing a radar quadrant for a radar PDF document.
 */
public class RadarQuadrantChapterWriter {

	private static final int HEADER_FONT_FAMILY = com.lowagie.text.Font.HELVETICA;
	private static final int HEADER_FONT_SIZE = 14;
	private static final int HEADER_FONT_STYLE = com.lowagie.text.Font.BOLD;

	private static final Font ARC_LABEL_FONT = new Font("Verdana", Font.PLAIN, 10);
	private static final int ARC_LABEL_HEIGHT = 20;

	private static final int[] ARC_RADIUSES = { 150, 275, 350, 400, 450 };
	private static final Color[] ARC_COLOURS = { new Color(223, 223, 223), new Color(166, 167, 169), new Color(190, 191, 193),
			new Color(209, 209, 209), new Color(223, 223, 223) };

	private static final Font BLIP_LABEL_FONT = new Font("Verdana", Font.PLAIN, 8);
	private static final int BLIP_SIZE = 14;
	private static final int BLIP_BORDER_SIZE = 4;

	private static final com.lowagie.text.Font SUB_HEADING_FONT = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11,
			com.lowagie.text.Font.BOLD);
	private final com.lowagie.text.Font KEY_TEXT_FONT = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8,
			com.lowagie.text.Font.NORMAL);

	private final TreeMap<Integer, Arc> arcsByIndex = new TreeMap<Integer, Arc>();
	private final TreeMap<Integer, RadarTechnologyTO> technologiesByIndex = new TreeMap<Integer, RadarTechnologyTO>();
	private final Map<String, List<Integer>> technologyIndexesByMaturity = new HashMap<String, List<Integer>>();

	private final RadarChapter radarChapter;
	private final Chapter chapter;

	private final TechGroupingTO techGrouping;
	private final List<MaturityTO> maturities;
	private final List<RadarTechnologyTO> technologies;

	public RadarQuadrantChapterWriter(final RadarChapter radarChapter, final TechGroupingTO techGrouping,
			final List<MaturityTO> maturities, final List<RadarTechnologyTO> technologies) {
		this.radarChapter = radarChapter;
		this.techGrouping = techGrouping;
		this.maturities = maturities;
		this.technologies = technologies;

		chapter = new Chapter(radarChapter.getIndex());
	}

	public void writeTo(final Document document, final PdfWriter pdfWriter) throws DocumentException {

		selectTechnologiesForTechGrouping(techGrouping, technologies);
		defineArcs(maturities);

		addHeading(document);
		drawRadarQuadrant(document, pdfWriter);
		addLegend(document);
	}

	private void selectTechnologiesForTechGrouping(final TechGroupingTO techGrouping, final List<RadarTechnologyTO> technologies) {
		int technologyIndex = 1;
		for (final RadarTechnologyTO technology : technologies) {
			if (techGrouping.getName().equals(technology.getTechGrouping())) {
				technologiesByIndex.put(technologyIndex, technology);
				addToListMap(technologyIndexesByMaturity, technology.getMaturity(), technologyIndex);
			}
			technologyIndex++;
		}
	}

	private void defineArcs(final List<MaturityTO> maturities) {

		for (int i = 0; i < ARC_RADIUSES.length; i++) {
			final MaturityTO maturity = maturities.get(i);
			final String maturityName = maturity.getName();
			final int radius = ARC_RADIUSES[i];
			final Color colour = ARC_COLOURS[i];

			final Arc arc = new Arc(maturityName, radius, colour);

			final List<Integer> technologyIndexesForArc = technologyIndexesByMaturity.get(maturityName);
			if (technologyIndexesForArc != null) {
				arc.addAllTechnologyIndexes(technologyIndexesForArc);
			}

			arcsByIndex.put(i, arc);
		}
	}

	private void addHeading(final Document document) throws DocumentException {
		final String chapterTitle = radarChapter.getHeading().toUpperCase();
		final com.lowagie.text.Font headerFont = new com.lowagie.text.Font(HEADER_FONT_FAMILY, HEADER_FONT_SIZE, HEADER_FONT_STYLE,
				radarChapter.getHighlightColour());

		final Chunk chunk = new Chunk(chapterTitle, headerFont);
		chunk.setLocalDestination(chapterTitle);

		final Paragraph paragraph = new Paragraph(chunk);
		chapter.setTitle(paragraph);

		radarChapter.setPdfTitle(paragraph.toString());

		document.add(chapter);
	}

	private void addLegend(final Document document) throws DocumentException {
		final Arc outermostArc = arcsByIndex.lastEntry().getValue();
		final int outermostArcRadius = outermostArc.getRadius();

		final MultiColumnText multiColumnText = new MultiColumnText();
		multiColumnText.addRegularColumns(document.left(), document.right() - outermostArcRadius, 10f, 2);

		for (int i = arcsByIndex.size() - 1; i >= 0; i--) {
			final Arc arc = arcsByIndex.get(i);

			if (arc.getTechnologyIndexes().isEmpty() == false) {
				SUB_HEADING_FONT.setColor(radarChapter.getHighlightColour());
				final Paragraph subHeading = new Paragraph(arc.getLabel().toUpperCase(), SUB_HEADING_FONT);
				multiColumnText.addElement(subHeading);

				for (final Integer technologyIndex : arc.getTechnologyIndexes()) {
					final Paragraph technologyParagraph = new Paragraph();
					technologyParagraph.setFont(KEY_TEXT_FONT);
					final String technologyName = technologiesByIndex.get(technologyIndex).getTechnology();
					technologyParagraph.add(technologyIndex + ". " + technologyName + "\n");
					multiColumnText.addElement(technologyParagraph);
				}
			}
		}
		document.add(multiColumnText);
	}

	private void drawRadarQuadrant(final Document document, final PdfWriter pdfWriter) throws DocumentException {
		final Arc outermostArc = arcsByIndex.lastEntry().getValue();
		final int outermostArcRadius = outermostArc.getRadius();

		// The width and height must be at least the size of the entire square in which the arc is being drawn
		// (even though we are only drawing a quarter of the arc)
		final float imageWidth = outermostArcRadius * 2;
		final float imageHeight = outermostArcRadius * 2;

		final PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
		final PdfTemplate pdfTemplate = pdfContentByte.createTemplate(imageWidth, imageHeight);
		final Graphics2D graphics2d = pdfTemplate.createGraphics(imageWidth, imageHeight, new DefaultFontMapper());

		// Iterate over arcs in reverse order
		for (int i = arcsByIndex.size() - 1; i >= 0; i--) {
			final Arc arc = arcsByIndex.get(i);
			drawRadarArc(arc.getRadius(), outermostArcRadius, arc.getColour(), graphics2d);

			int innerArcRadius = 0;
			if (i != 0) {
				innerArcRadius = arcsByIndex.get(i - 1).getRadius();
			}
			drawArcLabel(arc.getRadius(), innerArcRadius, outermostArcRadius, arc.getLabel(), radarChapter.getHighlightColour(), graphics2d);
		}

		// Iterate over technologies for each arc
		for (final Integer arcIndex : arcsByIndex.keySet()) {
			final Arc arc = arcsByIndex.get(arcIndex);

			final int arcRadius = arc.getRadius();
			int innerArcRadius = 0;
			if (arcIndex != 0) {
				innerArcRadius = arcsByIndex.get(arcIndex - 1).getRadius();
			}

			// Determine how many rails there should be
			final Map<Integer, List<Integer>> technologiesByRailRadius = new HashMap<Integer, List<Integer>>();
			final List<Integer> technologyIndexes = arc.getTechnologyIndexes();
			final int numberTechnologies = technologyIndexes.size();
			if (numberTechnologies > 0) {
				final int maxBlipsPerArc = (arcRadius + innerArcRadius) / BLIP_SIZE / 2;
				final int numberRails = (int) Math.ceil((float) numberTechnologies / maxBlipsPerArc);

				// Sort the technologies by rail
				// TODO this doesn't divide the technologies equally across the rails
				int technologyCount = 0;
				int railIndex = 1;
				int railRadius = ((arcRadius - innerArcRadius) / (numberRails + 1) * railIndex) + innerArcRadius;
				for (final Integer technologyIndex : technologyIndexes) {
					addToListMap(technologiesByRailRadius, railRadius, technologyIndex);

					if (technologyCount > maxBlipsPerArc * railIndex) {
						railIndex++;
						railRadius = ((arcRadius - innerArcRadius) / (numberRails + 1) * railIndex) + innerArcRadius;
					}
					technologyCount++;
				}
			}

			// Add the blips for each rail
			for (final Entry<Integer, List<Integer>> entry : technologiesByRailRadius.entrySet()) {
				final Integer arcRailRadius = entry.getKey();
				final List<Integer> technologiesForRail = entry.getValue();

				int blipIndex = 0;
				for (final Integer technologyIndex : technologiesForRail) {
					drawBlip(technologyIndex, arcRailRadius, blipIndex, technologiesForRail.size(), radarChapter.getHighlightColour(),
							outermostArcRadius, outermostArcRadius - ARC_LABEL_HEIGHT, graphics2d);

					blipIndex++;
				}
			}
		}

		graphics2d.dispose();

		final Image image = new ImgTemplate(pdfTemplate);
		final float imageXPosition = pdfWriter.getPageSize().getWidth() - outermostArcRadius;
		final float imageYPosition = -outermostArcRadius + 100; // TODO hard-coded
		image.setAbsolutePosition(imageXPosition, imageYPosition);
		document.add(image);
	}

	private void drawRadarArc(final int arcRadius, final int outermostArcRadius, final Color colour, final Graphics2D graphics2d) {
		final int x = outermostArcRadius - arcRadius;
		final int y = outermostArcRadius - arcRadius;
		final int width = arcRadius * 2;
		final int height = arcRadius * 2;

		final int startAngle = 90; // TODO quadrant rotation
		final int arcAngle = 90;

		graphics2d.setColor(colour);
		graphics2d.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	private void drawArcLabel(final int arcRadius, final int innerArcRadius, final int outermostArcRadius, final String label,
			final Color colour, final Graphics2D graphics2d) {

		final int arcStartX = outermostArcRadius - arcRadius;
		final int arcEndX = outermostArcRadius - innerArcRadius;
		final int arcCentre = (arcStartX + arcEndX) / 2;

		// TODO Not sure how to centre the label within the arc: x, y is the position of the start of the label
		final int labelLength = label.length();
		final int paddingFactor = 6;
		final int centeringPadding = labelLength / 2 * paddingFactor;

		final int x = arcCentre - centeringPadding;
		final int y = outermostArcRadius + ARC_LABEL_HEIGHT;

		graphics2d.setFont(ARC_LABEL_FONT);
		graphics2d.setColor(colour);
		graphics2d.drawString(label.toUpperCase(), x, y);
	}

	private void drawBlip(final int techologyIndex, final int railRadius, final int blipIndex, final int totalBlipsForRail,
			final Color colour, final int radarWidth, final int radarHeight, final Graphics2D graphics2d) {
		final double quadrantDegrees = 90;
		final double degreesAllowedPerTechnology = quadrantDegrees / totalBlipsForRail;

		// Degrees to shift by for this blip so that they are spaced out = (blipIndex * degreesAllowedPerTechnology)
		// Put the blip in the centre of its allowed space = (degreesAllowedPerTechnology / 2)
		final double thetaDegrees = (blipIndex * degreesAllowedPerTechnology) + (degreesAllowedPerTechnology / 2);
		final double thetaRadians = Math.toRadians(thetaDegrees);

		final double blipCentreXFromRadarCentre = railRadius * Math.cos(thetaRadians);
		final double blipCentreYFromRadarCentre = railRadius * Math.sin(thetaRadians);

		final float centeringPadding = (BLIP_SIZE + BLIP_BORDER_SIZE) / 2;

		final double xFromRadarCentre = blipCentreXFromRadarCentre - centeringPadding;
		final double yFromRadarCentre = blipCentreYFromRadarCentre - centeringPadding;

		// For the PDF writer the origin is at the top left of the diagram whereas all measurements so far are from the centre of the radar
		// therefore minus from the radarWidth
		final int x = (int) (radarWidth - xFromRadarCentre);
		final int y = (int) (radarHeight - yFromRadarCentre);
		final int blipWidth = BLIP_SIZE + BLIP_BORDER_SIZE;
		final int blipHeight = BLIP_SIZE + BLIP_BORDER_SIZE;

		// Draw blip border
		graphics2d.setColor(Color.WHITE);
		graphics2d.fillArc(x - (BLIP_BORDER_SIZE / 2), y - (BLIP_BORDER_SIZE / 2), blipWidth, blipHeight, 0, 360);

		// Draw blip centre
		graphics2d.setColor(colour);
		graphics2d.fillArc(x, y, BLIP_SIZE, BLIP_SIZE, 0, 360);

		// Draw blip label
		graphics2d.setColor(Color.WHITE);
		graphics2d.setFont(BLIP_LABEL_FONT);

		// TODO Same problem as above - the index must be centered inside the blip
		// this is done below by shifting the text by a "centeringPadding" but this will depend on the size and length of the text
		// there should be a better way?
		if (techologyIndex < 10) {
			// 1 digit
			final float horizontalCenteringPadding = centeringPadding - 5;
			graphics2d.drawString(String.valueOf(techologyIndex), x + horizontalCenteringPadding, y + centeringPadding);
		}
		else if (techologyIndex >= 10 && techologyIndex < 100) {
			// 2 digits
			final float horizontalCenteringPadding = centeringPadding - 7;
			graphics2d.drawString(String.valueOf(techologyIndex), x + horizontalCenteringPadding, y + centeringPadding);
		}
		else if (techologyIndex >= 100 && techologyIndex < 1000) {
			// 3 digits
			// TODO doesn't quite fit - could reduce the font, but I think the best thing to do would be to increase the blip size
			final float horizontalCenteringPadding = centeringPadding - 9;
			graphics2d.drawString(String.valueOf(techologyIndex), x + horizontalCenteringPadding, y + centeringPadding);
		}
		else {
			throw new IllegalStateException("Radar PDF writer does not support adding more than 999 technologies to the radar");
		}
	}

	private static <X, Y> void addToListMap(final Map<X, List<Y>> map, final X key, final Y item) {
		List<Y> list = map.get(key);
		if (list == null) {
			list = new ArrayList<Y>();
			map.put(key, list);
		}

		list.add(item);
	}
}
