package com.ai.techradar.camel;

public class TechnologyReceiverBean {

	public void doSomething(final String body) {
		System.out.println("TechnologyReceiverBean has received a message. Message Body: ");
		System.out.println(body);
	}

}
