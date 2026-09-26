package com.ferry.order.webservice.order.deliver;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderDeliverRestResponse(
	String id,
	String orderNumber,
	String status,
	String staffNotes,
	Long completedAt,
	long updatedAt){
}
