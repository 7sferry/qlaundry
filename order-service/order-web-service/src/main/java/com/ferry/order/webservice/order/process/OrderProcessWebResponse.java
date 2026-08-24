package com.ferry.order.webservice.order.process;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderProcessWebResponse(String id, String orderNumber, String status, String staffNotes,
                                      Long completedAt, long updatedAt){
}
