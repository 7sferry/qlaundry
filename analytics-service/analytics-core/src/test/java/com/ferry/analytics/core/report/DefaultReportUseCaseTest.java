package com.ferry.analytics.core.report;

import com.ferry.analytics.core.report.ReportResponse.ServiceShare;
import com.ferry.analytics.core.report.ReportResponse.TrendPoint;
import com.ferry.analytics.domain.report.ReportBucket;
import com.ferry.analytics.domain.report.ReportPeriod;
import com.ferry.analytics.domain.report.ReportWindow;
import com.ferry.analytics.domain.report.RevenueBucketProjection;
import com.ferry.analytics.domain.report.ServiceBreakdownProjection;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
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
class DefaultReportUseCaseTest{

	private static final String TENANT_ID = "01TENANTANGGREKLAUNDRY000";
	private static final String STAFF_ID = "01STAFFNADIAKUSUMA0000000";

	@Mock
	ReportGateway gateway;
	@Mock
	ReportPresenter presenter;
	@Captor
	ArgumentCaptor<ReportWindow> windowCaptor;
	@Captor
	ArgumentCaptor<ReportResponse> responseCaptor;

	@Test
	void givenNoPeriod_thenThrowsConstraintViolationException(){
		DefaultReportUseCase useCase = new DefaultReportUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T05:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new ReportRequest(null), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenWeekPeriod_thenFillsTheSevenDaysEndingTodayWithZerosWhereThereWereNoOrders(){
		DefaultReportUseCase useCase = new DefaultReportUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-13T20:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Jakarta"))
				.role(StaffRole.STAFF)
				.build();
		willReturn(List.of(new RevenueBucketProjection(LocalDate.of(2026, 9, 9), BigDecimal.valueOf(120000), 3L),
				new RevenueBucketProjection(LocalDate.of(2026, 9, 14), BigDecimal.valueOf(45000), 1L))).given(gateway)
				.revenueTrend(any(TenantId.class), any(ReportWindow.class));
		willReturn(List.of()).given(gateway)
				.serviceBreakdown(any(TenantId.class), any(ReportWindow.class));

		useCase.execute(new ReportRequest(ReportPeriod.WEEK), principal, presenter);

		then(gateway).should()
				.revenueTrend(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		ReportWindow window = windowCaptor.getValue();
		List<TrendPoint> trend = responseCaptor.getValue().revenueTrend();

		thenSoftly(softly -> {
			softly.then(window.from()).isEqualTo(LocalDate.of(2026, 9, 8));
			softly.then(window.toExclusive()).isEqualTo(LocalDate.of(2026, 9, 15));
			softly.then(window.zone()).isEqualTo(ZoneId.of("Asia/Jakarta"));
			softly.then(window.fromInstant()).isEqualTo(Instant.parse("2026-09-07T17:00:00Z"));
			softly.then(window.toExclusiveInstant()).isEqualTo(Instant.parse("2026-09-14T17:00:00Z"));
			softly.then(window.bucket()).isEqualTo(ReportBucket.DAY);
			softly.then(trend).hasSize(7);
			softly.then(trend.getFirst().label()).isEqualTo("Tue 08");
			softly.then(trend.getFirst().orders()).isZero();
			softly.then(trend.get(1).revenue()).isEqualByComparingTo("120000");
			softly.then(trend.getLast().label()).isEqualTo("Mon 14");
			softly.then(trend.getLast().orders()).isEqualTo(1L);
			softly.then(responseCaptor.getValue().period()).isEqualTo(ReportPeriod.WEEK);
		});
	}

	@Test
	void givenYearPeriod_thenReturnsTwelveMonthlyBucketsEndingThisMonth(){
		DefaultReportUseCase useCase = new DefaultReportUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T05:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(List.of(new RevenueBucketProjection(LocalDate.of(2026, 9, 1), BigDecimal.valueOf(8740000), 93L)))
				.given(gateway)
				.revenueTrend(any(TenantId.class), any(ReportWindow.class));
		willReturn(List.of()).given(gateway)
				.serviceBreakdown(any(TenantId.class), any(ReportWindow.class));

		useCase.execute(new ReportRequest(ReportPeriod.YEAR), principal, presenter);

		then(gateway).should()
				.serviceBreakdown(eq(new TenantId(TENANT_ID)), windowCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		ReportWindow window = windowCaptor.getValue();
		List<TrendPoint> trend = responseCaptor.getValue().revenueTrend();

		thenSoftly(softly -> {
			softly.then(window.zone()).isEqualTo(ZoneOffset.UTC);
			softly.then(window.fromInstant()).isEqualTo(Instant.parse("2025-10-01T00:00:00Z"));
			softly.then(window.toExclusiveInstant()).isEqualTo(Instant.parse("2026-10-01T00:00:00Z"));
			softly.then(trend).hasSize(12);
			softly.then(trend.getFirst().bucketStart()).isEqualTo(LocalDate.of(2025, 10, 1));
			softly.then(trend.getFirst().label()).isEqualTo("Oct");
			softly.then(trend.getLast().label()).isEqualTo("Sep");
			softly.then(trend.getLast().orders()).isEqualTo(93L);
		});
	}

	@Test
	void givenQuarterPeriod_thenBucketsByWeekStartingOnAMonday(){
		DefaultReportUseCase useCase = new DefaultReportUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T05:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(List.of()).given(gateway)
				.revenueTrend(any(TenantId.class), any(ReportWindow.class));
		willReturn(List.of()).given(gateway)
				.serviceBreakdown(any(TenantId.class), any(ReportWindow.class));

		useCase.execute(new ReportRequest(ReportPeriod.QUARTER), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		List<TrendPoint> trend = responseCaptor.getValue().revenueTrend();

		thenSoftly(softly -> {
			softly.then(trend.getFirst().bucketStart()).isEqualTo(LocalDate.of(2026, 6, 8));
			softly.then(trend.getLast().bucketStart()).isEqualTo(LocalDate.of(2026, 9, 14));
			softly.then(trend).hasSize(15);
			softly.then(trend).extracting(TrendPoint::orders).containsOnly(0L);
		});
	}

	@Test
	void givenServiceBreakdown_thenComputesEachServiceShareOfRevenue(){
		DefaultReportUseCase useCase = new DefaultReportUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T05:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(List.of()).given(gateway)
				.revenueTrend(any(TenantId.class), any(ReportWindow.class));
		willReturn(List.of(
				new ServiceBreakdownProjection("01SERVICECUCIKERING000000", "Cuci Kering", 12L,
						BigDecimal.valueOf(200000)),
				new ServiceBreakdownProjection("01SERVICECUCISETRIKA00000", "Cuci Setrika", 5L,
						BigDecimal.valueOf(100000)))).given(gateway)
				.serviceBreakdown(any(TenantId.class), any(ReportWindow.class));

		useCase.execute(new ReportRequest(ReportPeriod.MONTH), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		ReportResponse response = responseCaptor.getValue();
		List<ServiceShare> breakdown = response.serviceBreakdown();

		thenSoftly(softly -> {
			softly.then(response.revenueTrend()).hasSize(30);
			softly.then(response.revenueTrend().getFirst().label()).isEqualTo("01 Sep");
			softly.then(breakdown.getFirst().percentage()).isEqualByComparingTo("66.67");
			softly.then(breakdown.getLast().percentage()).isEqualByComparingTo("33.33");
			softly.then(breakdown.getLast().count()).isEqualTo(5L);
		});
	}

}
