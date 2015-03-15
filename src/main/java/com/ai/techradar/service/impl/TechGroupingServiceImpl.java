package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.TechGrouping;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.web.service.to.TechGroupingTO;

@SuppressWarnings("unchecked")
public class TechGroupingServiceImpl implements TechGroupingService {

	@Override
	public List<TechGroupingTO> getTechGroupings() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(TechGrouping.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				);

		final List<TechGroupingTO> tgs = new ArrayList<TechGroupingTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final TechGroupingTO tg = new TechGroupingTO();
			tg.setId((Long)row[0]);
			tg.setName((String)row[1]);

			tgs.add(tg);
		}

		session.getTransaction().commit();
		session.close();

		return tgs;
	}

	@Override
	public TechGroupingTO createTechGrouping(final TechGroupingTO techGrouping) throws ValidationException {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		final List<String> validations = new ArrayList<String>();

		if(techGrouping != null) {
			if(!StringUtils.isBlank(techGrouping.getName())) {
				final TechGrouping existingTechGrouping = readTechGrouping(techGrouping.getName(), session);

				if(existingTechGrouping == null) {
					final TechGrouping techGroupingEntity = new TechGrouping();
					techGroupingEntity.setName(techGrouping.getName());

					final Long id = (Long)session.save(techGroupingEntity);

					techGrouping.setId(id);
				} else {
					validations.add("Tech grouping '" + techGrouping.getName() + "' already exists in tech radar");
				}
			} else {
				validations.add("Missing mandatory field 'tech grouping name'");
			}
		} else {
			validations.add("Missing mandatory field 'tech grouping'");
		}

		session.getTransaction().commit();
		session.close();
		
		if(!validations.isEmpty()) {
			throw new ValidationException(validations);
		}

		return techGrouping;
	}

	@Override
	public TechGroupingTO updateTechGrouping(final TechGroupingTO techGroupingTO) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(TechGrouping.class);
		query.add(Restrictions.eq("id", techGroupingTO.getId()));

		final TechGrouping techGrouping = (TechGrouping) query.uniqueResult();

		if(techGrouping != null) {
			techGrouping.setName(techGroupingTO.getName());
		} else {
			// TODO error
		}

		session.getTransaction().commit();
		session.close();

		return techGroupingTO;
	}

	@Override
	public boolean deleteTechGrouping(final Long id) {
		boolean result = false;

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(TechGrouping.class);
		query.add(Restrictions.eq("id", id));

		final TechGrouping techGrouping = (TechGrouping) query.uniqueResult();

		if(techGrouping != null) {
			session.delete(techGrouping);
			result = true;
		}

		session.getTransaction().commit();
		session.close();

		return result;
	}

	private TechGrouping readTechGrouping(final String name, final Session session) {
		final Criteria query = session.createCriteria(TechGrouping.class);
		query.add(Restrictions.ilike("name", name));
		return (TechGrouping)query.uniqueResult();
	}

}
