package com.ai.techradar.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//import java.util.Random;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.ai.techradar.web.service.to.RadarMaturityTO;
import com.ai.techradar.web.service.to.RadarTechGroupingTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;

public class RadarPreviewServlet extends HttpServlet {

	private static final long serialVersionUID = 5976711967719593613L;

	private RadarService service = new RadarServiceImpl();

	private static final int[] ARC_WIDTHS = new int[]{150, 125, 75, 50};

	private static final String[] ARC_COLOURS = new String[]{"#BFC0BF", "#CBCCCB", "#D7D8D6", "#E4E5E4"};

	private static final String[] QUADRANT_COLOURS = new String[]{"#3DB5BE", "#83AD78", "#E88744", "#8D2145"};

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/svg+xml");

		final String widthParam = request.getParameter("w");
		final String radarId = request.getPathInfo().substring(1);
		final Document doc = getSvgDoc(service, radarId, widthParam);

		final Transcoder transcoder = new SVGTranscoder();
		transcoder.addTranscodingHint(PDFTranscoder.KEY_XML_PARSER_VALIDATING, new Boolean(false));
		final TranscoderInput transcoderInput = new TranscoderInput(doc);
		final TranscoderOutput transcoderOutput = new TranscoderOutput(new OutputStreamWriter(response.getOutputStream()));
		try {
			transcoder.transcode(transcoderInput, transcoderOutput);
		} catch (final TranscoderException e) {
			System.out.println("TranscoderException: " + e.getMessage());
		}
	}

	public static Document getSvgDoc(final RadarService service, final String radarId, final String widthParam) {

		final float optimumWidth = 1000;
		final float w = widthParam==null ? optimumWidth : Float.parseFloat(widthParam);
		final float h = w;
		final float scaleFactor = (float)w/optimumWidth;

		final RadarTO radar = buildDataModel(service.getRadarById(Long.parseLong(radarId)));

		final Map<String, Arc> arcMap = new LinkedHashMap<String, Arc>();
		for(final RadarMaturityTO x : radar.getXs()) {
			String arcName = x.getArc().getName();
			Arc arc = arcMap.get(arcName);
			if(arc == null) {
				arc = new Arc(ARC_WIDTHS[arcMap.size()], arcName, ARC_COLOURS[arcMap.size()]);
				arcMap.put(arcName, arc);
			}
		}

		final DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		final Document doc = impl.createDocument(svgNS, "svg", null);

		// Get the root element (the 'svg' element).
		final Element svgRoot = doc.getDocumentElement();

		// Set the width and height attributes on the root 'svg' element.
		svgRoot.setAttributeNS(null, "width", "" + w);
		svgRoot.setAttributeNS(null, "height", "" + h);

		float totalArc = 0;
		for(final Arc arc : arcMap.values()) {
			totalArc = totalArc + arc.getRadius();
		}

		float cumulativeArc = 0;
		int count = 0;
		for(final Arc arc : arcMap.values()) {
			final float segmentWidth = ((float)arc.getRadius() / totalArc)*((float)w/2);

			arc.setInnerRadius((int)cumulativeArc);
			arc.setOuterRadius((int)(cumulativeArc + segmentWidth));
			if(count < 2) {
				arc.setRails(2);
			} else {
				arc.setRails(1);
			}

			arc.setIndex(count);
			cumulativeArc = cumulativeArc + segmentWidth;
			count++;
		}


		for(int i = arcMap.size(); i > 0; i--) {
			final Arc arc = (Arc)arcMap.values().toArray()[i-1];

			drawArc(svgRoot, doc, svgNS, arc.getInnerRadius(),  arc.getOuterRadius(), w/2, h/2, arc.getColour());
			drawArcAxisText(svgRoot, doc, svgNS, scaleFactor, arc.getInnerRadius(),  arc.getOuterRadius(), w, h, arc.getName());
		}

		final float axisWidth = scaleFactor*15;

		final Element rectangle1 = doc.createElementNS(svgNS, "rect");
		rectangle1.setAttribute("x", "" + 0);
		rectangle1.setAttribute("y", "" + ((h/2)-(axisWidth/2)));
		rectangle1.setAttribute("width", "" + w);
		rectangle1.setAttribute("height", "" + axisWidth);
		rectangle1.setAttribute("fill", "white");
		rectangle1.setAttribute("opacity", "0.5");
		rectangle1.setAttribute("x", "" + 0);
		svgRoot.appendChild(rectangle1);

		final Element rectangle2 = doc.createElementNS(svgNS, "rect");
		rectangle2.setAttribute("x", "" + ((w/2)-(axisWidth/2)));
		rectangle2.setAttribute("y", "" + 0);
		rectangle2.setAttribute("width", "" + axisWidth);
		rectangle2.setAttribute("height", "" + h);
		rectangle2.setAttribute("fill", "white");
		rectangle2.setAttribute("opacity", "0.5");
		svgRoot.appendChild(rectangle2);

		final float blipWidth = scaleFactor*30;


		final Map<String, Quadrant> quadrantMap = new HashMap<String, Quadrant>();
		for(final RadarTechnologyTO z : radar.getZs()) {
			String quadrantName = z.getY().getQuadrant().getName();
			Quadrant techQuadrant = quadrantMap.get(quadrantName);
			if(techQuadrant==null) {
				techQuadrant = new Quadrant(quadrantName, QUADRANT_COLOURS[quadrantMap.size()]);
				techQuadrant.setStartTheta(quadrantMap.size()*90);
				quadrantMap.put(quadrantName, techQuadrant);
			}
			techQuadrant.getItems().add(z.getTechnology());
		}

		for(final Quadrant quadrant : quadrantMap.values()) {

			final Stack<Stack<Stack<TechnologyTO>>> arcRails = new Stack<Stack<Stack<TechnologyTO>>>();
			for(final Arc arc : arcMap.values()) {
				final Stack<Stack<TechnologyTO>> rails = new Stack<Stack<TechnologyTO>>();
				for(int k = 0; k < arc.getRails(); k++) {
					rails.push(new Stack<TechnologyTO>());
				}
				arcRails.push(rails);
			}

			count = 0;
			for(final TechnologyTO technology : quadrant.getItems()) {
				// TODO error
				final Arc arc = arcMap.get(technology.getZs().get(0).getX().getArc().getName());
				arcRails.get(arc.getIndex()).get((int)Math.floor(count%arc.getRails())).push(technology);
				count++;
			}

			count = 0;
			for(int j = 0; j < arcRails.size(); j++) {
				final Stack<Stack<TechnologyTO>> rails = arcRails.get(j);
				for(int k = 0; k < rails.size(); k++) {
					final Stack<TechnologyTO> techs = rails.get(k);
					for(int l = 0; l < techs.size(); l++) {
						final TechnologyTO item = techs.get(l);
						// TODO error
						String quadrantName = item.getZs().get(0).getY().getQuadrant().getName();
						Quadrant techQuadrant = quadrantMap.get(quadrantName);
						if(techQuadrant==null) {
							techQuadrant = new Quadrant(quadrantName, QUADRANT_COLOURS[quadrantMap.size()]);
							techQuadrant.setStartTheta(quadrantMap.size()*90);
							quadrantMap.put(quadrantName, techQuadrant);
						}
						
						// TODO error
						final Arc arc = arcMap.get(item.getZs().get(0).getX().getArc().getName());
						final float r = (((arc.getOuterRadius()-arc.getInnerRadius())/((rails.size())+1))*(k+1))+arc.getInnerRadius();

						final float segmentWidth = 90/(techs.size()+1);
						final double x = (w/2) + r*Math.cos(rad(segmentWidth*(l+1) + techQuadrant.getStartTheta()));
						final double y = (h/2) + r*Math.sin(rad(segmentWidth*(l+1) + techQuadrant.getStartTheta()));

						if(item.getZs().get(0).getMovement().equals(MovementEnum.c)) {
							final Element triangle = doc.createElementNS(svgNS, "path");
							triangle.setAttribute("d", "M412.201,311.406c0.021,0,0.042,0,0.063,0c0.067,0,0.135,0,0.201,0c4.052,0,6.106-0.051,8.168-0.102c2.053-0.051,4.115-0.102,8.176-0.102h0.103c6.976-0.183,10.227-5.306,6.306-11.53c-3.988-6.121-4.97-5.407-8.598-11.224c-1.631-3.008-3.872-4.577-6.179-4.577c-2.276,0-4.613,1.528-6.48,4.699c-3.578,6.077-3.26,6.014-7.306,11.723C402.598,306.067,405.426,311.406,412.201,311.406");
							triangle.setAttribute("stroke", item.isCustomerStrategic()?"#FFDF00":"#FFFFFF");
							triangle.setAttribute("stroke-width", "2");
							triangle.setAttribute("fill", techQuadrant.getColour());
							triangle.setAttribute("transform", "scale("+((float)blipWidth/34)+") translate("+(-404+x*((float)34/blipWidth)-17)+", "+(-282+y*((float)34/blipWidth)-17)+")");
							svgRoot.appendChild(triangle);

							final Text tn = doc.createTextNode(""+(count+1));
							final Element textElement = doc.createElementNS(svgNS, "text");
							textElement.setAttribute("x", "" + x);
							textElement.setAttribute("y", "" + (y+(scaleFactor*6)));
							textElement.setAttribute("text-anchor", "middle");
							textElement.setAttribute("fill", "white");
							textElement.setAttribute("style", "font-size:" + scaleFactor*14 + "px;font-style:italic;font-weight:bold;");
							textElement.appendChild(tn);
							svgRoot.appendChild(textElement);
						} else {
							final Element circle = doc.createElementNS(svgNS, "path");
							circle.setAttribute("d", "M420.084,282.092c-1.073,0-2.16,0.103-3.243,0.313c-6.912,1.345-13.188,8.587-11.423,16.874c1.732,8.141,8.632,13.711,17.806,13.711c0.025,0,0.052,0,0.074-0.003c0.551-0.025,1.395-0.011,2.225-0.109c4.404-0.534,8.148-2.218,10.069-6.487c1.747-3.886,2.114-7.993,0.913-12.118C434.379,286.944,427.494,282.092,420.084,282.092");
							circle.setAttribute("stroke", item.isCustomerStrategic()?"#FFDF00":"#FFFFFF");
							circle.setAttribute("stroke-width", "2");
							circle.setAttribute("fill", techQuadrant.getColour());
							circle.setAttribute("transform", "scale("+((float)blipWidth/34)+") translate("+(-404+x*((float)34/blipWidth)-17)+", "+(-282+y*((float)34/blipWidth)-17)+")");
							svgRoot.appendChild(circle);

							final Text tnCircle = doc.createTextNode(""+(count+1));
							final Element circleTextElement = doc.createElementNS(svgNS, "text");
							circleTextElement.setAttribute("x", "" + x);
							circleTextElement.setAttribute("y", "" + (y+(scaleFactor*4)));
							circleTextElement.setAttribute("text-anchor", "middle");
							circleTextElement.setAttribute("fill", "white");
							circleTextElement.setAttribute("style", "font-size:" + scaleFactor*14 + "px;font-style:italic;font-weight:bold;");
							circleTextElement.appendChild(tnCircle);
							svgRoot.appendChild(circleTextElement);
						}
						count++;
					}
				}
			}

		}

		return doc;

	}

	private static void drawArc(
			final Element svgRoot,
			final Document doc,
			final String svgNS,
			final float innerRadius,
			final float outerRadius,
			final float x,
			final float y,
			final String colour) {

		final Element circleElement = doc.createElementNS(svgNS, "circle");
		circleElement.setAttribute("r", "" + outerRadius);
		circleElement.setAttribute("cx", "" + (int)x);
		circleElement.setAttribute("cy", "" + (int)y);
		circleElement.setAttribute("fill", colour);

		svgRoot.appendChild(circleElement);
	}

	private static void drawArcAxisText(
			final Element svgRoot,
			final Document doc,
			final String svgNS,
			final float scaleFactor,
			final float innerRadius,
			final float outerRadius, 
			final float totalWidth, 
			final float totalHeight,
			final String text) {

		final int x = (int)((totalWidth/2)+innerRadius+((outerRadius-innerRadius)/2));
		final int y = (int)totalHeight/2;

		final Text tn = doc.createTextNode(text.toUpperCase());

		final Element textElement = doc.createElementNS(svgNS, "text");
		textElement.setAttribute("x", "" + x);
		textElement.setAttribute("y", "" + (y+(scaleFactor*4)));
		textElement.setAttribute("text-anchor", "middle");
		textElement.setAttribute("fill", "#000");
		textElement.setAttribute("style", "font-size:" + scaleFactor*10 + "px;font-weight:900;");
		textElement.appendChild(tn);

		svgRoot.appendChild(textElement);
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
	}

	public static class Quadrant {
		private final String name;
		private final String colour;
		private int startTheta;
		private List<TechnologyTO> items = new ArrayList<TechnologyTO>();
		public Quadrant(String name, String colour) {
			this.name = name;
			this.colour = colour;
		}
		public String getName() {
			return name;
		}
		public String getColour() {
			return colour;
		}
		public int getStartTheta() {
			return startTheta;
		}
		public void setStartTheta(int startTheta) {
			this.startTheta = startTheta;
		}
		public List<TechnologyTO> getItems() {
			return items;
		}
		public void setItems(List<TechnologyTO> items) {
			this.items = items;
		}
	}

	/*private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		//get the range, casting to long to avoid overflow problems
		long range = (long)aEnd - (long)aStart + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long)(range * aRandom.nextDouble());
		int randomNumber =  (int)(fraction + aStart);
		return randomNumber;
	}*/

	private static double rad(final float deg){
		return deg*Math.PI/180;
	};

	private static RadarTO buildDataModel(RadarTO r){
		for(RadarTechnologyTO z: r.getZs()){
			z.setRadar(r);
			List<RadarTechnologyTO> zs = new ArrayList<RadarTechnologyTO>();
			zs.add(z);
			z.getTechnology().setZs(zs);
			
			List<RadarTechnologyTO> xZs = z.getX().getZs();
			if(xZs == null){
				z.getX().setZs(new ArrayList<RadarTechnologyTO>());
			}
			z.getX().getZs().add(z);
			
			List<RadarTechnologyTO> yZs = z.getY().getZs();
			if(yZs == null){
				z.getY().setZs(new ArrayList<RadarTechnologyTO>());
			}
			z.getY().getZs().add(z);
		}
		
		for(RadarMaturityTO x: r.getXs()){
			x.setRadar(r);
			List<RadarMaturityTO> xs = new ArrayList<RadarMaturityTO>();
			xs.add(x);
			x.getArc().setXs(xs);
		}
		
		for(RadarTechGroupingTO y: r.getYs()){
			y.setRadar(r);
			List<RadarTechGroupingTO> ys = new ArrayList<RadarTechGroupingTO>();
			ys.add(y);
			y.getQuadrant().setYs(ys);
		}
		
		return r;
	}
}
