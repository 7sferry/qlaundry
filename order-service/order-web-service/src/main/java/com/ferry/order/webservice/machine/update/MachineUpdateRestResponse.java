package com.ferry.order.webservice.machine.update;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineUpdateRestResponse(String id, String name, String type, Double capacityKg, String description,
                                        boolean active){
}
