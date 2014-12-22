package com.ai.techradar.service.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import com.ai.techradar.service.UserService;

@SuppressWarnings("unchecked")
public class WindowsServerUserService implements UserService {

	// Constants
	private static final String LDAP_CTX_FACTORY_CLASS = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String UID_ATTR_ID = "uid";
	private static final String SURNAME_ATTR_ID = "sn";
	private static final String GIVEN_NAME_ATTR_ID = "givenName";

	// Instance variables
	private final String ldapConnection; //e.g. ldap://localhost:389
	private final String rootDn; //e.g. cn=users,dc=Andys-MacBook-Pro,dc=local

	public WindowsServerUserService(final String ldapConnection, final String rootDn) {
		this.ldapConnection = ldapConnection;
		this.rootDn = rootDn;
	}

	@SuppressWarnings("rawtypes")
	public UserInfo getUserInfo(final String uid) throws UserDoesNotExistException, ServiceContactFailedException {

		try {

			final Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY_CLASS);
			env.put(Context.PROVIDER_URL, ldapConnection);
			final DirContext ctx = new InitialDirContext(env);

			final Attributes matchAttrs = new BasicAttributes(true);
			matchAttrs.put(new BasicAttribute(UID_ATTR_ID, uid));
			
			// Search for objects with these matching attributes
			final NamingEnumeration results = ctx.search(rootDn, matchAttrs);
			while(results.hasMore()) {
				final SearchResult searchResult = (SearchResult)results.next();

				final String givenName = (String)searchResult.getAttributes().get(GIVEN_NAME_ATTR_ID).get();
				final String surname = (String)searchResult.getAttributes().get(SURNAME_ATTR_ID).get();

				final UserInfo user = new UserInfo();
				user.setGivenName(givenName);
				user.setSurname(surname);
				return user;
			}
			
			throw new UserDoesNotExistException("Failed to find user with UID " + uid);

		} catch (final NamingException e) {
			throw new ServiceContactFailedException(e);
		}

	}

}
