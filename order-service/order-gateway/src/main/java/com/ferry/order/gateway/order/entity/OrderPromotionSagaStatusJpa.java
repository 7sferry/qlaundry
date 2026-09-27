package com.ferry.order.gateway.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "order_promotion_saga_statuses")
public class OrderPromotionSagaStatusJpa{
	@Id
	private short id;

	@Column(nullable = false, length = 25)
	private String name;

	@JoinColumn(name = "statusId")
	@OneToMany(fetch = FetchType.LAZY)
	private Set<OrderPromotionSagaJpa> sagas;
}
