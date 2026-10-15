package com.ferry.order.domain.service;

import com.ferry.order.domain.common.Decimals;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.order.OrderPriority;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record LaundryService(String id, String tenantId, String name, Note description,
                                   Money pricePerUnit, ServiceUnit unit, ServiceCategory category,
                                   int estimatedHours, Integer expressHours, Double expressMultiplier,
                                   boolean popular, boolean active, Integer version, boolean deleted, Instant createdAt, String createdBy,
                                   Instant updatedAt, String updatedBy){
	private static final double MIN_EXPRESS_MULTIPLIER = 1.0d;

	public LaundryService{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidOrderStateException("Tenant id must not be blank");
		}
		if(name == null || name.isBlank()){
			throw new InvalidOrderStateException("Service name must not be blank");
		}
		if(pricePerUnit == null || unit == null || category == null){
			throw new InvalidOrderStateException("Price, unit and category must not be null");
		}
		if(estimatedHours <= 0){
			throw new InvalidOrderStateException("Estimated hours must be greater than zero");
		}
		if((expressHours == null) != (expressMultiplier == null)){
			throw new InvalidOrderStateException("Express hours and express multiplier must be set together");
		}
		if(expressHours != null && (expressHours <= 0 || expressHours > estimatedHours)){
			throw new InvalidOrderStateException("Express hours must be between 1 and the estimated hours");
		}
		if(expressMultiplier != null && expressMultiplier < MIN_EXPRESS_MULTIPLIER){
			throw new InvalidOrderStateException("Express multiplier must be at least 1");
		}
		expressMultiplier = Decimals.scaled(expressMultiplier);
	}

	public static LaundryService create(String tenantId, String name, Note description,
	                                          Money pricePerUnit, ServiceUnit unit, ServiceCategory category,
	                                          int estimatedHours, Integer expressHours, Double expressMultiplier,
	                                          boolean popular, String createdBy){
		Instant now = Instant.now();
		return new LaundryService(null, tenantId, name, description, pricePerUnit, unit, category,
				estimatedHours, expressHours, expressMultiplier, popular, true, null, false, now, createdBy, now, createdBy);
	}

	public LaundryService update(String name, Note description, Money pricePerUnit, ServiceUnit unit,
	                                   ServiceCategory category, int estimatedHours, Integer expressHours,
	                                   Double expressMultiplier, boolean popular, boolean active, String updatedBy){
		return toBuilder()
				.name(name)
				.description(description)
				.pricePerUnit(pricePerUnit)
				.unit(unit)
				.category(category)
				.estimatedHours(estimatedHours)
				.expressHours(expressHours)
				.expressMultiplier(expressMultiplier)
				.popular(popular)
				.active(active)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public LaundryService markDeleted(String updatedBy){
		return toBuilder().deleted(true).active(false).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public boolean offersExpress(){
		return expressHours != null;
	}

	public String descriptionValue(){
		return description == null ? null : description.value();
	}

	public Money priceFor(int quantity, Double weightKg, OrderPriority priority){
		BigDecimal units = unit.isWeighed()
				? BigDecimal.valueOf(weight(weightKg)) : BigDecimal.valueOf(quantity);
		BigDecimal multiplier = express(priority) ? BigDecimal.valueOf(expressMultiplier) : BigDecimal.ONE;
		return pricePerUnit.multiply(units).multiply(multiplier);
	}

	private double weight(Double weightKg){
		if(weightKg == null || weightKg <= 0){
			throw new InvalidOrderStateException("Weight in kg is required for a per-kg service");
		}
		return Decimals.scaled(weightKg.doubleValue());
	}

	public Instant estimatedDeliveryFrom(Instant pickupAt, OrderPriority priority){
		int hours = express(priority) ? expressHours : estimatedHours;
		return pickupAt.plusSeconds(hours * 3600L);
	}

	private boolean express(OrderPriority priority){
		if(priority != OrderPriority.EXPRESS){
			return false;
		}
		if(!offersExpress()){
			throw new InvalidOrderStateException("This service does not offer express");
		}
		return true;
	}

}
