package com.ai.techradar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.SkillLevelEnum;
import com.ai.techradar.database.entities.TechGrouping;
import com.ai.techradar.database.entities.UserTechnology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.MeService;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.UserTechnologyTO;

@SuppressWarnings("unchecked")
public class MeServiceImpl implements MeService {

	public List<UserTechnologyTO> getMySkillLevels() {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		final List<UserTechnologyTO> skillLevels = new ArrayList<UserTechnologyTO>();
		
		final String userId = AdminHandlerHelper.getCurrentUser();
		
		if(userId != null) {
			
			final Criteria query = session.createCriteria(UserTechnology.class);

			final Criteria joinToUser = query.createCriteria("user", "user");
			joinToUser.add(Restrictions.eq("username", userId));
			
			query.createAlias("technology", "technology");

			query.setProjection(Projections.projectionList()
					.add(Projections.property("user.username"))
					.add(Projections.property("technology.name"))
					.add(Projections.property("skillLevel"))
					.add(Projections.property("technology.techGrouping"))
					);
			
			for(final Object[] row : (List<Object[]>)query.list()) {
				final UserTechnologyTO ut = new UserTechnologyTO();
				ut.setUser((String)row[0]);
				ut.setTechnology((String)row[1]);
				ut.setSkillLevel((SkillLevelEnum)row[2]);
				ut.setTechGroup(((TechGrouping)row[3]).getName()); 

				skillLevels.add(ut);
			}
			
		} else {
			// TODO validate
		}

		session.getTransaction().commit();
		session.close();

		return skillLevels;
	}

}
