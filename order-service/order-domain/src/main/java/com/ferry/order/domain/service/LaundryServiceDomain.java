package com.ferry.order.domain.service;

import com.ferry.order.domain.common.Decimals;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.order.OrderPriority;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record LaundryServiceDomain(String id, String tenantId, String name, NoteDomain description,
                                   MoneyDomain pricePerUnit, ServiceUnit unit, ServiceCategory category,
                                   int estimatedHours, double expressMultiplier, boolean popular, boolean active,
                                   Integer version, boolean deleted, Instant createdAt, String createdBy,
                                   Instant updatedAt, String updatedBy){
	private static final double MIN_EXPRESS_MULTIPLIER = 1.0d;

	public LaundryServiceDomain{
		if(tenantId == null || tenantId.isBlank()){
			throw new IllegalArgumentException("Tenant id must not be blank");
		}
		if(name == null || name.isBlank()){
			throw new IllegalArgumentException("Service name must not be blank");
		}
		if(pricePerUnit == null || unit == null || category == null){
			throw new IllegalArgumentException("Price, unit and category must not be null");
		}
		if(estimatedHours <= 0){
			throw new IllegalArgumentException("Estimated hours must be greater than zero");
		}
		if(expressMultiplier < MIN_EXPRESS_MULTIPLIER){
			throw new IllegalArgumentException("Express multiplier must be at least 1");
		}
		expressMultiplier = Decimals.scaled(expressMultiplier);
	}

	public static LaundryServiceDomain create(String tenantId, String name, NoteDomain description,
	                                          MoneyDomain pricePerUnit, ServiceUnit unit, ServiceCategory category,
	                                          int estimatedHours, double expressMultiplier, boolean popular,
	                                          String createdBy){
		Instant now = Instant.now();
		return new LaundryServiceDomain(null, tenantId, name, description, pricePerUnit, unit, category,
				estimatedHours, expressMultiplier, popular, true, null, false, now, createdBy, now, createdBy);
	}

	public LaundryServiceDomain update(String name, NoteDomain description, MoneyDomain pricePerUnit, ServiceUnit unit,
	                                   ServiceCategory category, int estimatedHours, double expressMultiplier,
	                                   boolean popular, boolean active, String updatedBy){
		return toBuilder()
				.name(name)
				.description(description)
				.pricePerUnit(pricePerUnit)
				.unit(unit)
				.category(category)
				.estimatedHours(estimatedHours)
				.expressMultiplier(expressMultiplier)
				.popular(popular)
				.active(active)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public LaundryServiceDomain markDeleted(String updatedBy){
		return toBuilder().deleted(true).active(false).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public String descriptionValue(){
		return description == null ? null : description.value();
	}

	public MoneyDomain priceFor(int quantity, Double weightKg, OrderPriority priority){
		BigDecimal units = unit.isWeighed()
				? BigDecimal.valueOf(weight(weightKg)) : BigDecimal.valueOf(quantity);
		BigDecimal multiplier = priority == OrderPriority.EXPRESS
				? BigDecimal.valueOf(expressMultiplier) : BigDecimal.ONE;
		return pricePerUnit.multiply(units).multiply(multiplier);
	}

	private double weight(Double weightKg){
		if(weightKg == null || weightKg <= 0){
			throw new IllegalArgumentException("Weight in kg is required for a per-kg service");
		}
		return Decimals.scaled(weightKg.doubleValue());
	}

	public Instant estimatedDeliveryFrom(Instant pickupAt){
		return pickupAt.plusSeconds(estimatedHours * 3600L);
	}

}
