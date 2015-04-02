package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.Quadrant;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.QuadrantService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.web.service.to.QuadrantTO;

@SuppressWarnings("unchecked")
public class QuadrantServiceImpl implements QuadrantService {

	@Override
	public List<QuadrantTO> getQuadrants() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Quadrant.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				.add(Projections.property("techGrouping"))
				);

		final List<QuadrantTO> tgs = new ArrayList<QuadrantTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final QuadrantTO tg = new QuadrantTO();
			tg.setId((Long)row[0]);
			tg.setName((String)row[1]);
			tg.setTechGrouping((Boolean)row[2]);

			tgs.add(tg);
		}

		session.getTransaction().commit();
		session.close();

		return tgs;
	}

	@Override
	public QuadrantTO createQuadrant(final QuadrantTO quadrant) throws ValidationException {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		final List<String> validations = new ArrayList<String>();

		if(quadrant != null) {
			if(!StringUtils.isBlank(quadrant.getName())) {
				final Quadrant existingQuadrant = readQuadrant(quadrant.getName(), session);

				if(existingQuadrant == null) {
					final Quadrant quadrantEntity = new Quadrant();
					quadrantEntity.setName(quadrant.getName());
					quadrantEntity.setTechGrouping(quadrant.isTechGrouping());

					final Long id = (Long)session.save(quadrantEntity);

					quadrant.setId(id);
				} else {
					validations.add("Quadrant '" + quadrant.getName() + "' already exists in tech radar");
				}
			} else {
				validations.add("Missing mandatory field 'quadrant name'");
			}
		} else {
			validations.add("Missing mandatory field 'quadrant'");
		}

		session.getTransaction().commit();
		session.close();
		
		if(!validations.isEmpty()) {
			throw new ValidationException(validations);
		}

		return quadrant;
	}

	@Override
	public QuadrantTO updateQuadrant(final QuadrantTO quadrantTO) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Quadrant.class);
		query.add(Restrictions.eq("id", quadrantTO.getId()));

		final Quadrant quadrant = (Quadrant) query.uniqueResult();

		if(quadrant != null) {
			quadrant.setName(quadrantTO.getName());
		} else {
			// TODO error
		}

		session.getTransaction().commit();
		session.close();

		return quadrantTO;
	}

	@Override
	public boolean deleteQuadrant(final Long id) {
		boolean result = false;

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Quadrant.class);
		query.add(Restrictions.eq("id", id));

		final Quadrant quadrant = (Quadrant) query.uniqueResult();

		if(quadrant != null) {
			session.delete(quadrant);
			result = true;
		}

		session.getTransaction().commit();
		session.close();

		return result;
	}

	private Quadrant readQuadrant(final String name, final Session session) {
		final Criteria query = session.createCriteria(Quadrant.class);
		query.add(Restrictions.ilike("name", name));
		return (Quadrant)query.uniqueResult();
	}

}
