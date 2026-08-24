package com.ferry.order.webservice.order.complete;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderCompleteWebResponse(String id, String orderNumber, String status, String staffNotes,
                                       Long completedAt, long updatedAt){
}
