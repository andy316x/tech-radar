package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import com.ai.techradar.database.entities.TechGrouping;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.web.service.to.TechGroupingTO;

@SuppressWarnings("unchecked")
public class TechGroupingServiceImpl implements TechGroupingService {

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

	public TechGroupingTO createTechGrouping(final TechGroupingTO techGrouping) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final TechGrouping techGroupingEntity = new TechGrouping();
		techGroupingEntity.setName(techGrouping.getName());

		final Long id = (Long)session.save(techGroupingEntity);

		session.getTransaction().commit();
		session.close();

		techGrouping.setId(id);

		return techGrouping;
	}

}
