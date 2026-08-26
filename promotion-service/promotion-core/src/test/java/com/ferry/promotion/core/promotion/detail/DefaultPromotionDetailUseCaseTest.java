package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionDetailUseCaseTest{

	private static final String TENANT_ID = "01TENANTFLAMBOYAN00000000";
	private static final String STAFF_ID = "01STAFFHENDRAWIJAYA000000";
	private static final String PROMOTION_ID = "01PROMOPELANGGANBARU00000";

	@Mock
	PromotionDetailGateway gateway;
	@InjectMocks
	DefaultPromotionDetailUseCase useCase;
	@Mock
	PromotionDetailPresenter presenter;
	@Captor
	ArgumentCaptor<PromotionDetailResponse> responseCaptor;

	@Test
	void givenBlankPromotionId_thenThrowsConstraintViolationException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionDetailRequest request = new PromotionDetailRequest("  ");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenAPromotionOfAnotherTenant_thenThrowsNotFoundException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionDetailRequest request = new PromotionDetailRequest(PROMOTION_ID);
		willReturn(Optional.empty()).given(gateway)
				.findById(any(PromotionIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Promotion Not Found"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenAKnownPromotion_thenPresentsItScopedToTheCallersTenant(){
		Instant now = Instant.now();
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionDomain promotion = PromotionDomain.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("PELANGGANBARU"))
				.name("Diskon Pelanggan Baru")
				.description(new NoteDomain("first order only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(40.0d)
				.maxDiscountAmount(MoneyDomain.of(25000L))
				.combinable(true)
				.usageLimit(500)
				.usedCount(123)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionDetailRequest request = new PromotionDetailRequest(PROMOTION_ID);
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionIdDomain.class), any(TenantIdDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findById(eq(new PromotionIdDomain(PROMOTION_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(presenter).should()
				.present(responseCaptor.capture());

		PromotionDomain presented = responseCaptor.getValue().promotion();

		thenSoftly(softly -> {
			softly.then(presented.codeValue()).isEqualTo("PELANGGANBARU");
			softly.then(presented.remainingUsage()).isEqualTo(377);
			softly.then(presented.discountFor(MoneyDomain.of(100000L), MoneyDomain.ZERO).value())
					.isEqualByComparingTo(new BigDecimal("25000.00"));
		});
	}

}
