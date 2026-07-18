package com.ferry.user.webservice.staff.list;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffListWebResponse(List<Staff> staffs){

	public record Staff(String description, String fullName, long createdAt, String username,
	                    List<Email> emails, List<Phone> phones, List<Address> addresses){

	}

	public record Email(String email){

	}

	public record Phone(String phone){

	}

	public record Address(String address){

	}

}
