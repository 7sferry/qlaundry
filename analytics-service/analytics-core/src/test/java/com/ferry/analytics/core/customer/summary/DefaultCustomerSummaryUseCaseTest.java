package com.ferry.analytics.core.customer.summary;

import com.ferry.analytics.domain.customer.CustomerSummaryProjection;
import com.ferry.analytics.domain.period.MonthToDateWindow;
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
	ArgumentCaptor<MonthToDateWindow> windowCaptor;
	@Captor
	ArgumentCaptor<CustomerSummaryResponse> responseCaptor;

	@Test
	void givenThreeCustomersSpentThisMonth_thenAveragesOverThoseThreeOnly(){
		DefaultCustomerSummaryUseCase useCase = new DefaultCustomerSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-12T04:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Makassar"))
				.role(StaffRole.STAFF)
				.build();
		willReturn(new CustomerSummaryProjection(40L, 6L, BigDecimal.valueOf(500000), 3L)).given(gateway)
				.summarize(any(TenantId.class), any(MonthToDateWindow.class));

		useCase.execute(principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue().from()).isEqualTo(Instant.parse("2026-09-30T16:00:00Z"));
			softly.then(response.totalCustomers()).isEqualTo(40L);
			softly.then(response.newCustomers()).isEqualTo(6L);
			softly.then(response.monthSpend()).isEqualByComparingTo("500000");
			softly.then(response.averageSpend()).isEqualByComparingTo("166666.67");
		});
	}

	@Test
	void givenNobodyOrderedThisMonth_thenSpendAndAverageAreZero(){
		DefaultCustomerSummaryUseCase useCase = new DefaultCustomerSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-02T01:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new CustomerSummaryProjection(12L, 0L, null, 0L)).given(gateway)
				.summarize(any(TenantId.class), any(MonthToDateWindow.class));

		useCase.execute(principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().monthSpend()).isEqualByComparingTo("0");
			softly.then(responseCaptor.getValue().averageSpend()).isEqualByComparingTo("0");
			softly.then(responseCaptor.getValue().totalCustomers()).isEqualTo(12L);
		});
	}

}
