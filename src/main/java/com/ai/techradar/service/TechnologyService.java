package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.ai.techradar.web.service.to.UserTechnologyTO;

public interface TechnologyService {

	List<TechnologyTO> getTechnologies();

	TechnologyTO getTechnologyById(Long id);

	TechnologyTO createTechnology(TechnologyTO technology);

	UserTechnologyTO setUserTechnology(Long technologyId, UserTechnologyTO technology) throws ValidationException;

	List<UserTechnologyTO> getTechnologyUsers(Long technologyId) throws ValidationException;

	List<RadarTechnologyTO> getTechnologyRadars(Long technologyId) throws ValidationException;

}
