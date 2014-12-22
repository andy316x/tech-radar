package com.ai.techradar.service.impl;

import com.ai.techradar.service.UserService;

public class StubUserService implements UserService {

	public UserInfo getUserInfo(final String uid) throws UserDoesNotExistException, ServiceContactFailedException {

		if(uid != null && !uid.trim().isEmpty()) {
			final String[] names = uid.trim().split(" ");

			final UserInfo user = new UserInfo();

			user.setGivenName(names[0].trim());

			if(names.length > 1) {
				final StringBuilder strBuilder = new StringBuilder();
				for(int i = 1; i < names.length; i++) {
					strBuilder.append(names[i].trim());
					if(i < names.length-1) {
						strBuilder.append(" ");
					}
				}
				user.setSurname(strBuilder.toString());
			}

			return user;
		}

		return null;
	}

}
