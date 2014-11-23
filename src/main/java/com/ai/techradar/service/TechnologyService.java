package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.TechnologyTO;

public interface TechnologyService {

	List<TechnologyTO> getTechnologies();

	TechnologyTO createTechnology(TechnologyTO technology);

}
