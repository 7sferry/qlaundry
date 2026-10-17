package com.ferry.promotion.gateway.promotion.entity;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.common.Note;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
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
public class PromotionJpa{

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
	@Column(precision = 5, scale = Money.SCALE)
	private BigDecimal percentage;
	@Column(precision = 19, scale = Money.SCALE)
	private BigDecimal amount;
	@Column(precision = 19, scale = Money.SCALE)
	private BigDecimal maxDiscountAmount;
	@Column(precision = 19, scale = Money.SCALE)
	private BigDecimal minSubtotal;
	@Column(nullable = false)
	private boolean combinable;
	@Column
	private Integer usageLimit;
	@Column(nullable = false)
	private int usedCount;
	@Column(nullable = false)
	private Instant startAt;
	@Column(nullable = false)
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

	public static PromotionJpa construct(String id, Promotion promotion){
		PromotionJpa entity = new PromotionJpa();
		entity.setId(id);
		entity.setTenantId(promotion.tenantId());
		entity.setCode(promotion.codeValue());
		entity.setName(promotion.name());
		entity.setDescription(promotion.descriptionValue());
		entity.setTypeId(promotion.type().getValue());
		entity.setPercentage(promotion.percentage());
		entity.setAmount(promotion.amountValue());
		entity.setMaxDiscountAmount(promotion.maxDiscountAmountValue());
		entity.setMinSubtotal(promotion.minSubtotalValue());
		entity.setCombinable(promotion.combinable());
		entity.setUsageLimit(promotion.usageLimit());
		entity.setUsedCount(promotion.usedCount());
		entity.setStartAt(promotion.startAt());
		entity.setEndAt(promotion.endAt());
		entity.setActive(promotion.active());
		entity.setCreatedBy(promotion.createdBy());
		entity.setCreatedAt(promotion.createdAt());
		entity.setUpdatedBy(promotion.updatedBy());
		entity.setUpdatedAt(promotion.updatedAt());
		entity.setDeleted(promotion.deleted());
		entity.setVersion(promotion.version());
		return entity;
	}

	public static Promotion construct(PromotionJpa saved){
		return new Promotion(saved.getId(), saved.getTenantId(), new PromotionCode(saved.getCode()), saved.getName(),
				new Note(saved.getDescription()), PromotionType.fromValue(saved.getTypeId()), saved.getPercentage(),
				saved.getAmount() == null ? null : new Money(saved.getAmount()),
				saved.getMaxDiscountAmount() == null ? null : new Money(saved.getMaxDiscountAmount()),
				saved.getMinSubtotal() == null ? null : new Money(saved.getMinSubtotal()), saved.isCombinable(),
				saved.getUsageLimit(), saved.getUsedCount(), saved.getStartAt(), saved.getEndAt(), saved.isActive(),
				saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(),
				saved.getUpdatedBy());
	}

}
