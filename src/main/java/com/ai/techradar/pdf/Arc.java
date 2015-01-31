package com.ai.techradar.pdf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes an arc displayed in a radar quadrant.
 */
public class Arc {

	private final String label;

	private final int radius;

	private final Color colour;

	private final List<Integer> technologyIndexes = new ArrayList<Integer>();

	public Arc(final String label, final int radius, final Color colour) {
		this.label = label;
		this.radius = radius;
		this.colour = colour;
	}

	public String getLabel() {
		return label;
	}

	public int getRadius() {
		return radius;
	}

	public Color getColour() {
		return colour;
	}

	public void addTechnologyIndex(final Integer technologyIndexToAdd) {
		technologyIndexes.add(technologyIndexToAdd);
	}

	public void addAllTechnologyIndexes(final List<Integer> technologyIndexesToAdd) {
		technologyIndexes.addAll(technologyIndexesToAdd);
	}

	public List<Integer> getTechnologyIndexes() {
		return Collections.unmodifiableList(technologyIndexes);
	}
}
