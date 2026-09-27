package com.ferry.user.webservice.staff.update;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffUpdateRestResponse(
	String description,
	String fullName,
	long createdAt,
	String username,
	String role,
	List<Email> emails,
	List<Phone> phones,
	List<Address> addresses){

	public record Email(String email){

	}

	public record Phone(String phone){

	}

	public record Address(String address){

	}

}
