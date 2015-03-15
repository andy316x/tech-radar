package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.BusinessUnit;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.BusinessUnitService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.web.service.to.BusinessUnitTO;

@SuppressWarnings("unchecked")
public class BusinessUnitServiceImpl implements BusinessUnitService {

	@Override
	public List<BusinessUnitTO> getBusinessUnits() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(BusinessUnit.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				);

		final List<BusinessUnitTO> bus = new ArrayList<BusinessUnitTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final BusinessUnitTO bu = new BusinessUnitTO();
			bu.setId((Long)row[0]);
			bu.setName((String)row[1]);

			bus.add(bu);
		}

		session.getTransaction().commit();
		session.close();

		return bus;
	}

	@Override
	public BusinessUnitTO createBusinessUnit(final BusinessUnitTO businessUnit) throws ValidationException {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		final List<String> validations = new ArrayList<String>();
		
		if(businessUnit != null) {
			if(!StringUtils.isBlank(businessUnit.getName())) {
				final BusinessUnit existingBusinessUnit = readBusinessUnit(businessUnit.getName(), session);
				
				if(existingBusinessUnit == null) {
					final BusinessUnit businessUnitEntity = new BusinessUnit();
					businessUnitEntity.setName(businessUnit.getName());

					final Long id = (Long)session.save(businessUnitEntity);

					businessUnit.setId(id);
				} else {
					validations.add("Business unit '" + businessUnit.getName() + "' already exists in tech radar");
				}
			} else {
				validations.add("Missing mandatory field 'business unit name'");
			}
		} else {
			validations.add("Missing mandatory field 'business unit'");
		}
		
		session.getTransaction().commit();
		session.close();
		
		if(!validations.isEmpty()) {
			throw new ValidationException(validations);
		}

		return businessUnit;
	}

	@Override
	public BusinessUnitTO updateBusinessUnit(final BusinessUnitTO businessUnitTO) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(BusinessUnit.class);
		query.add(Restrictions.eq("id", businessUnitTO.getId()));

		final BusinessUnit businessUnit = (BusinessUnit) query.uniqueResult();

		if(businessUnit != null) {
			businessUnit.setName(businessUnitTO.getName());
		} else {
			// TODO error
		}

		session.getTransaction().commit();
		session.close();

		return businessUnitTO;
	}

	@Override
	public boolean deleteBusinessUnit(final Long id) {
		boolean result = false;

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(BusinessUnit.class);
		query.add(Restrictions.eq("id", id));

		final BusinessUnit businessUnit = (BusinessUnit) query.uniqueResult();

		if(businessUnit != null) {
			session.delete(businessUnit);
			result = true;
		}

		session.getTransaction().commit();
		session.close();

		return result;
	}
	
	private BusinessUnit readBusinessUnit(final String name, final Session session) {
		final Criteria query = session.createCriteria(BusinessUnit.class);
		query.add(Restrictions.ilike("name", name));
		return (BusinessUnit)query.uniqueResult();
	}

}
