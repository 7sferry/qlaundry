package com.ferry.user.domain;

import com.ferry.user.domain.exception.InvalidUsernameException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record UsernameDomain(String value) {

	public UsernameDomain {
		if (value == null || value.isBlank()) {
			throw new InvalidUsernameException("Username must not be blank");
		}
		if (value.length() < 5) {
			throw new InvalidUsernameException("Username must be at least 5 characters long");
		}
		value = value.trim();
	}

}
