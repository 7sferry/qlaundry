package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.OrderItemDomain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private OrderJpaEntity order;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "order_id", insertable = false, updatable = false)
	private String orderId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private ClothingTypeJpaEntity type;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "type_id", insertable = false, updatable = false)
	private short typeId;
	@Column(length = 100)
	private String label;
	@Column(nullable = false)
	private int quantity;
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

	public void setOrder(OrderJpaEntity order){
		this.order = order;
		this.orderId = order.getId();
	}

	public void setType(ClothingTypeJpaEntity type){
		this.type = type;
		this.typeId = type.getId();
	}

	public static OrderItemJpaEntity construct(String id, OrderItemDomain item, OrderJpaEntity order,
	                                           ClothingTypeJpaEntity type){
		OrderItemJpaEntity entity = new OrderItemJpaEntity();
		entity.id = id;
		entity.order = order;
		entity.orderId = order.getId();
		entity.type = type;
		entity.typeId = type.getId();
		entity.label = item.label();
		entity.quantity = item.quantity();
		entity.createdBy = item.createdBy();
		entity.createdAt = item.createdAt();
		entity.updatedBy = item.updatedBy();
		entity.updatedAt = item.updatedAt();
		entity.deleted = item.deleted();
		entity.version = item.version();
		return entity;
	}

	public static OrderItemDomain construct(OrderItemJpaEntity saved){
		return new OrderItemDomain(saved.id, saved.orderId, ClothingType.fromValue(saved.typeId).orElseThrow(),
				saved.label, saved.quantity, saved.version, saved.deleted, saved.createdAt, saved.createdBy,
				saved.updatedAt, saved.updatedBy);
	}

}
