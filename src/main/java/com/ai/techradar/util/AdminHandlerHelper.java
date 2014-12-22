package com.ai.techradar.util;


public class AdminHandlerHelper {

	// Thread local variable containing each thread's user
	private static final ThreadLocal<String> threadId = new ThreadLocal<String>() {
		private String user;
		@Override
		protected String initialValue() {
			return null;
		}
		@Override
		public String get() {
			return user;
		}
		@Override
		public void set(final String user) {
			this.user = user;
		}
		@Override
		public void remove() {
			this.user = null;
		}
	};

	public static void login(final String user) {
		threadId.set(user);
	}
	
	public static String getCurrentUser() {
		return threadId.get();
	}

	public static void logout() {
		threadId.remove();
	}

}
