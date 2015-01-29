package com.ai.techradar.servlet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletResponse;

import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.servlet.RadarPreviewServlet.Quadrant;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.ai.techradar.web.service.to.TechGroupingTO;
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

@SuppressWarnings("unused")
public class PDFGeneratorSecond extends PdfPageEventHelper {

	// Will need to be changed if more than 1 chapter per page
	private HashMap<Integer, String> contentsPage = new HashMap<Integer, String>();
	private final Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
	private final Font subheaderFont = new Font(Font.HELVETICA, 11, Font.BOLD);
	private final Font textFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
	private final RadarService service = (RadarService)SpringStarter.getContext().getBean("RadarService");

	private final static float optimumWidth = 650;
	private final static float width = 650;
	private final static float scaleFactor = width / optimumWidth;
	private final int height = 650;

	private static float a1 = scaleFactor * 150;
	private static float a2 = scaleFactor * 125;
	private static float a3 = scaleFactor * 75;
	private static float a4 = scaleFactor * 50;
	private static float a5 = scaleFactor * 50;

	private static final float[] ARC_WIDTHS = new float[] { a1, a2, a3, a4, a5 };

	private static final Color[] ARC_COLOURS = new Color[] { new Color(223,223,223), new Color(166,167,169), new Color(190,191,193), new Color(209,209,209), new Color(223,223,223) };

	private final Map<String, List<RadarTechnologyTO>> map = new LinkedHashMap<String, List<RadarTechnologyTO>>();

	ByteArrayOutputStream bOut = new ByteArrayOutputStream();

	// TODO replace with loop for all chapters / sections
	// TODO add a OnSection handler to add sections to contents

	PdfWriter writer;

	private static final String[] QUADRANT_COLOURS = new String[] { "#428BCA", "#D9534F", "#5CB85C", "#f0AD4E" };
	int colourCount = 0;

	public void makeImageRadar(final Document document, final RadarTO radar) {

		try {

			final float w = scaleFactor * optimumWidth;
			final float h = height;

			final Map<String, Arc> arcMap = new LinkedHashMap<String, Arc>();
			for (final MaturityTO maturity : radar.getMaturities()) {
				final Arc arc = new Arc((int) ARC_WIDTHS[arcMap.size()], maturity.getName(),
						ARC_COLOURS[arcMap.size()]);
				arcMap.put(maturity.getName(), arc);
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
			for (final TechGroupingTO techGrouping : radar.getTechGroupings()) {
				final Quadrant quadrant = new Quadrant(techGrouping.getName(),
						QUADRANT_COLOURS[quadrantMap.size()]);
				quadrant.setStartTheta(quadrantMap.size() * 90);
				quadrantMap.put(techGrouping.getName(), quadrant);
			}

			// add technologies to quad

			for (final RadarTechnologyTO tech : radar.getTechnologies()) {
				String techGroupingName = tech.getTechGrouping();
				List<RadarTechnologyTO> techGrouping = map.get(techGroupingName);
				if (techGrouping == null) {
					techGrouping = new ArrayList<RadarTechnologyTO>();
					map.put(techGroupingName, techGrouping);
				}
				techGrouping.add(tech);
				quadrantMap.get(tech.getTechGrouping()).getItems().add(tech);
			}

			int chapNumber = 1;
			int techIndex = 0;
			int techIndexRadar = 0;
			for (final Quadrant quadrant : quadrantMap.values()) {

				// write techs

				headerFont.setColor(Color.decode(quadrant.getColour()));
				final String chapTitle = quadrant.getName().toUpperCase();
				final Chunk chunk = new Chunk(chapTitle, headerFont);
				chunk.setLocalDestination(chapTitle);
				final Chapter chapter = new Chapter(new Paragraph(chunk), chapNumber);
				chapNumber++;

				document.add(chapter);

				// draw quadrant
				final PdfContentByte cb = writer.getDirectContent();
				final PdfTemplate tp = cb.createTemplate(optimumWidth, height);
				final Graphics2D g2d = tp.createGraphics(optimumWidth, height, new DefaultFontMapper());

				// Loop around the arcs in reverse and draw them
				final List<Arc> arcs = new ArrayList<Arc>();
				for(final Arc arc : arcMap.values()) {
					arcs.add(arc);
				}
				final Arc outerArc = arcs.get(arcs.size()-1);
				for(int i = arcs.size(); i > 0; i--) {
					final Arc arc = arcs.get(i-1);
					g2d.setColor(arc.getColour());
					g2d.fillArc(outerArc.getOuterRadius() - arc.getOuterRadius(), outerArc.getOuterRadius() - arc.getOuterRadius(), arc.getOuterRadius()*2, arc.getOuterRadius()*2, 90, 90);
				}


				g2d.setFont(new java.awt.Font("Verdana", Font.NORMAL, 8));
				g2d.setColor(Color.decode(quadrant.getColour()));

				int cumaltive = 0;
				for(int i = arcs.size(); i > 0; i--) {
					final Arc arc = arcs.get(i-1);
					
					final int next = arc.getOuterRadius() - arc.getInnerRadius();
					final int half = next / 2;
					
					g2d.drawString(arc.getName().toUpperCase(), scaleFactor * 330, cumaltive + half);
					
					cumaltive += next;
				}

				final int axisWidth = 8;
				g2d.setPaint(new Color(1, 1, 1, 0.5f));
				g2d.fillRect(outerArc.getOuterRadius() - axisWidth / 2, 0, axisWidth, outerArc.getOuterRadius() * 2);
				g2d.fillRect(0, outerArc.getOuterRadius() - axisWidth / 2, outerArc.getOuterRadius() * 2, axisWidth);

				g2d.setColor(Color.decode(quadrant.getColour()));

				final Stack<Stack<Stack<RadarTechnologyTO>>> arcRails = new Stack<Stack<Stack<RadarTechnologyTO>>>();
				for (final Arc arc : arcMap.values()) {
					final Stack<Stack<RadarTechnologyTO>> rails = new Stack<Stack<RadarTechnologyTO>>();
					for (int k = 0; k < arc.getRails(); k++) {
						rails.push(new Stack<RadarTechnologyTO>());
					}
					arcRails.push(rails);
				}

				count = 0;
				for (final RadarTechnologyTO technology : quadrant.getItems()) {
					final Arc arc = arcMap.get(technology.getMaturity());
					arcRails.get(arc.getIndex()).get((int) Math.floor(count % arc.getRails()))
					.push(technology);
					count++;
				}

				// separate into sub headings
				final List<RadarTechnologyTO> techList = quadrant.getItems();

				final Map<String, Paragraph> mapArcNameToPDFParagraph = new HashMap<String, Paragraph>();
				final List<String> arcNames = new ArrayList<String>();

				techIndexRadar = techIndex + 1;
				for (final RadarTechnologyTO tech : techList) {
					final String arcName = tech.getMaturity();
					if (mapArcNameToPDFParagraph.containsKey(arcName)) {
						final Paragraph currentParagraph = mapArcNameToPDFParagraph.get(arcName);
						currentParagraph.setFont(textFont);
						currentParagraph.add(++techIndex + ". " + tech.getTechnology() + "\n");
					} else {
						final Paragraph techParagraph = new Paragraph();
						techParagraph.setFont(textFont);
						mapArcNameToPDFParagraph.put(arcName, techParagraph);
						techParagraph.add(++techIndex + ". " + tech.getTechnology() + "\n");
						arcNames.add(arcName);
					}
				}

				count = 0;
				for (int j = 0; j < arcRails.size(); j++) {
					final Stack<Stack<RadarTechnologyTO>> rails = arcRails.get(j);
					for (int k = 0; k < rails.size(); k++) {
						final Stack<RadarTechnologyTO> techs = rails.get(k);
						for (int l = 0; l < techs.size(); l++) {
							final RadarTechnologyTO item = techs.get(l);

							final Arc arc = arcMap.get(item.getMaturity());
							final float r = (((arc.getOuterRadius() - arc.getInnerRadius()) / ((rails.size()) + 1)) * (k + 1)) + arc.getInnerRadius();

							final float segmentWidth = 90 / (techs.size() + 1);
							final double x = (w / 2) + r * Math.cos(rad(segmentWidth * (l + 1) + 180));
							final double y = (h / 2) + r * Math.sin(rad(segmentWidth * (l + 1) + 180));

							// blip draw

							final int blipSize = (int) scaleFactor * 14;
							final int oneCorrection = (int) (scaleFactor * 2);
							final double tempWhiteOutline = scaleFactor * 4;

							final int whiteOutline = (int) Math.ceil(tempWhiteOutline);
							final float fontSizeUpto99 = scaleFactor * 8;

							final float fontSizeAbove99 = scaleFactor * 6;
							final int lowNumberPositionCorrection = (int) scaleFactor * 2;
							final int highNumberPositionCorrection = (int) scaleFactor * 1;



							g2d.setColor(Color.decode(quadrant.getColour()));
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



				for (final String arcName : arcNames) {
					subheaderFont.setColor(Color.decode(quadrant.getColour()));
					final Paragraph subHeading = new Paragraph(arcName.toUpperCase(), subheaderFont);
					document.add(subHeading);
					document.add(mapArcNameToPDFParagraph.get(arcName));
				}

			}
		} catch (final DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static double rad(final float deg) {
		return deg * Math.PI / 180;
	};

	public void makeContentsPage(Document document) {

		final Font spacesFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
		spacesFont.setColor(Color.WHITE);

		headerFont.setColor(Color.black);
		textFont.setColor(Color.BLACK);

		final Font contentsHeaderFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

		final String title3 = "CONTENTS";
		final Anchor anchor3 = new Anchor(title3, contentsHeaderFont);
		anchor3.setName(title3);
		final Chapter chapter3 = new Chapter(new Paragraph(anchor3), 0);

		for (final Integer pgNum : contentsPage.keySet()) {
			String chapterTitle = contentsPage.get(pgNum);
			final int openBracket = chapterTitle.indexOf("[");
			final int endChapterNumber = chapterTitle.indexOf(".");
			final String chapterNumber = chapterTitle.substring(openBracket + 1, endChapterNumber);
			final int comma = chapterTitle.indexOf(",");
			final int closeBracket = chapterTitle.indexOf("]");
			chapterTitle = chapterTitle.substring(comma + 1, closeBracket);

			final Chunk numberTitleChunk = new Chunk(chapterNumber + "      " + chapterTitle, textFont);
			final Chunk pageNumChunk = new Chunk("      " + pgNum + "\n", textFont);
			numberTitleChunk.setLocalGoto(chapterTitle.trim());
			chapter3.add(numberTitleChunk);
			chapter3.add(pageNumChunk);

		}
		try {
			document.add(chapter3);
		} catch (final DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void makePDF(final HttpServletResponse response, final Long id) {
		try {

			final RadarTO radar = service.getRadarById(id);

			// arcs

			final Map<String, Arc> arcMap = new LinkedHashMap<String, Arc>();
			for (final MaturityTO maturity : radar.getMaturities()) {
				Arc arc = arcMap.get(maturity.getName());
				if (arc == null) {
					arc = new Arc((int) ARC_WIDTHS[arcMap.size()], maturity.getName(),
							ARC_COLOURS[arcMap.size()]);
					arcMap.put(maturity.getName(), arc);
				}

			}

			final Document document = new Document(PageSize.A4);
			bOut = new ByteArrayOutputStream();

			// PdfWriter
			writer = PdfWriter.getInstance(document, bOut);
			// Add a page event handler (OnSection and OnChapter)
			writer.setPageEvent(this);

			document.open();

			makeImageRadar(document, radar);
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
			final PdfReader reader = new PdfReader(bOut.toByteArray());
			final int n = reader.getNumberOfPages();

			final List<Integer> pages = new ArrayList<Integer>();
			pages.add(n);
			for (int i = 1; i < n; i++) {
				pages.add(i);
			}
			reader.selectPages(pages);
			final PdfStamper stamper = new PdfStamper(reader, response.getOutputStream());

			stamper.close();
			bOut.close();
		} catch (final Exception e) {
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
		private final Color colour;
		private int innerRadius;
		private int outerRadius;
		private int rails;
		private int index;

		public Arc(final int radius, final String name, final Color colour) {
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

		public Color getColour() {
			return colour;
		}

		public int getInnerRadius() {
			return innerRadius;
		}

		public void setInnerRadius(final int innerRadius) {
			this.innerRadius = innerRadius;
		}

		public int getOuterRadius() {
			return outerRadius;
		}

		public void setOuterRadius(final int outerRadius) {
			this.outerRadius = outerRadius;
		}

		public int getRails() {
			return rails;
		}

		public void setRails(final int rails) {
			this.rails = rails;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(final int index) {
			this.index = index;
		}
	};
}
