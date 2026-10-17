package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.customer.TopCustomerProjection;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultTopCustomerUseCaseTest{

	private static final String TENANT_ID = "01TENANTTERATAIEMAS000000";
	private static final String STAFF_ID = "01STAFFYUSUFHAKIM00000000";

	@Mock
	TopCustomerGateway gateway;
	@Mock
	TopCustomerPresenter presenter;
	@Captor
	ArgumentCaptor<PeriodWindow> windowCaptor;
	@Captor
	ArgumentCaptor<TopCustomerResponse> responseCaptor;

	@Test
	void givenNoPeriod_thenAsksForThisMonthsTopFiveAndPassesThemThroughInOrder(){
		DefaultTopCustomerUseCase useCase = new DefaultTopCustomerUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-20T09:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		List<TopCustomerProjection> top = List.of(
				new TopCustomerProjection("01CUSTOMERMEGAWATI0000000", 4L, BigDecimal.valueOf(320000)),
				new TopCustomerProjection("01CUSTOMERFAJARSIDIK00000", 2L, BigDecimal.valueOf(95000)));
		willReturn(top).given(gateway)
				.top(any(TenantId.class), any(PeriodWindow.class), anyInt());

		useCase.execute(new TopCustomerRequest(null), principal, presenter);

		then(gateway).should()
				.top(eq(new TenantId(TENANT_ID)), windowCaptor.capture(), eq(5));
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue().from()).isEqualTo(Instant.parse("2026-10-01T00:00:00Z"));
			softly.then(windowCaptor.getValue().to()).isEqualTo(Instant.parse("2026-10-20T09:00:00Z"));
			softly.then(responseCaptor.getValue().customers()).isEqualTo(top);
		});
	}

	@Test
	void givenNonMemberOrders_thenAddsThemAsOneRowRankedBySpend(){
		DefaultTopCustomerUseCase useCase = new DefaultTopCustomerUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-12T04:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		TopCustomerProjection bigMember = new TopCustomerProjection("01CUSTOMERHENDRAWAN00000", 6L,
				BigDecimal.valueOf(540000));
		TopCustomerProjection smallMember = new TopCustomerProjection("01CUSTOMERLINDASARI00000", 1L,
				BigDecimal.valueOf(30000));
		TopCustomerProjection nonMember = new TopCustomerProjection(null, 9L, BigDecimal.valueOf(210000));
		willReturn(List.of(bigMember, smallMember)).given(gateway)
				.top(any(TenantId.class), any(PeriodWindow.class), anyInt());
		willReturn(Optional.of(nonMember)).given(gateway)
				.nonMember(any(TenantId.class), any(PeriodWindow.class));

		useCase.execute(new TopCustomerRequest(ReportPeriod.WEEK), principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().customers())
				.containsExactly(bigMember, nonMember, smallMember));
	}

	@Test
	void givenYearPeriodInJakarta_thenStartsElevenMonthsBeforeThisMonth(){
		DefaultTopCustomerUseCase useCase = new DefaultTopCustomerUseCase(gateway,
				Clock.fixed(Instant.parse("2026-10-20T09:00:00Z"), ZoneOffset.UTC));
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Jakarta"))
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(List.of()).given(gateway)
				.top(any(TenantId.class), any(PeriodWindow.class), anyInt());

		useCase.execute(new TopCustomerRequest(ReportPeriod.YEAR), principal, presenter);

		then(gateway).should()
				.top(eq(new TenantId(TENANT_ID)), windowCaptor.capture(), eq(5));
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(windowCaptor.getValue().from()).isEqualTo(Instant.parse("2025-10-31T17:00:00Z"));
			softly.then(windowCaptor.getValue().to()).isEqualTo(Instant.parse("2026-10-20T09:00:00Z"));
			softly.then(responseCaptor.getValue().customers()).isEmpty();
		});
	}

}
