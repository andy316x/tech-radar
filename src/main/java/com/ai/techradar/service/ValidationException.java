package com.ai.techradar.service;

import java.util.List;

public class ValidationException extends Exception {

	private static final long serialVersionUID = -6218122506846371767L;

	private final List<String> validations;

	public ValidationException(final List<String> validations) {
		this.validations = validations;
	}

	public List<String> getValidations() {
		return validations;
	}

}
