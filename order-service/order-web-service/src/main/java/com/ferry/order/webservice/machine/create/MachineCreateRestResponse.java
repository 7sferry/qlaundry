package com.ferry.order.webservice.machine.create;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineCreateRestResponse(String id, String name, String type, Double capacityKg, String description,
                                        boolean active){
}
