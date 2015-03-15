package com.ai.techradar.service;

import java.util.List;

import com.ai.techradar.web.service.to.BusinessUnitTO;

public interface BusinessUnitService {

	List<BusinessUnitTO> getBusinessUnits();

	BusinessUnitTO createBusinessUnit(BusinessUnitTO businessUnit) throws ValidationException;
	
	BusinessUnitTO updateBusinessUnit(BusinessUnitTO businessUnit);
	
	boolean deleteBusinessUnit(Long id);

}
