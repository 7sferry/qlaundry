package com.ferry.order.webservice.order.cancel;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderCancelWebResponse(String id, String orderNumber, String status, String staffNotes,
                                     Long completedAt, long updatedAt){
}
