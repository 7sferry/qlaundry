package com.ferry.analytics.core.promotion;

import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.promotion.PromotionSummaryProjection;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionSummaryUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENCANAWUNGU00000";
	private static final String STAFF_ID = "01STAFFSURYANINGSIH000000";

	@Mock
	PromotionSummaryGateway gateway;
	@Mock
	PromotionSummaryPresenter presenter;
	@Captor
	ArgumentCaptor<PeriodWindow> windowCaptor;
	@Captor
	ArgumentCaptor<Instant> expiringBeforeCaptor;
	@Captor
	ArgumentCaptor<PromotionSummaryResponse> responseCaptor;

	@Test
	void givenNow_thenExpiringSoonLooksSevenDaysAheadAndRedemptionsCoverTheMonthToDate(){
		DefaultPromotionSummaryUseCase useCase = new DefaultPromotionSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-09T06:15:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Jayapura"))
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new PromotionSummaryProjection(11L, 4L, 1L, 23L)).given(gateway)
				.summarize(any(TenantId.class), any(PeriodWindow.class), any(Instant.class));

		useCase.execute(principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture(), expiringBeforeCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		PromotionSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue().from()).isEqualTo(Instant.parse("2026-09-30T15:00:00Z"));
			softly.then(windowCaptor.getValue().to()).isEqualTo(Instant.parse("2026-10-09T06:15:00Z"));
			softly.then(expiringBeforeCaptor.getValue()).isEqualTo(Instant.parse("2026-10-16T06:15:00Z"));
			softly.then(response.totalPromotions()).isEqualTo(11L);
			softly.then(response.activePromotions()).isEqualTo(4L);
			softly.then(response.expiringSoon()).isEqualTo(1L);
			softly.then(response.monthRedemptions()).isEqualTo(23L);
		});
	}

}
