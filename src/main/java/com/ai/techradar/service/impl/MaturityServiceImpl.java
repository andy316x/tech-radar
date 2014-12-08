package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import com.ai.techradar.database.entities.Maturity;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.MaturityService;
import com.ai.techradar.web.service.to.MaturityTO;

@SuppressWarnings("unchecked")
public class MaturityServiceImpl extends AbstractTechRadarService implements MaturityService {

	public MaturityServiceImpl(final String user) {
		super(user);
	}

	public List<MaturityTO> getMaturities() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Maturity.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				);

		final List<MaturityTO> ms = new ArrayList<MaturityTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final MaturityTO m = new MaturityTO();
			m.setId((Long)row[0]);
			m.setName((String)row[1]);

			ms.add(m);
		}

		session.getTransaction().commit();
		session.close();

		return ms;
	}

	public MaturityTO createMaturity(final MaturityTO maturity) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Maturity maturityEntity = new Maturity();
		maturityEntity.setName(maturity.getName());

		final Long id = (Long)session.save(maturityEntity);

		session.getTransaction().commit();
		session.close();

		maturity.setId(id);

		return maturity;
	}

}
