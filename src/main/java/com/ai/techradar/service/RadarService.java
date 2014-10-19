package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.RadarTO;

public interface RadarService {

	List<RadarTO> getRadars();

	RadarTO getRadarById(Long id);

}
