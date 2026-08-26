package com.ferry.promotion.gateway.promotion.entity;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionType;
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
@EqualsAndHashCode(of = {"tenantId", "code"})
@Entity
@Table(name = "promotions",
		uniqueConstraints = @UniqueConstraint(name = "idx_promotions_tenant_code",
				columnNames = {"tenant_id", "code"}))
public class PromotionJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, length = 32)
	private String code;
	@Column(nullable = false, length = 100)
	private String name;
	@Column
	private String description;
	@Column(nullable = false)
	private short typeId;
	@Column(precision = 5, scale = MoneyDomain.SCALE)
	private Double percentage;
	@Column(precision = 19, scale = MoneyDomain.SCALE)
	private BigDecimal amount;
	@Column(precision = 19, scale = MoneyDomain.SCALE)
	private BigDecimal maxDiscountAmount;
	@Column(precision = 19, scale = MoneyDomain.SCALE)
	private BigDecimal minSubtotal;
	@Column(nullable = false)
	private boolean combinable;
	@Column
	private Integer usageLimit;
	@Column(nullable = false)
	private int usedCount;
	@Column
	private Instant startAt;
	@Column
	private Instant endAt;
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

	public static PromotionJpaEntity construct(String id, PromotionDomain promotion){
		PromotionJpaEntity entity = new PromotionJpaEntity();
		entity.id = id;
		entity.tenantId = promotion.tenantId();
		entity.code = promotion.codeValue();
		entity.name = promotion.name();
		entity.description = promotion.descriptionValue();
		entity.typeId = promotion.type().getValue();
		entity.percentage = promotion.percentage();
		entity.amount = promotion.amountValue();
		entity.maxDiscountAmount = promotion.maxDiscountAmountValue();
		entity.minSubtotal = promotion.minSubtotalValue();
		entity.combinable = promotion.combinable();
		entity.usageLimit = promotion.usageLimit();
		entity.usedCount = promotion.usedCount();
		entity.startAt = promotion.startAt();
		entity.endAt = promotion.endAt();
		entity.active = promotion.active();
		entity.createdBy = promotion.createdBy();
		entity.createdAt = promotion.createdAt();
		entity.updatedBy = promotion.updatedBy();
		entity.updatedAt = promotion.updatedAt();
		entity.deleted = promotion.deleted();
		entity.version = promotion.version();
		return entity;
	}

	public static PromotionDomain construct(PromotionJpaEntity saved){
		return new PromotionDomain(saved.id, saved.tenantId, new PromotionCodeDomain(saved.code), saved.name,
				new NoteDomain(saved.description), PromotionType.fromValue(saved.typeId).orElseThrow(),
				saved.percentage, saved.amount == null ? null : new MoneyDomain(saved.amount),
				saved.maxDiscountAmount == null ? null : new MoneyDomain(saved.maxDiscountAmount),
				saved.minSubtotal == null ? null : new MoneyDomain(saved.minSubtotal), saved.combinable,
				saved.usageLimit, saved.usedCount, saved.startAt, saved.endAt, saved.active, saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
