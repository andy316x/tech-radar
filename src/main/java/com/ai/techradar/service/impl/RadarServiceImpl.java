package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechnologyTO;

public class RadarServiceImpl implements RadarService {

	public List<RadarTO> getRadars() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("filename"))
				.add(Projections.property("dateUploaded"))
		);

		final List<RadarTO> rs = new ArrayList<RadarTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final RadarTO r = new RadarTO();
			r.setId((Long)row[0]);
			r.setFilename((String)row[1]);
			r.setDateUploaded((Date)row[2]);

			rs.add(r);
		}

		session.getTransaction().commit();
		session.close();

		return rs;
	}

	public RadarTO getRadarById(final Long id) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);
		query.add(Restrictions.eq("id", id));

		final Radar radar = (Radar)query.uniqueResult();

		final RadarTO r = new RadarTO();
		r.setId(radar.getId());
		r.setFilename(radar.getFilename());
		r.setDateUploaded(radar.getDateUploaded());

		final List<TechnologyTO> ts = new ArrayList<TechnologyTO>();
		for(final Technology technology : radar.getTechnologies()) {
			final TechnologyTO t = new TechnologyTO();
			t.setTechnologyName(technology.getName());
			t.setQuadrantName(technology.getQuadrant());
			t.setMovement(technology.getMovement());
			t.setArcName(technology.getArc());
			t.setBlipSize(technology.getUsageCount());
			t.setUrl(technology.getUrl());
			t.setDescription(technology.getDescription());
			t.setDetailUrl(technology.getDetailUrl());
			t.setCustomerStrategic(technology.isCustomerStrategic());
			ts.add(t);
		}
		r.setTechnologies(ts);

		session.getTransaction().commit();
		session.close();

		return r;
	}

}
