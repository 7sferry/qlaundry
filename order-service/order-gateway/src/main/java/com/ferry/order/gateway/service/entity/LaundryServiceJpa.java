package com.ferry.order.gateway.service.entity;

import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import jakarta.persistence.*;
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
public class LaundryServiceJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, length = 100)
	private String name;
	@Column
	private String description;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal pricePerUnit;
	@Column(nullable = false)
	private short unitId;
	@Column(nullable = false)
	private short categoryId;
	@Column(nullable = false)
	private int estimatedHours;
	@Column
	private Integer expressHours;
	@Column
	private Double expressMultiplier;
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

	public static LaundryServiceJpa construct(String id, LaundryService service){
		LaundryServiceJpa entity = new LaundryServiceJpa();
		entity.setId(id);
		entity.setTenantId(service.tenantId());
		entity.setName(service.name());
		entity.setDescription(service.descriptionValue());
		entity.setPricePerUnit(service.pricePerUnit().value());
		entity.setUnitId(service.unit().getValue());
		entity.setCategoryId(service.category().getValue());
		entity.setEstimatedHours(service.estimatedHours());
		entity.setExpressHours(service.expressHours());
		entity.setExpressMultiplier(service.expressMultiplier());
		entity.setPopular(service.popular());
		entity.setActive(service.active());
		entity.setCreatedBy(service.createdBy());
		entity.setCreatedAt(service.createdAt());
		entity.setUpdatedBy(service.updatedBy());
		entity.setUpdatedAt(service.updatedAt());
		entity.setDeleted(service.deleted());
		entity.setVersion(service.version());
		return entity;
	}

	public static LaundryService construct(LaundryServiceJpa saved){
		return new LaundryService(saved.getId(), saved.getTenantId(), saved.getName(), new Note(saved.getDescription()),
				new Money(saved.getPricePerUnit()), ServiceUnit.fromValue(saved.getUnitId()).orElseThrow(),
				ServiceCategory.fromValue(saved.getCategoryId()).orElseThrow(), saved.getEstimatedHours(),
				saved.getExpressHours(), saved.getExpressMultiplier(), saved.isPopular(), saved.isActive(),
				saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(),
				saved.getUpdatedBy());
	}

}
