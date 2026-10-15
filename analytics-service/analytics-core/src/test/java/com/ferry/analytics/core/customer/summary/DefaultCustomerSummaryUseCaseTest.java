package com.ferry.analytics.core.customer.summary;

import com.ferry.analytics.domain.customer.CustomerSummaryProjection;
import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.report.ReportPeriod;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class DefaultCustomerSummaryUseCaseTest{

	private static final String TENANT_ID = "01TENANTKAMBOJAINDAH00000";
	private static final String STAFF_ID = "01STAFFHENDRAWIJAYA000000";

	@Mock
	CustomerSummaryGateway gateway;
	@Mock
	CustomerSummaryPresenter presenter;
	@Captor
	ArgumentCaptor<PeriodWindow> windowCaptor;
	@Captor
	ArgumentCaptor<CustomerSummaryResponse> responseCaptor;

	@Test
	void givenNoPeriodAndThreeCustomersSpentThisMonth_thenUsesTheMonthToDateAndAveragesOverThoseThree(){
		DefaultCustomerSummaryUseCase useCase = new DefaultCustomerSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-12T04:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Makassar"))
				.role(StaffRole.STAFF)
				.build();
		willReturn(new CustomerSummaryProjection(40L, 6L, BigDecimal.valueOf(500000), 3L)).given(gateway)
				.summarize(any(TenantId.class), any(PeriodWindow.class));

		useCase.execute(new CustomerSummaryRequest(null), principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue().from()).isEqualTo(Instant.parse("2026-09-30T16:00:00Z"));
			softly.then(windowCaptor.getValue().to()).isEqualTo(Instant.parse("2026-10-12T04:00:00Z"));
			softly.then(response.totalCustomers()).isEqualTo(40L);
			softly.then(response.newCustomers()).isEqualTo(6L);
			softly.then(response.spend()).isEqualByComparingTo("500000");
			softly.then(response.averageSpend()).isEqualByComparingTo("166666.67");
		});
	}

	@Test
	void givenWeekPeriod_thenCountsFromSixDaysAgoUntilNow(){
		DefaultCustomerSummaryUseCase useCase = new DefaultCustomerSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-14T09:30:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new CustomerSummaryProjection(41L, 2L, BigDecimal.valueOf(90000), 2L)).given(gateway)
				.summarize(any(TenantId.class), any(PeriodWindow.class));

		useCase.execute(new CustomerSummaryRequest(ReportPeriod.WEEK), principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue())
					.isEqualTo(new PeriodWindow(Instant.parse("2026-10-08T00:00:00Z"),
							Instant.parse("2026-10-14T09:30:00Z")));
			softly.then(responseCaptor.getValue().newCustomers()).isEqualTo(2L);
			softly.then(responseCaptor.getValue().averageSpend()).isEqualByComparingTo("45000");
		});
	}

	@Test
	void givenNobodyOrderedInThePeriod_thenSpendAndAverageAreZero(){
		DefaultCustomerSummaryUseCase useCase = new DefaultCustomerSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-02T01:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new CustomerSummaryProjection(12L, 0L, null, 0L)).given(gateway)
				.summarize(any(TenantId.class), any(PeriodWindow.class));

		useCase.execute(new CustomerSummaryRequest(ReportPeriod.MONTH), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().spend()).isEqualByComparingTo("0");
			softly.then(responseCaptor.getValue().averageSpend()).isEqualByComparingTo("0");
			softly.then(responseCaptor.getValue().totalCustomers()).isEqualTo(12L);
		});
	}

}
