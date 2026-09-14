package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.tenant.TenantIdDomain;
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultDashboardSummaryUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENANGALAUNDRY000";
	private static final String STAFF_ID = "01STAFFBAYUSAPUTRA0000000";

	@Mock
	DashboardSummaryGateway gateway;
	@Mock
	DashboardSummaryPresenter presenter;
	@Captor
	ArgumentCaptor<DashboardSummaryResponse> responseCaptor;

	@Test
	void givenLastMonthHadSales_thenComputesGrowthAgainstItInJakartaTime(){
		DefaultDashboardSummaryUseCase useCase = new DefaultDashboardSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-30T18:30:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		List<StatusCountProjection> distribution = List.of(new StatusCountProjection("PENDING", 3L),
				new StatusCountProjection("COMPLETED", 41L));
		willReturn(new DashboardSummaryProjection(2L, BigDecimal.valueOf(96000), 55L, BigDecimal.valueOf(5500000),
				50L, BigDecimal.valueOf(4400000), 3L, 6L, 1L)).given(gateway)
				.summarize(any(TenantIdDomain.class), any(LocalDate.class), any(LocalDate.class),
						any(LocalDate.class));
		willReturn(distribution).given(gateway)
				.statusDistribution(any(TenantIdDomain.class), any(LocalDate.class), any(LocalDate.class));

		useCase.execute(new DashboardSummaryRequest(), principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantIdDomain(TENANT_ID)), eq(LocalDate.of(2026, 10, 1)),
						eq(LocalDate.of(2026, 10, 1)), eq(LocalDate.of(2026, 9, 1)));
		then(gateway).should()
				.statusDistribution(eq(new TenantIdDomain(TENANT_ID)), eq(LocalDate.of(2026, 10, 1)),
						eq(LocalDate.of(2026, 11, 1)));
		then(presenter).should()
				.present(responseCaptor.capture());

		DashboardSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.revenueGrowth()).isEqualByComparingTo("25.00");
			softly.then(response.ordersGrowth()).isEqualByComparingTo("10.00");
			softly.then(response.todayOrders()).isEqualTo(2L);
			softly.then(response.monthRevenue()).isEqualByComparingTo("5500000");
			softly.then(response.inProgressOrders()).isEqualTo(6L);
			softly.then(response.statusDistribution()).isEqualTo(distribution);
		});
	}

	@Test
	void givenNothingSoldLastMonth_thenGrowthIsZeroInsteadOfDividingByZero(){
		DefaultDashboardSummaryUseCase useCase = new DefaultDashboardSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T03:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new DashboardSummaryProjection(1L, BigDecimal.valueOf(24000), 9L, BigDecimal.valueOf(310000),
				0L, BigDecimal.ZERO, 0L, 2L, 0L)).given(gateway)
				.summarize(any(TenantIdDomain.class), any(LocalDate.class), any(LocalDate.class),
						any(LocalDate.class));
		willReturn(List.of()).given(gateway)
				.statusDistribution(any(TenantIdDomain.class), any(LocalDate.class), any(LocalDate.class));

		useCase.execute(new DashboardSummaryRequest(), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		DashboardSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.revenueGrowth()).isEqualByComparingTo("0");
			softly.then(response.ordersGrowth()).isEqualByComparingTo("0");
			softly.then(response.monthOrders()).isEqualTo(9L);
			softly.then(response.statusDistribution()).isEmpty();
		});
	}

	@Test
	void givenFallingSales_thenGrowthIsNegative(){
		DefaultDashboardSummaryUseCase useCase = new DefaultDashboardSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-20T10:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(new DashboardSummaryProjection(0L, BigDecimal.ZERO, 2L, BigDecimal.valueOf(100000), 3L,
				BigDecimal.valueOf(300000), 1L, 0L, 0L)).given(gateway)
				.summarize(any(TenantIdDomain.class), any(LocalDate.class), any(LocalDate.class),
						any(LocalDate.class));
		willReturn(List.of()).given(gateway)
				.statusDistribution(any(TenantIdDomain.class), any(LocalDate.class), any(LocalDate.class));

		useCase.execute(new DashboardSummaryRequest(), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().revenueGrowth()).isEqualByComparingTo("-66.67");
			softly.then(responseCaptor.getValue().ordersGrowth()).isEqualByComparingTo("-33.33");
		});
	}

}
