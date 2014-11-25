package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;

public interface RadarService {

	List<RadarTO> getRadars();

	RadarTO getRadarById(Long id);

	RadarTO createRadar(RadarTO radar) throws ValidationException;
	
	boolean deleteRadarById(Long id);
	
	RadarTO addTechnologiesToRadar(Long radarId, List<RadarTechnologyTO> radarTechnologyTOs) throws ValidationException;

}
