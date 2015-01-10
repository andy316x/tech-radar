package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.RadarTechnology;
import com.ai.techradar.database.entities.SkillLevelEnum;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.entities.User;
import com.ai.techradar.database.entities.UserTechnology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.TechnologyService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.ai.techradar.web.service.to.UserTechnologyTO;

@SuppressWarnings("unchecked")
public class TechnologyServiceImpl implements TechnologyService {

	public List<TechnologyTO> getTechnologies() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Technology.class);

		query.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("name"))
				.add(Projections.property("description"))
				);

		final List<TechnologyTO> ts = new ArrayList<TechnologyTO>();
		for(final Object[] row : (List<Object[]>)query.list()) {
			final TechnologyTO t = new TechnologyTO();
			t.setId((Long)row[0]);
			t.setName((String)row[1]);
			t.setDescription((String)row[2]);

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

	public UserTechnologyTO setUserTechnology(final Long technologyId, final UserTechnologyTO userTechnology) throws ValidationException {
		final List<String> validations = new ArrayList<String>();

		final String username = AdminHandlerHelper.getCurrentUser();

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {

			final UserTechnology userTechnologyEntity = readTechnologyRating(technologyId, username, session);

			if(userTechnologyEntity == null) {

				if(userTechnology.getSkillLevel() != null) {
					final UserTechnology newUserTechnology = new UserTechnology();
					newUserTechnology.setSkillLevel(userTechnology.getSkillLevel());

					// Find and set technology
					final Technology technology = readTechnology(technologyId, session);
					if(technology!=null) {
						newUserTechnology.setTechnology(technology);
						technology.getUsers().add(newUserTechnology);
						userTechnology.setTechnology(technology.getName());
					} else {
						validations.add("Could not find technology with ID " + technologyId);
					}

					// Find and set user
					if(!StringUtils.isBlank(username)) {
						final User user = readUser(AdminHandlerHelper.getCurrentUser(), session);
						if(user != null) {
							newUserTechnology.setUser(user);
							user.getTechnologies().add(newUserTechnology);
							userTechnology.setUser(user.getUsername());
						} else {
							validations.add("Could not find user with username " + AdminHandlerHelper.getCurrentUser());
						}
					} else {
						validations.add("User must be logged in to rate technology");
					}

					session.persist(newUserTechnology);

				} else {
					// Nothing to do?
				}

			} else {

				if(userTechnology.getSkillLevel() != null) {
					userTechnologyEntity.setSkillLevel(userTechnology.getSkillLevel());
				} else {
					session.delete(userTechnologyEntity);
				}

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

		return userTechnology;
	}

	public List<UserTechnologyTO> getTechnologyUsers(final Long technologyId) throws ValidationException {
		final List<UserTechnologyTO> results = new ArrayList<UserTechnologyTO>();

		final Session session = HibernateUtil.getSessionFactory().openSession();

		try {

			final Criteria query = session.createCriteria(Technology.class);
			query.add(Restrictions.eq("id", technologyId));

			// Join out to user technology
			final Criteria joinToUserTechnology = query.createCriteria("users", "technologyUser", JoinType.INNER_JOIN);

			// Join out to user
			joinToUserTechnology.createAlias("user", "user", JoinType.INNER_JOIN);

			query.setProjection(Projections.projectionList()
					.add(Projections.property("user.username"))
					.add(Projections.property("name"))
					.add(Projections.property("technologyUser.skillLevel"))
					);

			for(final Object[] row : (List<Object[]>)query.list()) {
				final UserTechnologyTO userTechnologyTO = new UserTechnologyTO();
				userTechnologyTO.setUser((String)row[0]);
				userTechnologyTO.setTechnology((String)row[1]);
				userTechnologyTO.setSkillLevel((SkillLevelEnum)row[2]);

				results.add(userTechnologyTO);
			}

		} finally {
			session.close();
		}

		return results;
	}

	public List<RadarTechnologyTO> getTechnologyRadars(final Long technologyId) throws ValidationException {
		final List<RadarTechnologyTO> results = new ArrayList<RadarTechnologyTO>();

		final Session session = HibernateUtil.getSessionFactory().openSession();

		try {

			final Criteria query = session.createCriteria(RadarTechnology.class);

			// Join out to technology
			final Criteria joinToRadarTechnology = query.createCriteria("technology", "technology", JoinType.INNER_JOIN);

			// Join out to radar
			query.createAlias("radar", "radar", JoinType.INNER_JOIN);

			// Join out to user who added the technology
			query.createCriteria("addedBy", "addedBy", JoinType.INNER_JOIN);

			// Restrict to technologies with the input ID
			joinToRadarTechnology.add(Restrictions.eq("id", technologyId));
			
			// Project to only columns that are needed (keep Hibernate efficient)
			query.setProjection(Projections.projectionList()
					.add(Projections.property("addedDate"))
					.add(Projections.property("addedBy.username"))
					
					.add(Projections.property("radar.id"))
					.add(Projections.property("radar.name"))
					);

			for(final Object[] row : (List<Object[]>)query.list()) {
				final RadarTechnologyTO radarTechnologyTO = new RadarTechnologyTO();

				radarTechnologyTO.setAddedDate((Date)row[0]);
				radarTechnologyTO.setAddedByUid((String)row[1]);

				radarTechnologyTO.setRadarId((Long)row[2]);
				radarTechnologyTO.setRadarName((String)row[3]);

				results.add(radarTechnologyTO);
			}

		} finally {
			session.close();
		}

		return results;
	}

	private UserTechnology readTechnologyRating(final Long id, final String username, final Session session) {
		final Criteria query = session.createCriteria(UserTechnology.class);

		query.createAlias("technology", "technology");
		query.createAlias("user", "user");

		query.add(Restrictions.eq("technology.id", id));
		query.add(Restrictions.eq("user.username", username));

		return (UserTechnology)query.uniqueResult();
	}

	private Technology readTechnology(final Long id, final Session session) {
		final Criteria query = session.createCriteria(Technology.class);
		query.add(Restrictions.eq("id", id));
		return (Technology)query.uniqueResult();
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
