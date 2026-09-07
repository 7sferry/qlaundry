package com.ferry.promotion.domain.promotion;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.common.exception.PromotionNotRedeemableException;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record PromotionDomain(String id, String tenantId, PromotionCodeDomain code, String name,
                              NoteDomain description, PromotionType type, BigDecimal percentage, MoneyDomain amount,
                              MoneyDomain maxDiscountAmount, MoneyDomain minSubtotal, boolean combinable,
                              Integer usageLimit, int usedCount, Instant startAt, Instant endAt, boolean active,
                              Integer version, boolean deleted, Instant createdAt, String createdBy,
                              Instant updatedAt, String updatedBy){
	private static final BigDecimal MAX_PERCENTAGE = BigDecimal.valueOf(100L);
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100L);
	private static final int PERCENTAGE_FACTOR_SCALE = 6;
	private static final int PERCENTAGE_SCALE = 2;

	public PromotionDomain{
		if(tenantId == null || tenantId.isBlank()){
			throw new IllegalArgumentException("Tenant id must not be blank");
		}
		if(code == null){
			throw new IllegalArgumentException("Promotion code must not be null");
		}
		if(name == null || name.isBlank()){
			throw new IllegalArgumentException("Promotion name must not be blank");
		}
		if(type == null){
			throw new IllegalArgumentException("Promotion type must not be null");
		}
		if(type.isPercentageBased()){
			if(percentage == null || percentage.compareTo(BigDecimal.ZERO) <= 0
					|| percentage.compareTo(MAX_PERCENTAGE) > 0){
				throw new IllegalArgumentException("Percentage must be greater than zero and at most 100");
			}
			percentage = percentage.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_EVEN);
		}else if(amount == null || !amount.isPositive()){
			throw new IllegalArgumentException("Fixed amount must be greater than zero");
		}
		if(maxDiscountAmount != null && !maxDiscountAmount.isPositive()){
			throw new IllegalArgumentException("Maximum discount amount must be greater than zero");
		}
		if(minSubtotal != null && !minSubtotal.isPositive()){
			throw new IllegalArgumentException("Minimum subtotal must be greater than zero");
		}
		if(usageLimit != null && usageLimit <= 0){
			throw new IllegalArgumentException("Usage limit must be greater than zero");
		}
		if(usedCount < 0){
			throw new IllegalArgumentException("Used count must not be negative");
		}
		if(startAt == null){
			throw new IllegalArgumentException("Promotion start date must not be null");
		}
		if(endAt == null){
			throw new IllegalArgumentException("Promotion end date must not be null");
		}
		if(!endAt.isAfter(startAt)){
			throw new IllegalArgumentException("Promotion end date must be after its start date");
		}
	}

	public static PromotionDomain create(String tenantId, PromotionCodeDomain code, String name,
	                                     NoteDomain description, PromotionType type, BigDecimal percentage,
	                                     MoneyDomain amount, MoneyDomain maxDiscountAmount, MoneyDomain minSubtotal,
	                                     boolean combinable, Integer usageLimit, Instant startAt, Instant endAt,
	                                     String createdBy){
		Instant now = Instant.now();
		return new PromotionDomain(null, tenantId, code, name, description, type, percentage, amount,
				maxDiscountAmount, minSubtotal, combinable, usageLimit, 0, startAt, endAt, true, null, false, now,
				createdBy, now, createdBy);
	}

	public PromotionDomain update(PromotionCodeDomain code, String name, NoteDomain description, PromotionType type,
	                              BigDecimal percentage, MoneyDomain amount, MoneyDomain maxDiscountAmount,
	                              MoneyDomain minSubtotal, boolean combinable, Integer usageLimit, Instant startAt,
	                              Instant endAt, boolean active, String updatedBy){
		return toBuilder()
				.code(code)
				.name(name)
				.description(description)
				.type(type)
				.percentage(percentage)
				.amount(amount)
				.maxDiscountAmount(maxDiscountAmount)
				.minSubtotal(minSubtotal)
				.combinable(combinable)
				.usageLimit(usageLimit)
				.startAt(startAt)
				.endAt(endAt)
				.active(active)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public PromotionDomain changeActive(boolean active, String updatedBy){
		return toBuilder().active(active).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public PromotionDomain redeem(String updatedBy){
		Instant now = Instant.now();
		return toBuilder().usedCount(usedCount + 1).updatedBy(updatedBy).updatedAt(now).build();
	}

	public Optional<PromotionRejection> rejectionAt(Instant now){
		if(deleted || !active){
			return Optional.of(PromotionRejection.INACTIVE);
		}
		if(startAt != null && now.isBefore(startAt)){
			return Optional.of(PromotionRejection.NOT_STARTED);
		}
		if(endAt != null && !now.isBefore(endAt)){
			return Optional.of(PromotionRejection.EXPIRED);
		}
		if(isExhausted()){
			return Optional.of(PromotionRejection.EXHAUSTED);
		}
		return Optional.empty();
	}

	public Optional<PromotionRejection> rejectionFor(Instant now, MoneyDomain initialSubtotal,
	                                                 boolean combinedWithOthers){
		Optional<PromotionRejection> base = rejectionAt(now);
		if(base.isPresent()){
			return base;
		}
		if(combinedWithOthers && !combinable){
			return Optional.of(PromotionRejection.NOT_COMBINABLE);
		}
		if(minSubtotal != null && initialSubtotal.value().compareTo(minSubtotal.value()) < 0){
			return Optional.of(PromotionRejection.BELOW_MIN_SUBTOTAL);
		}
		return Optional.empty();
	}

	public boolean isExhausted(){
		return usageLimit != null && usedCount >= usageLimit;
	}

	public Integer remainingUsage(){
		return usageLimit == null ? null : Math.max(0, usageLimit - usedCount);
	}

	public MoneyDomain discountFor(MoneyDomain initialSubtotal, MoneyDomain discountSoFar){
		MoneyDomain basis = type == PromotionType.NON_CUMULATIVE_PERCENTAGE
				? initialSubtotal
				: new MoneyDomain(initialSubtotal.value().subtract(discountSoFar.value()).max(BigDecimal.ZERO));
		MoneyDomain raw = switch(type){
			case PERCENTAGE, NON_CUMULATIVE_PERCENTAGE -> percentageOf(basis);
			case FIXED_AMOUNT -> amount;
		};
		if(maxDiscountAmount != null){
			raw = raw.min(maxDiscountAmount);
		}
		return raw.min(basis);
	}

	private MoneyDomain percentageOf(MoneyDomain subtotal){
		BigDecimal factor = percentage.divide(HUNDRED, PERCENTAGE_FACTOR_SCALE, RoundingMode.HALF_EVEN);
		return subtotal.multiply(factor);
	}

	public String codeValue(){
		return code.value();
	}

	public String descriptionValue(){
		return description == null ? null : description.value();
	}

	public BigDecimal amountValue(){
		return amount == null ? null : amount.value();
	}

	public BigDecimal maxDiscountAmountValue(){
		return maxDiscountAmount == null ? null : maxDiscountAmount.value();
	}

	public BigDecimal minSubtotalValue(){
		return minSubtotal == null ? null : minSubtotal.value();
	}

}
