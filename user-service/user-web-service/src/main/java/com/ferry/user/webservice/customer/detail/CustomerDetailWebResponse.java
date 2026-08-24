package com.ferry.user.webservice.customer.detail;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerDetailWebResponse(String id, String fullName, String phone, String email, String address,
                                        String notes, long joinedAt){
}
