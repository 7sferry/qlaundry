package com.ferry.order.webservice.order.ready;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderReadyWebResponse(String id, String orderNumber, String status, String staffNotes,
                                    Long completedAt, long updatedAt){
}
