package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.*;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.web.service.to.*;

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

	
	/**
	 *   Currently returns the following uni-directional data model:
	 *   
	 *     ------------------------------------------> X ---> Arc
	 *     |         |                           |
	 *   Radar ----> Z---------> Technology ---> Z
	 *     |         |                           |
	 *     ------------------------------------------> Y ---> Quadrant
	 *     
	 *     Both Zs point to the same X&Y Objects.
	 *     Both Zs are equivalent except the second contains a null radar.
	 *   
	 */
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

		//final List<TechnologyTO> ts = new ArrayList<TechnologyTO>();
		final List<XTO> xs = new ArrayList<XTO>();
		final List<YTO> ys = new ArrayList<YTO>();
		final List<ZTO> zs = new ArrayList<ZTO>();
		//final List<ArcTO> arcs = new ArrayList<ArcTO>();
		//final List<QuadrantTO> quadrants = new ArrayList<QuadrantTO>();
		
		for(final Z z_ : radar.getZs()) {
			Arc arc_ = z_.getX().getArc();
			Quadrant quadrant_ = z_.getY().getQuadrant();
			
			XTO x = null;
			for(XTO tempX: xs){
				if(tempX.getArc().getName().equals(arc_.getName())){ // NPE here
					x = tempX;
				}
			}
			if(x == null){
				ArcTO arc = new ArcTO();
				arc.setName(arc_.getName());
				arc.setId(arc_.getId());
				x = new XTO();
				x.setArc(arc);
				x.setId(z_.getX().getId());
				xs.add(x);
			}
			
			YTO y = null;
			for(YTO tempY: ys){
				if(tempY.getQuadrant().getName().equals(quadrant_.getName())){
					y = tempY;
				}
			}
			if(y == null){
				QuadrantTO quadrant = new QuadrantTO();
				quadrant.setName(quadrant_.getName());
				quadrant.setId(quadrant_.getId());
				y = new YTO();
				y.setQuadrant(quadrant);
				y.setId(z_.getY().getId());
				ys.add(y);
			}
			final ZTO zto = new ZTO();
			zto.setMovement(z_.getMovement());
			zto.setX(x);
			zto.setY(y);
			zs.add(zto);
			
			final TechnologyTO t = new TechnologyTO();
			final Technology technology = z_.getTechnology();
			t.setId(technology.getId());
			t.setName(technology.getName());
			t.setBlipSize(technology.getUsageCount());
			t.setUrl(technology.getUrl());
			t.setDescription(technology.getDescription());
			t.setDetailUrl(technology.getDetailUrl());
			t.setCustomerStrategic(technology.isCustomerStrategic());

			List<ZTO> temp_Zs = new ArrayList<ZTO>();
			temp_Zs.add(zto.clone());
			t.setZs(temp_Zs);
			zto.setTechnology(t);
		}
		r.setXs(xs);
		r.setYs(ys);
		r.setZs(zs);

		session.getTransaction().commit();
		session.close();

		return r;
	}

}
