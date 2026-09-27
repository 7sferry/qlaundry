package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.DashboardWindow;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
	private static final String JAKARTA = "Asia/Jakarta";

	@Mock
	DashboardSummaryGateway gateway;
	@Mock
	DashboardSummaryPresenter presenter;
	@Captor
	ArgumentCaptor<DashboardWindow> windowCaptor;
	@Captor
	ArgumentCaptor<DashboardSummaryResponse> responseCaptor;

	@Test
	void givenLastMonthHadSales_thenComputesGrowthAgainstItInJakartaTime(){
		DefaultDashboardSummaryUseCase useCase = new DefaultDashboardSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-30T18:30:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of(JAKARTA))
				.role(StaffRole.STAFF)
				.build();
		List<StatusCountProjection> distribution = List.of(new StatusCountProjection("PENDING", 3L),
				new StatusCountProjection("COMPLETED", 41L));
		willReturn(new DashboardSummaryProjection(2L, BigDecimal.valueOf(96000), 55L, BigDecimal.valueOf(5500000),
				50L, BigDecimal.valueOf(4400000), 3L, 6L, 1L)).given(gateway)
				.summarize(any(TenantId.class), any(DashboardWindow.class));
		willReturn(distribution).given(gateway)
				.statusDistribution(any(TenantId.class), any(DashboardWindow.class));

		useCase.execute(new DashboardSummaryRequest(null), principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(gateway).should()
				.statusDistribution(eq(new TenantId(TENANT_ID)),
						eq(new DashboardWindow(LocalDate.of(2026, 10, 1), ZoneId.of(JAKARTA))));
		then(presenter).should()
				.present(responseCaptor.capture());

		DashboardWindow window = windowCaptor.getValue();
		DashboardSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(window.date()).isEqualTo(LocalDate.of(2026, 10, 1));
			softly.then(window.dayFrom()).isEqualTo(Instant.parse("2026-09-30T17:00:00Z"));
			softly.then(window.dayTo()).isEqualTo(Instant.parse("2026-10-01T17:00:00Z"));
			softly.then(window.monthFrom()).isEqualTo(Instant.parse("2026-09-30T17:00:00Z"));
			softly.then(window.monthTo()).isEqualTo(Instant.parse("2026-10-31T17:00:00Z"));
			softly.then(window.lastMonthFrom()).isEqualTo(Instant.parse("2026-08-31T17:00:00Z"));
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
				.summarize(any(TenantId.class), any(DashboardWindow.class));
		willReturn(List.of()).given(gateway)
				.statusDistribution(any(TenantId.class), any(DashboardWindow.class));

		useCase.execute(new DashboardSummaryRequest(null), principal, presenter);

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
				.summarize(any(TenantId.class), any(DashboardWindow.class));
		willReturn(List.of()).given(gateway)
				.statusDistribution(any(TenantId.class), any(DashboardWindow.class));

		useCase.execute(new DashboardSummaryRequest(null), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().revenueGrowth()).isEqualByComparingTo("-66.67");
			softly.then(responseCaptor.getValue().ordersGrowth()).isEqualByComparingTo("-33.33");
		});
	}

	@Test
	void givenAnExplicitPastDate_thenSummarizesAgainstItsOwnMonthInsteadOfToday(){
		DefaultDashboardSummaryUseCase useCase = new DefaultDashboardSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-26T02:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(new DashboardSummaryProjection(4L, BigDecimal.valueOf(150000), 20L, BigDecimal.valueOf(2000000),
				5L, BigDecimal.valueOf(400000), 2L, 3L, 0L)).given(gateway)
				.summarize(any(TenantId.class), any(DashboardWindow.class));
		willReturn(List.of()).given(gateway)
				.statusDistribution(any(TenantId.class), any(DashboardWindow.class));

		useCase.execute(new DashboardSummaryRequest(LocalDate.of(2026, 7, 15)), principal, presenter);

		then(gateway).should()
				.summarize(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(gateway).should()
				.statusDistribution(eq(new TenantId(TENANT_ID)),
						eq(new DashboardWindow(LocalDate.of(2026, 7, 15), ZoneOffset.UTC)));

		DashboardWindow window = windowCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(window.monthStart()).isEqualTo(LocalDate.of(2026, 7, 1));
			softly.then(window.dayFrom()).isEqualTo(Instant.parse("2026-07-15T00:00:00Z"));
			softly.then(window.monthTo()).isEqualTo(Instant.parse("2026-08-01T00:00:00Z"));
			softly.then(window.lastMonthFrom()).isEqualTo(Instant.parse("2026-06-01T00:00:00Z"));
		});
	}

	@Test
	void givenADateAfterToday_thenRejectsBeforeTouchingTheGateway(){
		DefaultDashboardSummaryUseCase useCase = new DefaultDashboardSummaryUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-26T02:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		assertThatThrownBy(() -> useCase.execute(
						new DashboardSummaryRequest(LocalDate.of(2026, 9, 27)), principal, presenter))
				.isInstanceOf(InvalidAnalyticStateException.class)
				.hasMessage("Dashboard date must not be in the future");

		then(gateway).shouldHaveNoInteractions();
	}

}
