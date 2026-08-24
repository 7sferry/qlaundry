package com.ferry.order.webservice.order.pickup;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderPickupWebResponse(String id, String orderNumber, String status, String staffNotes,
                                     Long completedAt, long updatedAt){
}
