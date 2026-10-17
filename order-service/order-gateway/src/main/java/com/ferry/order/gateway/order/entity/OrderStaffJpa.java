package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.order.OrderStaff;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "order_staffs",
		uniqueConstraints = @UniqueConstraint(name = "uq_order_staffs_order_id_staff_id",
				columnNames = {"order_id", "staff_id"}),
		indexes = @Index(name = "idx_order_staffs_staff_id", columnList = "staff_id"))
public class OrderStaffJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private OrderJpa order;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "order_id", insertable = false, updatable = false)
	private String orderId;
	@Column(nullable = false, length = 50)
	private String staffId;
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

	public void setOrder(OrderJpa order){
		this.order = order;
		this.orderId = order.getId();
	}

	public static OrderStaffJpa construct(String id, OrderStaff staff, OrderJpa order){		OrderStaffJpa entity = new OrderStaffJpa();
		entity.setId(id);
		entity.setOrder(order);
		entity.setStaffId(staff.staffId());
		entity.setCreatedBy(staff.createdBy());
		entity.setCreatedAt(staff.createdAt());
		entity.setUpdatedBy(staff.updatedBy());
		entity.setUpdatedAt(staff.updatedAt());
		entity.setDeleted(staff.deleted());
		entity.setVersion(staff.version());
		return entity;
	}

	public static OrderStaff construct(OrderStaffJpa saved){
		return new OrderStaff(saved.getId(), saved.getOrderId(), saved.getStaffId(), saved.getVersion(),
				saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(),
				saved.getUpdatedBy());
	}

}
