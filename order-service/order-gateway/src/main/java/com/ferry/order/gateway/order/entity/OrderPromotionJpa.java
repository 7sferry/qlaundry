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
@Table(name = "order_promotions",
		indexes = @Index(name = "idx_order_promotions_promotion_id", columnList = "promotion_id"),
		uniqueConstraints = @UniqueConstraint(name = "uq_order_promotions_order_id_promotion_id",
				columnNames = {"order_id", "promotion_id"})
)
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
		entity.setId(id);
		entity.setOrder(order);
		entity.setPromotionId(promotion.promotionId());
		entity.setCode(promotion.code());
		entity.setDiscountAmount(promotion.discountAmount().value());
		entity.setCreatedBy(promotion.createdBy());
		entity.setCreatedAt(promotion.createdAt());
		entity.setUpdatedBy(promotion.updatedBy());
		entity.setUpdatedAt(promotion.updatedAt());
		entity.setDeleted(promotion.deleted());
		entity.setVersion(promotion.version());
		return entity;
	}

	public static OrderPromotion construct(OrderPromotionJpa saved){
		return new OrderPromotion(saved.getId(), saved.getOrderId(), saved.getPromotionId(), saved.getCode(),
				new Money(saved.getDiscountAmount()), saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(),
				saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
