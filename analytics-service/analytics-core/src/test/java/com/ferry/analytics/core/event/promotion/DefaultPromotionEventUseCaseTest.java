package com.ferry.analytics.core.event.promotion;

import com.ferry.analytics.core.event.promotion.PromotionEventRequest.PromotionEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.PromotionSnapshot;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTANGGREKPUTIH00000";
	private static final String PROMOTION_ID = "01PROMOMERDEKA45000000000";
	private static final String SECOND_PROMOTION_ID = "01PROMOGAJIAN000000000000";

	@Mock
	PromotionEventGateway gateway;
	@InjectMocks
	DefaultPromotionEventUseCase useCase;
	@Mock
	PromotionEventPresenter presenter;
	@Captor
	ArgumentCaptor<List<PromotionSnapshot>> promotionsCaptor;
	@Captor
	ArgumentCaptor<List<ConsumedEvent>> consumedCaptor;
	@Captor
	ArgumentCaptor<PromotionEventResponse> responseCaptor;

	@Test
	void givenNoEventList_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new PromotionEventRequest(null), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenCreatedAndToggledPromotions_thenUpsertsBothAndRecordsConsumedEvents(){
		Instant now = Instant.now();
		PromotionSnapshot merdeka = PromotionSnapshot.builder()
				.tenantId(TENANT_ID)
				.promotionId(PROMOTION_ID)
				.code("MERDEKA45")
				.name("Diskon Merdeka")
				.type("CUMULATIVE_PERCENTAGE")
				.combinable(true)
				.startAt(now)
				.endAt(now.plusSeconds(2592000L))
				.active(true)
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();
		PromotionSnapshot pausedGajian = PromotionSnapshot.builder()
				.tenantId(TENANT_ID)
				.promotionId(SECOND_PROMOTION_ID)
				.code("GAJIAN")
				.name("Promo Gajian")
				.type("FIXED_AMOUNT")
				.usageLimit(50)
				.startAt(now.minusSeconds(86400L))
				.endAt(now.plusSeconds(172800L))
				.active(false)
				.version(3)
				.createdAt(now.minusSeconds(86400L))
				.updatedAt(now)
				.build();

		useCase.execute(new PromotionEventRequest(List.of(
				new PromotionEvent("01EVENTMERDEKABARU0000000", "PROMOTION_CREATED", merdeka),
				new PromotionEvent("01EVENTGAJIANJEDA00000000", "PROMOTION_TOGGLED", pausedGajian))), presenter);

		then(gateway).should()
				.upsert(promotionsCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<ConsumedEvent> consumed = consumedCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(promotionsCaptor.getValue()).containsExactly(merdeka, pausedGajian);
			softly.then(consumed).extracting(ConsumedEvent::aggregate).containsOnly(AnalyticsAggregate.PROMOTION);
			softly.then(consumed).extracting(ConsumedEvent::type)
					.containsExactly("PROMOTION_CREATED", "PROMOTION_TOGGLED");
			softly.then(responseCaptor.getValue().applied()).extracting(a -> a.promotionId())
					.containsExactly(PROMOTION_ID, SECOND_PROMOTION_ID);
			softly.then(responseCaptor.getValue().rejected()).isEmpty();
		});
	}

	@Test
	void givenEveryEventIsInvalid_thenNeverWrites(){
		Instant now = Instant.now();
		PromotionSnapshot merdeka = PromotionSnapshot.builder()
				.tenantId(TENANT_ID)
				.promotionId(PROMOTION_ID)
				.code("MERDEKA45")
				.name("Diskon Merdeka")
				.type("CUMULATIVE_PERCENTAGE")
				.startAt(now)
				.endAt(now.plusSeconds(3600L))
				.active(true)
				.version(1)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new PromotionEventRequest(List.of(
				new PromotionEvent("", "PROMOTION_UPDATED", merdeka))), presenter);

		then(gateway).should(never())
				.upsert(anyList());
		then(gateway).should(never())
				.recordConsumed(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().applied()).isEmpty();
			softly.then(responseCaptor.getValue().rejected()).hasSize(1);
		});
	}

}
