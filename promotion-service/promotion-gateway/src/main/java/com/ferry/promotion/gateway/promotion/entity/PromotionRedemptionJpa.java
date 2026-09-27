package com.ferry.promotion.gateway.promotion.entity;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
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
@EqualsAndHashCode(of = {"tenantId", "referenceId", "code"})
@Entity
@Table(name = "promotion_redemptions",
		uniqueConstraints = @UniqueConstraint(name = "uq_promotion_redemptions_tenant_reference_code",
				columnNames = {"tenant_id", "reference_id", "code"}),
		indexes = @Index(name = "idx_promotion_redemptions_promotion_id", columnList = "promotion_id"))
public class PromotionRedemptionJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private PromotionJpa promotion;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "promotion_id", insertable = false, updatable = false)
	private String promotionId;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, length = 32)
	private String code;
	@Column(nullable = false, name = "reference_id", length = 50)
	private String referenceId;
	@Column(length = 50)
	private String customerId;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal subtotal;
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

	public void setPromotion(PromotionJpa promotion){
		this.promotion = promotion;
		this.promotionId = promotion.getId();
	}

	public static PromotionRedemptionJpa construct(String id, PromotionRedemption redemption,
	                                                     PromotionJpa promotion){
		PromotionRedemptionJpa entity = new PromotionRedemptionJpa();
		entity.id = id;
		entity.promotion = promotion;
		entity.promotionId = promotion.getId();
		entity.tenantId = redemption.tenantId();
		entity.code = redemption.codeValue();
		entity.referenceId = redemption.referenceId();
		entity.customerId = redemption.customerId();
		entity.subtotal = redemption.subtotal().value();
		entity.discountAmount = redemption.discountAmount().value();
		entity.createdBy = redemption.createdBy();
		entity.createdAt = redemption.createdAt();
		entity.updatedBy = redemption.updatedBy();
		entity.updatedAt = redemption.updatedAt();
		entity.deleted = redemption.deleted();
		entity.version = redemption.version();
		return entity;
	}

	public static PromotionRedemption construct(PromotionRedemptionJpa saved){
		return new PromotionRedemption(saved.id, saved.promotionId, saved.tenantId,
				new PromotionCode(saved.code), saved.referenceId, saved.customerId,
				new Money(saved.subtotal), new Money(saved.discountAmount), saved.version, saved.deleted,
				saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
