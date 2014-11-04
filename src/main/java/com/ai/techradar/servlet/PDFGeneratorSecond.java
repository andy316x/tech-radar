package com.ai.techradar.servlet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import javax.servlet.http.HttpServletResponse;

import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.servlet.RadarPreviewServlet.Quadrant;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PDFGeneratorSecond extends PdfPageEventHelper {

	// Will need to be changed if more than 1 chapter per page
	private HashMap<Integer, String> contentsPage = new HashMap<Integer, String>();
	private final Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
	private final Font subheaderFont = new Font(Font.HELVETICA, 11, Font.BOLD);
	private final Font textFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
	private RadarService service = new RadarServiceImpl();

	private final static float optimumWidth = 650; // 750;
	private final static float width = 650;
	private final static float scaleFactor = width / optimumWidth;
	private final int height = 650; // 750;

	private static float a1 = scaleFactor * 210;// 240; 210
	private static float a2 = scaleFactor * 210;// 240;
	private static float a3 = scaleFactor * 115;// 135; 115
	private static float a4 = scaleFactor * 115;// 135;

	private static final float[] ARC_WIDTHS = new float[] { a1, a2, a3, a4 };
	// must add up to int width

	private static final String[] ARC_COLOURS = new String[] { "#BFC0BF", "#CBCCCB", "#D7D8D6",
	"#E4E5E4" };

	private final Map<String, List<TechnologyTO>> map = new LinkedHashMap<String, List<TechnologyTO>>();

	private final RadarTO r = service.getRadarById(new Long(1));

	ByteArrayOutputStream bOut = new ByteArrayOutputStream();

	// TODO replace with loop for all chapters / sections
	// TODO add a OnSection handler to add sections to contents

	PdfWriter writer;

	private static final String[] QUADRANT_COLOURS = new String[] { "#3DB5BE", "#83AD78",
		"#E88744", "#8D2145" };
	int colourCount = 0;

	public void makeImageRadar(Document document) {

		try {

			final float w = scaleFactor * optimumWidth;
			final float h = height;

			final RadarTO radar = service.getRadarById(new Long(1));

			final Map<String, Arc> arcMap = new LinkedHashMap<String, Arc>();
			for (final TechnologyTO t : radar.getTechnologies()) {
				Arc arc = arcMap.get(t.getArcName());
				if (arc == null) {
					arc = new Arc((int) ARC_WIDTHS[arcMap.size()], t.getArcName(),
							ARC_COLOURS[arcMap.size()]);
					arcMap.put(t.getArcName(), arc);
				}
			}

			float totalArc = 0;
			for (final Arc arc : arcMap.values()) {
				totalArc = totalArc + arc.getRadius();
			}

			float cumulativeArc = 0;
			int count = 0;
			for (final Arc arc : arcMap.values()) {
				final float segmentWidth = ((float) arc.getRadius() / totalArc) * ((float) w / 2);

				arc.setInnerRadius((int) cumulativeArc);
				arc.setOuterRadius((int) (cumulativeArc + segmentWidth));
				if (count < 2) {
					arc.setRails(2);
				} else {
					arc.setRails(2);
				}
				arc.setIndex(count);
				cumulativeArc = cumulativeArc + segmentWidth;
				count++;
			}

			final Map<String, Quadrant> quadrantMap = new HashMap<String, Quadrant>();
			for (final TechnologyTO item : radar.getTechnologies()) {
				Quadrant techQuadrant = quadrantMap.get(item.getQuadrantName());
				if (techQuadrant == null) {
					techQuadrant = new Quadrant(item.getQuadrantName(),
							QUADRANT_COLOURS[quadrantMap.size()]);
					techQuadrant.setStartTheta(quadrantMap.size() * 90);
					quadrantMap.put(item.getQuadrantName(), techQuadrant);
				}
				techQuadrant.getItems().add(item);
			}

			// add technologies to quad

			for (final TechnologyTO tech : r.getTechnologies()) {
				List<TechnologyTO> quad = map.get(tech.getQuadrantName());
				if (quad == null) {
					quad = new ArrayList<TechnologyTO>();
					map.put(tech.getQuadrantName(), quad);
				}
				quad.add(tech);
			}

			int chapNumber = 1;
			int techIndex = 0;
			int techIndexRadar = 0;
			for (final Quadrant quadrant : quadrantMap.values()) {

				// write techs

				headerFont.setColor(Color.decode(quadrant.getColour()));
				String chapTitle = quadrant.getName().toUpperCase();
				Chunk chunk = new Chunk(chapTitle, headerFont);
				chunk.setLocalDestination(chapTitle);
				Chapter chapter = new Chapter(new Paragraph(chunk), chapNumber);
				chapNumber++;

				document.add(chapter);

				// draw quadrant
				PdfContentByte cb = writer.getDirectContent();
				PdfTemplate tp = cb.createTemplate(optimumWidth, height);
				Graphics2D g2d = tp.createGraphics(optimumWidth, height, new DefaultFontMapper());

				final int r1 = (int) a1;
				final int r2 = (int) r1 + (int) a2; // cumulative of arc widths
				final int r3 = (int) r2 + (int) a3;
				final int r4 = (int) r3 + (int) a4;

				g2d.setColor(Color.decode("#E4E5E4"));
				g2d.fillArc(r4 / 2 - r4 / 2, r4 / 2 - r4 / 2, r4, r4, 90, 90);

				g2d.setColor(Color.decode("#D7D8D6"));
				g2d.fillArc(r4 / 2 - r3 / 2, r4 / 2 - r3 / 2, r3, r3, 90, 90);

				g2d.setColor(Color.decode("#CBCCCB"));
				g2d.fillArc(r4 / 2 - r2 / 2, r4 / 2 - r2 / 2, r2, r2, 90, 90);

				g2d.setColor(Color.decode("#BFC0BF"));
				g2d.fillArc(r4 / 2 - r1 / 2, r4 / 2 - r1 / 2, r1, r1, 90, 90);
				g2d.setFont(new java.awt.Font("Verdana", Font.NORMAL, 8));
				g2d.setColor(Color.decode(quadrant.getColour()));

				int offset = (int) (scaleFactor * 38);

				g2d.drawString("PHASE OUT", scaleFactor * 330, offset);
				g2d.drawString("MAINTAIN", scaleFactor * 330, offset + (scaleFactor * 60));
				g2d.drawString("INVEST", scaleFactor * 330, offset + (scaleFactor * 125));
				g2d.drawString("WATCH", scaleFactor * 330, offset + (scaleFactor * 240));

				final int axisWidth = 8;
				g2d.setPaint(new Color(1, 1, 1, 0.5f));
				g2d.fillRect(r4 / 2 - axisWidth / 2, 0, axisWidth, r4);
				g2d.fillRect(0, r4 / 2 - axisWidth / 2, r4, axisWidth);

				g2d.setColor(Color.decode(quadrant.getColour()));

				final Stack<Stack<Stack<TechnologyTO>>> arcRails = new Stack<Stack<Stack<TechnologyTO>>>();
				for (final Arc arc : arcMap.values()) {
					final Stack<Stack<TechnologyTO>> rails = new Stack<Stack<TechnologyTO>>();
					for (int k = 0; k < arc.getRails(); k++) {
						rails.push(new Stack<TechnologyTO>());
					}
					arcRails.push(rails);
				}

				count = 0;
				for (final TechnologyTO technology : quadrant.getItems()) {
					final Arc arc = arcMap.get(technology.getArcName());
					arcRails.get(arc.getIndex()).get((int) Math.floor(count % arc.getRails()))
					.push(technology);
					count++;
				}

				// separate into sub headings
				List<TechnologyTO> techList = quadrant.getItems();

				HashMap<String, Paragraph> mapArcNameToPDFParagraph = new HashMap<String, Paragraph>();
				ArrayList<String> arcNames = new ArrayList<String>();

				techIndexRadar = techIndex + 1;
				for (TechnologyTO tech : techList) {
					if (mapArcNameToPDFParagraph.containsKey(tech.getArcName())) {
						Paragraph currentParagraph = mapArcNameToPDFParagraph.get(tech.getArcName());
						currentParagraph.setFont(textFont);
						currentParagraph.add(++techIndex + ". " + tech.getTechnologyName() + "\n");
					} else {
						Paragraph techParagraph = new Paragraph();
						techParagraph.setFont(textFont);
						mapArcNameToPDFParagraph.put(tech.getArcName(), techParagraph);
						techParagraph.add(++techIndex + ". " + tech.getTechnologyName() + "\n");
						arcNames.add(tech.getArcName());
					}
				}

				count = 0;
				for (int j = 0; j < arcRails.size(); j++) {
					final Stack<Stack<TechnologyTO>> rails = arcRails.get(j);
					for (int k = 0; k < rails.size(); k++) {
						final Stack<TechnologyTO> techs = rails.get(k);
						for (int l = 0; l < techs.size(); l++) {
							final TechnologyTO item = techs.get(l);

							// what does this do?

							// Quadrant techQuadrant =
							// quadrantMap.get(item.getQuadrantName());
							// if (techQuadrant == null) {
							// techQuadrant = new
							// Quadrant(item.getQuadrantName(),
							// QUADRANT_COLOURS[quadrantMap.size()]);
							// techQuadrant.setStartTheta(quadrantMap.size() *
							// 90);
							// quadrantMap.put(item.getQuadrantName(),
							// techQuadrant);
							// }

							final Arc arc = arcMap.get(item.getArcName());
							final float r = (((arc.getOuterRadius() - arc.getInnerRadius()) / ((rails
									.size()) + 1)) * (k + 1)) + arc.getInnerRadius();

							final float segmentWidth = 90 / (techs.size() + 1);
							final double x = (w / 2) + r
									* Math.cos(rad(segmentWidth * (l + 1) + 180));
							final double y = (h / 2) + r
									* Math.sin(rad(segmentWidth * (l + 1) + 180));

							// blip draw

							int blipSize = (int) scaleFactor * 14;
							int oneCorrection = (int) (scaleFactor * 2);
							double tempWhiteOutline = scaleFactor * 4;

							int whiteOutline = (int) Math.ceil(tempWhiteOutline);
							float fontSizeUpto99 = scaleFactor * 8;

							float fontSizeAbove99 = scaleFactor * 6;
							int lowNumberPositionCorrection = (int) scaleFactor * 2;
							int highNumberPositionCorrection = (int) scaleFactor * 1;

				

							g2d.setColor(Color.WHITE);
							g2d.fillArc((int) x - ((blipSize + whiteOutline) / 2) , (int) y - 2,
									blipSize + whiteOutline, blipSize + whiteOutline, 0, 360);
							g2d.setColor(Color.decode(quadrant.getColour()));
							g2d.fillArc((int) x - (blipSize / 2) , (int) y, blipSize, blipSize, 0,
									360);


							g2d.setColor(Color.WHITE);

							g2d.setFont(new java.awt.Font("Verdana", Font.NORMAL,
									(int) fontSizeUpto99));
							if (techIndexRadar < 100 && techIndexRadar > 9) { // 2
								// digits
								g2d.drawString("" + techIndexRadar, (int) x - (scaleFactor * 7)
										+ lowNumberPositionCorrection , (int) y + (scaleFactor * 11));
							} else if (techIndexRadar < 10) { // 1 digit
								g2d.drawString("" + techIndexRadar, (int) x - (scaleFactor * 7)
										+ lowNumberPositionCorrection + oneCorrection , (int) y
										+ (scaleFactor * 11));
							} else if (techIndexRadar > 99) { // 3 digits
								g2d.setFont(new java.awt.Font("Verdana", Font.NORMAL,
										(int) fontSizeAbove99));
								g2d.drawString("" + techIndexRadar, (int) x - (scaleFactor * 7)
										+ highNumberPositionCorrection, (int) y
										+ (scaleFactor * 11));
							}
							g2d.setColor(Color.decode(quadrant.getColour()));
							techIndexRadar++;
						}
					}
				}

				// finalise
				g2d.dispose();

				final Image img = new ImgTemplate(tp);
				img.setAbsolutePosition(scaleFactor * 170, scaleFactor * 110);
				document.add(img);



				for (String arcName : arcNames) {
					//for (String arcName : orderedArcNames) {
					System.out.println("ARC NAME: " + arcName);
					subheaderFont.setColor(Color.decode(quadrant.getColour()));
					Paragraph subHeading = new Paragraph(arcName.toUpperCase(), subheaderFont);
					document.add(subHeading);
					document.add(mapArcNameToPDFParagraph.get(arcName));
				}

			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static double rad(final float deg) {
		return deg * Math.PI / 180;
	};

	public void makeContentsPage(Document document) {

		Font spacesFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
		spacesFont.setColor(Color.WHITE);

		headerFont.setColor(Color.black);
		textFont.setColor(Color.BLACK);

		Font contentsHeaderFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

		String title3 = "CONTENTS";
		Anchor anchor3 = new Anchor(title3, contentsHeaderFont);
		anchor3.setName(title3);
		Chapter chapter3 = new Chapter(new Paragraph(anchor3), 0);

		for (Integer pgNum : contentsPage.keySet()) {
			String chapterTitle = contentsPage.get(pgNum);
			int openBracket = chapterTitle.indexOf("[");
			int endChapterNumber = chapterTitle.indexOf(".");
			String chapterNumber = chapterTitle.substring(openBracket + 1, endChapterNumber);
			int comma = chapterTitle.indexOf(",");
			int closeBracket = chapterTitle.indexOf("]");
			chapterTitle = chapterTitle.substring(comma + 1, closeBracket);

			Chunk numberTitleChunk = new Chunk(chapterNumber + "      " + chapterTitle, textFont);
			Chunk pageNumChunk = new Chunk("      " + pgNum + "\n", textFont);
			numberTitleChunk.setLocalGoto(chapterTitle.trim());
			chapter3.add(numberTitleChunk);
			chapter3.add(pageNumChunk);

		}
		try {
			document.add(chapter3);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void MakePDF(HttpServletResponse response) {
		try {

			// arcs

			final Map<String, Arc> arcMap = new LinkedHashMap<String, Arc>();
			for (final TechnologyTO t : r.getTechnologies()) {
				Arc arc = arcMap.get(t.getArcName());
				if (arc == null) {
					arc = new Arc((int) ARC_WIDTHS[arcMap.size()], t.getArcName(),
							ARC_COLOURS[arcMap.size()]);
					arcMap.put(t.getArcName(), arc);
				}

			}

			Document document = new Document(PageSize.A4);
			bOut = new ByteArrayOutputStream();

			// PdfWriter
			writer = PdfWriter.getInstance(document, bOut);
			// Add a page event handler (OnSection and OnChapter)
			writer.setPageEvent(this);

			document.open();

			makeImageRadar(document);
			makeContentsPage(document);

			// Close and flush
			document.close();
			bOut.flush();

			// Use the in-memory byte stream output stream to create a PDF
			// reader
			// Get the number of pages and make a list so that the last page is
			// now the first page
			// Use a PdfStamper to then write this PDF but this time to the
			// response output stream
			PdfReader reader = new PdfReader(bOut.toByteArray());
			int n = reader.getNumberOfPages();

			List<Integer> pages = new ArrayList<Integer>();
			pages.add(n);
			for (int i = 1; i < n; i++) {
				pages.add(i);
			}
			reader.selectPages(pages);
			PdfStamper stamper = new PdfStamper(reader, response.getOutputStream());

			stamper.close();
			bOut.close();
		} catch (Exception e) {
			System.out.printf("ERROR: ", e.getMessage());
		}
	}

	@Override
	public void onChapter(final PdfWriter writer, final Document document,
			final float paragraphPosition, final Paragraph title) {
		contentsPage.put(writer.getPageNumber(), title.toString());
	}

	private static class Arc {
		private final int radius;
		private final String name;
		private final String colour;
		private int innerRadius;
		private int outerRadius;
		private int rails;
		private int index;

		public Arc(int radius, String name, String colour) {
			this.radius = radius;
			this.name = name;
			this.colour = colour;
		}

		public int getRadius() {
			return radius;
		}

		public String getName() {
			return name;
		}

		public String getColour() {
			return colour;
		}

		public int getInnerRadius() {
			return innerRadius;
		}

		public void setInnerRadius(int innerRadius) {
			this.innerRadius = innerRadius;
		}

		public int getOuterRadius() {
			return outerRadius;
		}

		public void setOuterRadius(int outerRadius) {
			this.outerRadius = outerRadius;
		}

		public int getRails() {
			return rails;
		}

		public void setRails(int rails) {
			this.rails = rails;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	};
}
