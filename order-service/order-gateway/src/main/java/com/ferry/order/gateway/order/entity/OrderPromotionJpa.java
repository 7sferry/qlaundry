package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.order.OrderPromotion;
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
@Table(name = "order_promotions", indexes = {
		@Index(name = "idx_order_promotions_order_id", columnList = "order_id"),
		@Index(name = "idx_order_promotions_promotion_id", columnList = "promotion_id")
})
public class OrderPromotionJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private OrderJpa order;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "order_id", insertable = false, updatable = false)
	private String orderId;
	@Column(nullable = false, name = "promotion_id", length = 50)
	private String promotionId;
	@Column(nullable = false, length = 32)
	private String code;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal discountAmount;
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

	public static OrderPromotionJpa construct(String id, OrderPromotion promotion, OrderJpa order){
		OrderPromotionJpa entity = new OrderPromotionJpa();
		entity.id = id;
		entity.order = order;
		entity.orderId = order.getId();
		entity.promotionId = promotion.promotionId();
		entity.code = promotion.code();
		entity.discountAmount = promotion.discountAmount().value();
		entity.createdBy = promotion.createdBy();
		entity.createdAt = promotion.createdAt();
		entity.updatedBy = promotion.updatedBy();
		entity.updatedAt = promotion.updatedAt();
		entity.deleted = promotion.deleted();
		entity.version = promotion.version();
		return entity;
	}

	public static OrderPromotion construct(OrderPromotionJpa saved){
		return new OrderPromotion(saved.id, saved.orderId, saved.promotionId, saved.code,
				new Money(saved.discountAmount), saved.version, saved.deleted, saved.createdAt, saved.createdBy,
				saved.updatedAt, saved.updatedBy);
	}

}
