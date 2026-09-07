package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.InvalidUsernameException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record UsernameDomain(String value) {

	private static final String ALLOWED_PATTERN = "^[a-zA-Z0-9_]+$";

	public UsernameDomain {
		if (value == null || value.isBlank()) {
			throw new InvalidUsernameException("Username must not be blank");
		}
		value = value.toLowerCase().trim();
		if (value.length() < 5) {
			throw new InvalidUsernameException("Username must be at least 5 characters long");
		}
		value = value.toLowerCase().trim();
		if (!value.matches(ALLOWED_PATTERN)) {
			throw new InvalidUsernameException("Username must only contain letters, numbers and underscore");
		}
	}

}
