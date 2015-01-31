package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ai.techradar.database.entities.Maturity;
import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.RadarMaturity;
import com.ai.techradar.database.entities.RadarTechGrouping;
import com.ai.techradar.database.entities.RadarTechnology;
import com.ai.techradar.database.entities.TechGrouping;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.entities.User;
import com.ai.techradar.database.entities.UserTechnology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.UserService;
import com.ai.techradar.service.UserService.UserInfo;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.ai.techradar.web.service.to.TechGroupingTO;

@SuppressWarnings("unchecked")
public class RadarServiceImpl implements RadarService {
	
	@Autowired
	@Qualifier("UserService")
	private UserService userService;

	public List<RadarTO> getRadars() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);

		query.createAlias("createdBy", "createdBy");

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				.add(Projections.property("createdDate"))
				.add(Projections.property("createdBy.username"))
				);

		final List<RadarTO> rs = new ArrayList<RadarTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final RadarTO r = new RadarTO();
			r.setId((Long)row[0]);
			r.setName((String)row[1]);
			r.setDateCreated((Date)row[2]);
			
			final UserInfo userInfo = userService.getUserInfo((String)row[3]);
			if(userInfo.getSurname() != null) {
				r.setCreatedBy(userInfo.getGivenName() + " " + userInfo.getSurname());	
			} else {
				r.setCreatedBy(userInfo.getGivenName());
			}

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
		r.setName(radar.getName());
		r.setDateCreated(radar.getCreatedDate());

		final List<MaturityTO> maturities = new ArrayList<MaturityTO>();
		final List<RadarMaturity> maturityEntities = radar.getRadarMaturities();
		Collections.sort(maturityEntities, MATURITY_COMPARATOR);
		for(final RadarMaturity radarMaturity : maturityEntities) {
			final MaturityTO maturityTO = new MaturityTO();
			maturityTO.setName(radarMaturity.getMaturity().getName());
			maturities.add(maturityTO);
		}

		final List<TechGroupingTO> techGroupings = new ArrayList<TechGroupingTO>();
		final List<RadarTechGrouping> techGroupingEntities = radar.getRadarTechGroupings();
		Collections.sort(techGroupingEntities, TECH_GROUPING_COMPARATOR);
		for(final RadarTechGrouping radarTechGrouping : techGroupingEntities) {
			final TechGroupingTO techGroupingTO = new TechGroupingTO();
			techGroupingTO.setName(radarTechGrouping.getTechGrouping().getName());
			techGroupings.add(techGroupingTO);
		}

		final List<RadarTechnologyTO> technologies = new ArrayList<RadarTechnologyTO>();

		for(final RadarTechnology z_ : radar.getRadarTechnologies()) {
			final RadarTechnologyTO tech = new RadarTechnologyTO();
			tech.setMovement(z_.getMovement());
			tech.setTechGrouping(z_.getRadarTechGrouping().getTechGrouping().getName());
			tech.setMaturity(z_.getRadarMaturity().getMaturity().getName());
			final Technology technology = z_.getTechnology();
			tech.setTechnology(technology.getName());
			tech.setBlipSize(technology.getUsageCount());
			tech.setUrl(technology.getUrl());
			tech.setDescription(technology.getDescription());
			tech.setDetailUrl(technology.getDetailUrl());
			tech.setCustomerStrategic(technology.isCustomerStrategic());
			technologies.add(tech);
		}
		r.setMaturities(maturities);
		r.setTechGroupings(techGroupings);
		r.setTechnologies(technologies);

		session.getTransaction().commit();
		session.close();

		return r;
	}

	private static final Comparator<RadarMaturity> MATURITY_COMPARATOR = new Comparator<RadarMaturity>() {
		public int compare(final RadarMaturity maturity1, final RadarMaturity maturity2) {
			if(maturity1.getTheOrder() == maturity2.getTheOrder()) {
				return 0;
			}

			if(maturity1.getTheOrder() < maturity2.getTheOrder()) {
				return -1;
			} else {
				return 1;
			}
		}
	};

	private static final Comparator<RadarTechGrouping> TECH_GROUPING_COMPARATOR = new Comparator<RadarTechGrouping>() {
		public int compare(final RadarTechGrouping maturity1, final RadarTechGrouping maturity2) {
			if(maturity1.getTheOrder() == maturity2.getTheOrder()) {
				return 0;
			}

			if(maturity1.getTheOrder() < maturity2.getTheOrder()) {
				return -1;
			} else {
				return 1;
			}
		}
	};

	public RadarTO createRadar(final RadarTO radarTO) throws ValidationException {
		final List<String> validations = new ArrayList<String>();

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {

			final Radar radar = new Radar();
			radar.setCreatedDate(new Date());


			// Created by
			final User createdBy = readUser(AdminHandlerHelper.getCurrentUser(), session);
			if(createdBy!=null) {
				radar.setCreatedBy(createdBy);
				createdBy.getRadars().add(radar);
			}


			if(!StringUtils.isBlank(radarTO.getName())) {
				radar.setName(radarTO.getName());
			} else {
				validations.add("Mandatory field 'name' has not been supplied");
			}

			final Long id = (Long)session.save(radar);
			radarTO.setId(id);
			radarTO.setCreatedBy(createdBy.getUsername());

			final List<MaturityTO> radarMaturityTOs = radarTO.getMaturities();
			if(radarMaturityTOs!=null && !radarMaturityTOs.isEmpty()) {
				if(radarMaturityTOs.size() > 3) {
					int i = 0;
					for(final MaturityTO radarMaturityTO : radarMaturityTOs) {
						final Maturity maturity = readMaturity(radarMaturityTO.getName(), session);
						if(maturity!=null) {
							final RadarMaturity radarMaturity = new RadarMaturity();
							radarMaturity.setRadar(radar);
							radarMaturity.setMaturity(maturity);
							radarMaturity.setTheOrder(i++);
							session.persist(radarMaturity);
						} else {
							validations.add("Maturity " + radarMaturityTO.getName() + " does not exist");
						}
					}
				} else {
					validations.add(radarMaturityTOs.size() + " maturities supplied, at least 4 required");
				}
			} else {
				validations.add("No maturities");
			}

			final List<TechGroupingTO> radarTechGroupingTOs = radarTO.getTechGroupings();
			if(radarTechGroupingTOs!=null && !radarTechGroupingTOs.isEmpty()) {
				if(radarTechGroupingTOs.size() == 4) {
					int i = 0;
					for(final TechGroupingTO radarTechGroupingTO : radarTechGroupingTOs) {
						final TechGrouping techGrouping = readTechGrouping(radarTechGroupingTO.getName(), session);
						if(techGrouping!=null) {
							final RadarTechGrouping radarTechGrouping = new RadarTechGrouping();
							radarTechGrouping.setRadar(radar);
							radarTechGrouping.setTechGrouping(techGrouping);
							radarTechGrouping.setTheOrder(i++);
							session.persist(radarTechGrouping);
						} else {
							validations.add("Tech grouping " + radarTechGroupingTO.getName() + " does not exist");
						}
					}
				} else {
					validations.add(radarTechGroupingTOs.size() + " tech groupings supplied, 4 required");
				}
			} else {
				validations.add("No tech groupings");
			}


			if(validations.isEmpty()) {
				session.getTransaction().commit();
			} else {
				session.getTransaction().rollback();
				throw new ValidationException(validations);
			}

		} catch(final ValidationException ex) {
			throw new ValidationException(ex.getValidations());
		} finally {
			session.close();
		}

		return radarTO;
	}

	public boolean deleteRadarById(final Long id) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);
		query.add(Restrictions.eq("id", id));

		final Radar radar = (Radar)query.uniqueResult();

		for(final RadarTechnology radarTechnology : radar.getRadarTechnologies()) {
			session.delete(radarTechnology);
		}

		for(final RadarTechGrouping radarTechGrouping : radar.getRadarTechGroupings()) {
			session.delete(radarTechGrouping);
		}

		for(final RadarMaturity radarMaturity : radar.getRadarMaturities()) {
			session.delete(radarMaturity);
		}

		session.delete(radar);

		session.getTransaction().commit();
		session.close();

		return true;
	}

	public RadarTO addTechnologiesToRadar(final Long radarId, final List<RadarTechnologyTO> radarTechnologyTOs) throws ValidationException {
		final List<String> validations = new ArrayList<String>();
		
		final String uid = AdminHandlerHelper.getCurrentUser();

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {
			
			if(!StringUtils.isBlank(uid)) {
				
				final User user = readUser(uid, session);
				if(user==null) {
					validations.add("Unable to find user with ID " + radarId);
				}
				
				final Radar radar = readRadar(radarId, session);
				if(radar==null) {
					validations.add("Unable to find radar with ID " + radarId);
				}
				
				if(!validations.isEmpty()) {
					throw new ValidationException(validations);
				}

				// Remove all the existing technologies
				// TODO at the moment an upload removes all previous 
				// technologies - what should the behaviour be?
				final Iterator<RadarTechnology> deletedTechnologyIter = radar.getRadarTechnologies().iterator();
				while(deletedTechnologyIter.hasNext()) {
					final RadarTechnology deletedTechnology = deletedTechnologyIter.next();
					session.delete(deletedTechnology);
					deletedTechnologyIter.remove();
				}

				if(radarTechnologyTOs!=null && !radarTechnologyTOs.isEmpty()) {

					int i = 0;
					for(final RadarTechnologyTO radarTechnologyTO : radarTechnologyTOs) {

						final String technologyName = radarTechnologyTO.getTechnology();
						final Technology technology = readTechnology(technologyName, session);
						if(technology==null) {
							validations.add("Unable to find technology '" + technologyName);
						}

						final String maturityName = radarTechnologyTO.getMaturity();
						final RadarMaturity radarMaturity = readRadarMaturity(radarId, maturityName, session);
						if(radarMaturity==null) {
							validations.add("Unable to find maturity '" + maturityName + "' in radar with ID " + radarId);
						}

						final String techGroupingName = radarTechnologyTO.getTechGrouping();
						final RadarTechGrouping radarTechGrouping = readRadarTechGrouping(radarId, techGroupingName, session);
						if(radarTechGrouping==null) {
							validations.add("Unable to find tech grouping '" + techGroupingName + "' in radar with ID " + radarId);
						}

						final RadarTechnology radarTechnology = new RadarTechnology();
						radarTechnology.setTechnology(technology);
						radarTechnology.setRadar(radar);
						radarTechnology.setRadarMaturity(radarMaturity);
						radarTechnology.setRadarTechGrouping(radarTechGrouping);
						radarTechnology.setMovement(radarTechnologyTO.getMovement());
						radarTechnology.setTheOrder(i++);
						
						radarTechnology.setAddedDate(new Date());
						radarTechnology.setAddedBy(user);
						user.getRadarTechnologies().add(radarTechnology);
						
						session.persist(radarTechnology);

					}

				} else {
					validations.add("No technologies supplied");
				}


				if(validations.isEmpty()) {
					session.getTransaction().commit();
				} else {
					session.getTransaction().rollback();
					throw new ValidationException(validations);
				}	
				
			} else {
				validations.add("User is not logged in");
				throw new ValidationException(validations);
			}

		} catch(final ValidationException ex) {
			throw new ValidationException(ex.getValidations());
		} finally {
			session.close();
		}

		return null;
	}

	private RadarMaturity readRadarMaturity(final Long id, final String maturity, final Session session) {
		final Criteria query = session.createCriteria(RadarMaturity.class);

		query.createAlias("radar", "radar");
		query.createAlias("maturity", "maturity");

		query.add(Restrictions.eq("radar.id", id));
		query.add(Restrictions.ilike("maturity.name", maturity));

		return (RadarMaturity)query.uniqueResult();
	}

	private RadarTechGrouping readRadarTechGrouping(final Long id, final String techGrouping, final Session session) {
		final Criteria query = session.createCriteria(RadarTechGrouping.class);

		query.createAlias("radar", "radar");
		query.createAlias("techGrouping", "tg");

		query.add(Restrictions.eq("radar.id", id));
		query.add(Restrictions.ilike("tg.name", techGrouping));

		return (RadarTechGrouping)query.uniqueResult();
	}

	private Radar readRadar(final Long id, final Session session) {
		final Criteria query = session.createCriteria(Radar.class);
		query.add(Restrictions.eq("id", id));
		return (Radar)query.uniqueResult();
	}

	private Technology readTechnology(final String name, final Session session) {
		final Criteria query = session.createCriteria(Technology.class);
		query.add(Restrictions.ilike("name", name));
		return (Technology)query.uniqueResult();
	}

	private Maturity readMaturity(final String name, final Session session) {
		final Criteria query = session.createCriteria(Maturity.class);
		query.add(Restrictions.ilike("name", name));
		return (Maturity)query.uniqueResult();
	}

	private TechGrouping readTechGrouping(final String name, final Session session) {
		final Criteria query = session.createCriteria(TechGrouping.class);
		query.add(Restrictions.ilike("name", name));
		return (TechGrouping)query.uniqueResult();
	}

	private User readUser(final String username, final Session session) {
		final Criteria query = session.createCriteria(User.class);
		query.add(Restrictions.eq("username", username));
		final User user = (User)query.uniqueResult();

		if(user != null) {
			return user;
		}

		final User newUser = new User();
		newUser.setUsername(username);
		newUser.setRadars(new ArrayList<Radar>());
		newUser.setTechnologies(new ArrayList<UserTechnology>());
		newUser.setRadarTechnologies(new ArrayList<RadarTechnology>());
		session.persist(newUser);

		return newUser;
	}

}
