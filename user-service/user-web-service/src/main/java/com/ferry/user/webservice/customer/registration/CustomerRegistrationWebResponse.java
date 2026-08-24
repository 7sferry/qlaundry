package com.ferry.user.webservice.customer.registration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerRegistrationWebResponse(String id, String fullName, String phone, String email, String address,
                                              String notes, long joinedAt){
}
