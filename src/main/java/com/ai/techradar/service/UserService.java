package com.ai.techradar.service;

import java.io.Serializable;


public interface UserService {

	UserInfo getUserInfo(String uid) throws UserDoesNotExistException, ServiceContactFailedException;

	public static class UserInfo implements Serializable {

		private static final long serialVersionUID = 4663205958450558806L;

		private String givenName;

		private String surname;

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(final String givenName) {
			this.givenName = givenName;
		}

		public String getSurname() {
			return surname;
		}

		public void setSurname(final String surname) {
			this.surname = surname;
		}

	}
	
	public static class UserDoesNotExistException extends RuntimeException {
		private static final long serialVersionUID = 6294480916486411873L;
		public UserDoesNotExistException() {
			super();
		}
		public UserDoesNotExistException(final String reason) {
			super(reason);
		}
		public UserDoesNotExistException(final Throwable th) {
			super(th);
		}
	}
	
	public static class ServiceContactFailedException extends RuntimeException {
		private static final long serialVersionUID = -6769420654013405574L;
		public ServiceContactFailedException() {
			super();
		}
		public ServiceContactFailedException(final String reason) {
			super(reason);
		}
		public ServiceContactFailedException(final Throwable th) {
			super(th);
		}
	}

}
