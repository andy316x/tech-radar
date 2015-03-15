package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.BusinessUnit;
import com.ai.techradar.database.entities.Maturity;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.MaturityService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.web.service.to.MaturityTO;

@SuppressWarnings("unchecked")
public class MaturityServiceImpl implements MaturityService {

	@Override
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

	@Override
	public MaturityTO createMaturity(final MaturityTO maturity) throws ValidationException {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final List<String> validations = new ArrayList<String>();

		if(maturity != null) {
			if(!StringUtils.isBlank(maturity.getName())) {
				final Maturity existingMaturity = readMaturity(maturity.getName(), session);

				if(existingMaturity == null) {
					final Maturity maturityEntity = new Maturity();
					maturityEntity.setName(maturity.getName());

					final Long id = (Long)session.save(maturityEntity);

					maturity.setId(id);
				} else {
					validations.add("Maturity '" + maturity.getName() + "' already exists in tech radar");
				}
			} else {
				validations.add("Missing mandatory field 'maturity name'");
			}
		} else {
			validations.add("Missing mandatory field 'maturity'");
		}

		session.getTransaction().commit();
		session.close();

		if(!validations.isEmpty()) {
			throw new ValidationException(validations);
		}

		return maturity;
	}

	@Override
	public MaturityTO updateMaturity(final MaturityTO maturityTO) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Maturity.class);
		query.add(Restrictions.eq("id", maturityTO.getId()));

		final BusinessUnit businessUnit = (BusinessUnit) query.uniqueResult();

		if(businessUnit != null) {
			businessUnit.setName(maturityTO.getName());
		} else {
			// TODO error
		}

		session.getTransaction().commit();
		session.close();

		return maturityTO;
	}

	@Override
	public boolean deleteMaturity(final Long id) {
		boolean result = false;

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Maturity.class);
		query.add(Restrictions.eq("id", id));

		final Maturity maturity = (Maturity) query.uniqueResult();

		if(maturity != null) {
			session.delete(maturity);
			result = true;
		}

		session.getTransaction().commit();
		session.close();

		return result;
	}

	private Maturity readMaturity(final String name, final Session session) {
		final Criteria query = session.createCriteria(Maturity.class);
		query.add(Restrictions.ilike("name", name));
		return (Maturity)query.uniqueResult();
	}

}
