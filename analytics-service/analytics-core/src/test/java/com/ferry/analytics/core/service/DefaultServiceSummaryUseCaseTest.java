package com.ferry.analytics.core.service;

import com.ferry.analytics.domain.service.ServiceSummaryProjection;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultServiceSummaryUseCaseTest{

	private static final String TENANT_ID = "01TENANTSEDAPMALAM0000000";
	private static final String STAFF_ID = "01STAFFIWANSETIAWAN000000";

	@Mock
	ServiceSummaryGateway gateway;
	@InjectMocks
	DefaultServiceSummaryUseCase useCase;
	@Mock
	ServiceSummaryPresenter presenter;
	@Captor
	ArgumentCaptor<ServiceSummaryResponse> responseCaptor;

	@Test
	void givenThreeActiveServices_thenAveragesTheirPricesToTwoDecimals(){
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(new ServiceSummaryProjection(5L, 3L, 2L, BigDecimal.valueOf(25000))).given(gateway)
				.summarize(any(TenantId.class));

		useCase.execute(principal, presenter);

		then(gateway).should()
				.summarize(new TenantId(TENANT_ID));
		then(presenter).should()
				.present(responseCaptor.capture());

		ServiceSummaryResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.totalServices()).isEqualTo(5L);
			softly.then(response.activeServices()).isEqualTo(3L);
			softly.then(response.popularServices()).isEqualTo(2L);
			softly.then(response.averagePrice()).isEqualByComparingTo("8333.33");
		});
	}

	@Test
	void givenNoActiveService_thenAveragePriceIsZero(){
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(new ServiceSummaryProjection(2L, 0L, 0L, null)).given(gateway)
				.summarize(any(TenantId.class));

		useCase.execute(principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().averagePrice()).isEqualByComparingTo("0");
			softly.then(responseCaptor.getValue().totalServices()).isEqualTo(2L);
		});
	}

}
