package com.ferry.order.webservice.order.confirm;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderConfirmWebResponse(String id, String orderNumber, String status, String staffNotes,
                                      Long completedAt, long updatedAt){
}
