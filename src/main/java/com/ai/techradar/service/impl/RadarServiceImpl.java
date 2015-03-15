package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ai.techradar.database.entities.BusinessUnit;
import com.ai.techradar.database.entities.Maturity;
import com.ai.techradar.database.entities.MovementEnum;
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
		query.createAlias("businessUnit", "businessUnit");

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				.add(Projections.property("description"))
				.add(Projections.property("businessUnit.name"))
				.add(Projections.property("published"))
				.add(Projections.property("lastPublishedDate"))
				.add(Projections.property("approved"))
				.add(Projections.property("majorVersion"))
				.add(Projections.property("minorVersion"))
				.add(Projections.property("createdDate"))
				.add(Projections.property("createdBy.username"))
				);

		final List<RadarTO> rs = new ArrayList<RadarTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final RadarTO r = new RadarTO();
			r.setId((Long)row[0]);
			r.setName((String)row[1]);
			r.setDescription((String)row[2]);
			r.setBusinessUnit((String)row[3]);
			r.setPublished((Boolean)row[4]);
			r.setLastPublishedDate((Date)row[5]);
			r.setApproved((Boolean)row[6]);
			r.setMajorVersion((Integer)row[7]);
			r.setMinorVersion((Integer)row[8]);
			r.setDateCreated((Date)row[9]);

			final UserInfo userInfo = userService.getUserInfo((String)row[10]);
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

		final RadarTO r = new RadarTO();


		// Maturities enrichment
		final List<MaturityTO> maturities = new ArrayList<MaturityTO>();

		final Criteria maturitiesQuery = session.createCriteria(RadarMaturity.class);
		final Criteria maturityJoinToRadar = maturitiesQuery.createCriteria("radar", "radar",  JoinType.INNER_JOIN);
		maturitiesQuery.createAlias("maturity", "maturity",  JoinType.INNER_JOIN);
		maturityJoinToRadar.add(Restrictions.eq("id", id));
		maturityJoinToRadar.createAlias("businessUnit", "businessUnit", JoinType.INNER_JOIN);
		maturityJoinToRadar.createAlias("createdBy", "createdBy", JoinType.INNER_JOIN);
		maturitiesQuery.addOrder(Order.asc("theOrder"));
		maturitiesQuery.setProjection(Projections.projectionList()
				.add(Projections.property("radar.id"))
				.add(Projections.property("radar.name"))
				.add(Projections.property("radar.description"))
				.add(Projections.property("businessUnit.name"))
				.add(Projections.property("radar.published"))
				.add(Projections.property("radar.lastPublishedDate"))
				.add(Projections.property("radar.approved"))
				.add(Projections.property("radar.majorVersion"))
				.add(Projections.property("radar.minorVersion"))
				.add(Projections.property("radar.createdDate"))
				.add(Projections.property("createdBy.username"))
				.add(Projections.property("maturity.name")));

		for(final Object[] row : (List<Object[]>)maturitiesQuery.list()) {
			r.setId((Long)row[0]);
			r.setName((String)row[1]);
			r.setDescription((String)row[2]);
			r.setBusinessUnit((String)row[3]);
			r.setPublished((Boolean)row[4]);
			r.setLastPublishedDate((Date)row[5]);
			r.setApproved((Boolean)row[6]);
			r.setMajorVersion((Integer)row[7]);
			r.setMinorVersion((Integer)row[8]);
			r.setDateCreated((Date)row[9]);
			r.setCreatedBy((String)row[10]);

			final MaturityTO maturityTO = new MaturityTO();
			maturityTO.setName((String)row[11]);
			maturities.add(maturityTO);
		}
		r.setMaturities(maturities);


		// Tech groupings enrichment
		final List<TechGroupingTO> techGroupings = new ArrayList<TechGroupingTO>();

		final Criteria techGroupingsQuery = session.createCriteria(RadarTechGrouping.class);
		final Criteria techGroupingJoinToRadar = techGroupingsQuery.createCriteria("radar", "radar",  JoinType.INNER_JOIN);
		techGroupingsQuery.createAlias("techGrouping", "techGrouping",  JoinType.INNER_JOIN);
		techGroupingJoinToRadar.add(Restrictions.eq("id", id));
		techGroupingsQuery.addOrder(Order.asc("theOrder"));
		techGroupingsQuery.setProjection(Projections.property("techGrouping.name"));

		for(final String row : (List<String>)techGroupingsQuery.list()) {
			final TechGroupingTO techGroupingTO = new TechGroupingTO();
			techGroupingTO.setName(row);
			techGroupings.add(techGroupingTO);
		}
		r.setTechGroupings(techGroupings);


		// Technologies enrichment
		final List<RadarTechnologyTO> technologies = new ArrayList<RadarTechnologyTO>();

		final Criteria technologiesQuery = session.createCriteria(RadarTechnology.class);
		final Criteria technologyJoinToRadar = technologiesQuery.createCriteria("radar", "radar", JoinType.INNER_JOIN);
		technologiesQuery.createAlias("technology", "technology",  JoinType.INNER_JOIN);
		final Criteria joinToRadarTechGrouping = technologiesQuery.createCriteria("radarTechGrouping", "radarTechGrouping", JoinType.INNER_JOIN);
		joinToRadarTechGrouping.createAlias("techGrouping", "techGrouping", JoinType.INNER_JOIN);
		final Criteria joinToRadarMaturity = technologiesQuery.createCriteria("radarMaturity", "radarMaturity", JoinType.INNER_JOIN);
		joinToRadarMaturity.createAlias("maturity", "maturity", JoinType.INNER_JOIN);
		technologyJoinToRadar.add(Restrictions.eq("id", id));
		technologiesQuery.setProjection(Projections.projectionList()
				.add(Projections.property("movement"))
				.add(Projections.property("techGrouping.name"))
				.add(Projections.property("maturity.name"))
				.add(Projections.property("technology.id"))
				.add(Projections.property("technology.name"))
				.add(Projections.property("technology.usageCount"))
				.add(Projections.property("technology.url"))
				.add(Projections.property("technology.description"))
				.add(Projections.property("technology.detailUrl"))
				.add(Projections.property("technology.customerStrategic")));

		for(final Object[] row : (List<Object[]>)technologiesQuery.list()) {
			final RadarTechnologyTO tech = new RadarTechnologyTO();
			tech.setMovement((MovementEnum)row[0]);
			tech.setTechGrouping((String)row[1]);
			tech.setMaturity((String)row[2]);
			tech.setId((Long)row[3]);
			tech.setTechnology((String)row[4]);
			tech.setBlipSize((Integer)row[5]);
			tech.setUrl((String)row[6]);
			tech.setDescription((String)row[7]);
			tech.setDetailUrl((String)row[8]);
			tech.setCustomerStrategic((Boolean)row[9]);
			technologies.add(tech);
		}
		r.setTechnologies(technologies);

		session.getTransaction().commit();
		session.close();

		return r;
	}

	public RadarTO createRadar(final RadarTO radarTO) throws ValidationException {
		final List<String> validations = new ArrayList<String>();

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {

			// Create new blank radar
			final Radar radar = new Radar();
			radar.setCreatedDate(new Date());
			radar.setPublished(false);
			radar.setLastPublishedDate(null);
			radar.setApproved(false);
			radar.setMajorVersion(0);
			radar.setMinorVersion(0);


			// Created by
			final User createdBy = readUser(AdminHandlerHelper.getCurrentUser(), session);
			if(createdBy!=null) {
				radar.setCreatedBy(createdBy);
				createdBy.getRadars().add(radar);
			}


			// Name
			if(!StringUtils.isBlank(radarTO.getName())) {
				radar.setName(radarTO.getName());
			} else {
				validations.add("Mandatory field 'name' has not been supplied");
			}


			// Description
			if(!StringUtils.isBlank(radarTO.getDescription())) {
				radar.setDescription(radarTO.getDescription());
			} else {
				validations.add("Mandatory field 'description' has not been supplied");
			}


			// Business Unit
			final String businessUnitName = radarTO.getBusinessUnit();
			if(!StringUtils.isBlank(businessUnitName)) {
				final BusinessUnit businessUnit = readBusinessUnit(businessUnitName, session);
				if(businessUnit!=null) {
					radar.setBusinessUnit(businessUnit);
					businessUnit.getRadars().add(radar);
				} else {
					validations.add("Unable to find business unit '" + businessUnitName + "' in tech radar");
				}
			} else {
				validations.add("Mandatory field 'business unit' has not been supplied");
			}

			final Long id = (Long)session.save(radar);
			radarTO.setId(id);
			radarTO.setCreatedBy(createdBy.getUsername());

			final List<MaturityTO> radarMaturityTOs = radarTO.getMaturities();
			if(radarMaturityTOs!=null && !radarMaturityTOs.isEmpty()) {
				if(radarMaturityTOs.size() > 0) {
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
					validations.add(radarMaturityTOs.size() + " maturities supplied, at least 1 required");
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
				
				radar.setMinorVersion(radar.getMinorVersion()+1);

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

	private BusinessUnit readBusinessUnit(final String name, final Session session) {
		final Criteria query = session.createCriteria(BusinessUnit.class);
		query.add(Restrictions.ilike("name", name));
		return (BusinessUnit)query.uniqueResult();
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


	@Override
	public RadarTO updateRadar(final RadarTO radarTO) throws ValidationException {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);
		query.add(Restrictions.eq("id", radarTO.getId()));

		final Radar radar = (Radar) query.uniqueResult();

		if(radar != null) {
			// TODO validate input
			radar.setName(radarTO.getName());
			radar.setDescription(radarTO.getDescription());
			radar.setPublished(radarTO.isPublished());
			radar.setLastPublishedDate(radarTO.getLastPublishedDate());
			radar.setApproved(radarTO.isApproved());
			radar.setMajorVersion(radarTO.getMajorVersion());
			radar.setMinorVersion(radarTO.getMinorVersion());
		} else {
			// TODO error
		}

		session.getTransaction().commit();
		session.close();

		return radarTO;
	}

}
