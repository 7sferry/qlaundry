package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.customer.TopCustomerProjection;
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
import java.time.ZoneOffset;
import java.util.List;

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
	ArgumentCaptor<MonthToDateWindow> windowCaptor;
	@Captor
	ArgumentCaptor<TopCustomerResponse> responseCaptor;

	@Test
	void givenThisMonthsSpenders_thenAsksForTheTopFiveAndPassesThemThroughInOrder(){
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
				.top(any(TenantId.class), any(MonthToDateWindow.class), anyInt());

		useCase.execute(principal, presenter);

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

}
