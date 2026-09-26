package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.common.Note;
import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.promotion.PromotionType;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionToggleUseCaseTest{

	private static final String TENANT_ID = "01TENANTSERUNI0000000000";
	private static final String STAFF_ID = "01STAFFMAYAKUSUMA0000000";
	private static final String PROMOTION_ID = "01PROMOCASHBACKSABTU0000";

	@Mock
	PromotionToggleGateway gateway;
	@InjectMocks
	DefaultPromotionToggleUseCase useCase;
	@Mock
	PromotionTogglePresenter presenter;
	@Captor
	ArgumentCaptor<Promotion> promotionCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionToggleRequest request = new PromotionToggleRequest(PROMOTION_ID, false);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(PromotionForbiddenActionException.class)
				.hasMessage("Only super staff can manage promotions"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenNoActiveFlag_thenThrowsConstraintViolationException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionToggleRequest request = new PromotionToggleRequest(PROMOTION_ID, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenUnknownPromotion_thenThrowsNotFoundException(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		PromotionToggleRequest request = new PromotionToggleRequest(PROMOTION_ID, false);
		willReturn(Optional.empty()).given(gateway)
				.findById(any(PromotionId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Promotion Not Found"));

		then(gateway).should(never())
				.save(any(Promotion.class));
	}

	@Test
	void givenAnActivePromotionSwitchedOff_thenItStopsBeingRedeemableButKeepsItsUsage(){
		Instant now = Instant.now();
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("CASHBACKSABTU"))
				.name("Cashback Sabtu")
				.description(new Note("saturday only"))
				.type(PromotionType.FIXED_AMOUNT)
				.amount(Money.of(7500L))
				.usageLimit(60)
				.usedCount(19)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionToggleRequest request = new PromotionToggleRequest(PROMOTION_ID, false);
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<Promotion>getArgument(0)).given(gateway)
				.save(any(Promotion.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(promotionCaptor.capture());
		then(presenter).should()
				.present(any(PromotionToggleResponse.class));

		Promotion saved = promotionCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.active()).isFalse();
			softly.then(saved.deleted()).isFalse();
			softly.then(saved.usedCount()).isEqualTo(19);
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
			softly.then(saved.rejectionAt(Instant.now())).contains(PromotionRejection.INACTIVE);
		});
	}

	@Test
	void givenAPausedPromotionSwitchedBackOn_thenItBecomesRedeemableAgain(){
		Instant now = Instant.now();
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Promotion promotion = Promotion.builder()
				.id(PROMOTION_ID)
				.tenantId(TENANT_ID)
				.code(new PromotionCode("CASHBACKSABTU"))
				.name("Cashback Sabtu")
				.description(new Note("saturday only"))
				.type(PromotionType.FIXED_AMOUNT)
				.amount(Money.of(7500L))
				.usageLimit(60)
				.usedCount(19)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(false)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionToggleRequest request = new PromotionToggleRequest(PROMOTION_ID, true);
		willReturn(Optional.of(promotion)).given(gateway)
				.findById(any(PromotionId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<Promotion>getArgument(0)).given(gateway)
				.save(any(Promotion.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(promotionCaptor.capture());

		Promotion saved = promotionCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.active()).isTrue();
			softly.then(saved.usedCount()).isEqualTo(19);
			softly.then(saved.rejectionAt(Instant.now())).isEmpty();
		});
	}

}
