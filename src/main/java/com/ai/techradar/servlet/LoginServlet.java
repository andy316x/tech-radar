package com.ai.techradar.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.User;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.web.service.to.RadarTO;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 592021468711272272L;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		final String username = request.getUserPrincipal().getName();
		
		response.getWriter().append("{\"name\":\"" + username + "\"}");
		
		
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(User.class);
		query.add(Restrictions.eq("username", username));
		
		final User user = (User)query.uniqueResult();
		
		if(user==null) {
			// This is the first time the user has logged 
			// in, so create a record for them
			final User newUser = new User();
			newUser.setUsername(username);
			session.persist(newUser);
		}

		session.getTransaction().commit();
		session.close();
		
		response.flushBuffer();
	}

}
