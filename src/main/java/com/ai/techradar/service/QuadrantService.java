package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.QuadrantTO;

public interface QuadrantService {

	List<QuadrantTO> getQuadrants();

	QuadrantTO createQuadrant(QuadrantTO quadrant) throws ValidationException;
	
	QuadrantTO updateQuadrant(QuadrantTO quadrant);
	
	boolean deleteQuadrant(Long id);

}
