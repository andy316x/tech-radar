package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.UserTechnologyTO;

public interface MeService {

	List<UserTechnologyTO> getMySkillLevels() throws ValidationException;

}
