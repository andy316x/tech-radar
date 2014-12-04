package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.TechnologyService;
import com.ai.techradar.web.service.to.TechnologyTO;

@SuppressWarnings("unchecked")
public class TechnologyServiceImpl implements TechnologyService {

	public List<TechnologyTO> getTechnologies() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Technology.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				);

		final List<TechnologyTO> ts = new ArrayList<TechnologyTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final TechnologyTO t = new TechnologyTO();
			t.setId((Long)row[0]);
			t.setName((String)row[1]);

			ts.add(t);
		}

		session.getTransaction().commit();
		session.close();

		return ts;
	}

	public TechnologyTO getTechnologyById(final Long id) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Technology.class);

		query.add(Restrictions.eq("id", id));

		final TechnologyTO technology = new TechnologyTO();

		final Technology technologyEntity = (Technology) query.uniqueResult();

		technology.setId(technologyEntity.getId());
		technology.setName(technologyEntity.getName());
		technology.setBlipSize(technologyEntity.getUsageCount());
		technology.setUrl(technologyEntity.getUrl());
		technology.setDescription(technologyEntity.getDescription());
		technology.setDetailUrl(technologyEntity.getDetailUrl());
		technology.setCustomerStrategic(technologyEntity.isCustomerStrategic());

		session.getTransaction().commit();
		session.close();

		return technology;
	}

	public TechnologyTO createTechnology(final TechnologyTO technology) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Technology technologyEntity = new Technology();
		technologyEntity.setName(technology.getName());
		technologyEntity.setDescription(technology.getDescription());

		final Long id = (Long)session.save(technologyEntity);

		session.getTransaction().commit();
		session.close();

		technology.setId(id);

		return technology;
	}

}