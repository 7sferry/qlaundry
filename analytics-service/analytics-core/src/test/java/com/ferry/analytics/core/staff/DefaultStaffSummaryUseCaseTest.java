package com.ferry.analytics.core.staff;

import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.staff.StaffSummaryProjection;
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
class DefaultStaffSummaryUseCaseTest{

	private static final String TENANT_ID = "01TENANTSAKURABERSIH00000";
	private static final String STAFF_ID = "01STAFFRINAMARLINA0000000";

	@Mock
	StaffSummaryGateway gateway;
	@Mock
	StaffSummaryPresenter presenter;
	@Captor
	ArgumentCaptor<PeriodWindow> windowCaptor;
	@Captor
	ArgumentCaptor<StaffSummaryResponse> responseCaptor;

	@Test
	void givenItIsAlreadyTheFirstInJakarta_thenCountsNewStaffFromTheJakartaMonthStart(){
		DefaultStaffSummaryUseCase useCase = new DefaultStaffSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-30T19:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Jakarta"))
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new StaffSummaryProjection(7L, 2L)).given(gateway)
				.summarize(any(TenantId.class), any(PeriodWindow.class));

		useCase.execute(principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue().from()).isEqualTo(Instant.parse("2026-09-30T17:00:00Z"));
			softly.then(windowCaptor.getValue().to()).isEqualTo(Instant.parse("2026-09-30T19:00:00Z"));
			softly.then(responseCaptor.getValue().totalStaff()).isEqualTo(7L);
			softly.then(responseCaptor.getValue().newStaff()).isEqualTo(2L);
		});
	}

	@Test
	void givenNoZoneInTheToken_thenTheMonthStartsAtUtcMidnight(){
		DefaultStaffSummaryUseCase useCase = new DefaultStaffSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-15T08:30:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(new StaffSummaryProjection(3L, 0L)).given(gateway)
				.summarize(any(TenantId.class), any(PeriodWindow.class));

		useCase.execute(principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());

		thenSoftly(softly -> softly.then(windowCaptor.getValue())
				.isEqualTo(new PeriodWindow(Instant.parse("2026-10-01T00:00:00Z"),
						Instant.parse("2026-10-15T08:30:00Z"))));
	}

}
