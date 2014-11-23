package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.TechGroupingTO;

public interface TechGroupingService {

	List<TechGroupingTO> getTechGroupings();

	TechGroupingTO createTechGrouping(TechGroupingTO techGrouping);

}
