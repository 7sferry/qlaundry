package com.ferry.order.gateway.service.entity;

import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "laundry_services", indexes = @Index(name = "idx_laundry_services_tenant_id", columnList = "tenant_id"))
public class LaundryServiceJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, length = 100)
	private String name;
	@Column
	private String description;
	@Column(nullable = false, precision = 19, scale = MoneyDomain.SCALE)
	private BigDecimal pricePerUnit;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private ServiceUnitJpaEntity unit;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "unit_id", insertable = false, updatable = false)
	private short unitId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private ServiceCategoryJpaEntity category;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "category_id", insertable = false, updatable = false)
	private short categoryId;
	@Column(nullable = false)
	private int estimatedHours;
	@Column(nullable = false)
	private double expressMultiplier;
	@Column(nullable = false)
	private boolean popular;
	@Column(nullable = false)
	private boolean active;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	public void setUnit(ServiceUnitJpaEntity unit){
		this.unit = unit;
		this.unitId = unit.getId();
	}

	public void setCategory(ServiceCategoryJpaEntity category){
		this.category = category;
		this.categoryId = category.getId();
	}

	public static LaundryServiceJpaEntity construct(String id, LaundryServiceDomain service, ServiceUnitJpaEntity unit,
	                                                ServiceCategoryJpaEntity category){
		LaundryServiceJpaEntity entity = new LaundryServiceJpaEntity();
		entity.id = id;
		entity.tenantId = service.tenantId();
		entity.name = service.name();
		entity.description = service.descriptionValue();
		entity.pricePerUnit = service.pricePerUnit().value();
		entity.unit = unit;
		entity.unitId = unit.getId();
		entity.category = category;
		entity.categoryId = category.getId();
		entity.estimatedHours = service.estimatedHours();
		entity.expressMultiplier = service.expressMultiplier();
		entity.popular = service.popular();
		entity.active = service.active();
		entity.createdBy = service.createdBy();
		entity.createdAt = service.createdAt();
		entity.updatedBy = service.updatedBy();
		entity.updatedAt = service.updatedAt();
		entity.deleted = service.deleted();
		entity.version = service.version();
		return entity;
	}

	public static LaundryServiceDomain construct(LaundryServiceJpaEntity saved){
		return new LaundryServiceDomain(saved.id, saved.tenantId, saved.name, new NoteDomain(saved.description),
				new MoneyDomain(saved.pricePerUnit), ServiceUnit.fromValue(saved.unitId).orElseThrow(),
				ServiceCategory.fromValue(saved.categoryId).orElseThrow(), saved.estimatedHours,
				saved.expressMultiplier, saved.popular, saved.active, saved.version, saved.deleted, saved.createdAt,
				saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
