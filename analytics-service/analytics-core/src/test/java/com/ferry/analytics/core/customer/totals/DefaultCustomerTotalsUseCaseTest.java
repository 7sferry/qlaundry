package com.ferry.analytics.core.customer.totals;

import com.ferry.analytics.domain.customer.CustomerTotalsProjection;
import com.ferry.analytics.domain.staff.StaffRole;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerTotalsUseCaseTest{

	private static final String TENANT_ID = "01TENANTMAWARMERAH0000000";
	private static final String STAFF_ID = "01STAFFLINDASARI000000000";
	private static final String CUSTOMER_ID = "01CUSTOMERBAMBANG00000000";
	private static final String SECOND_CUSTOMER_ID = "01CUSTOMERCITRALESTARI000";

	@Mock
	CustomerTotalsGateway gateway;
	@InjectMocks
	DefaultCustomerTotalsUseCase useCase;
	@Mock
	CustomerTotalsPresenter presenter;
	@Captor
	ArgumentCaptor<Set<String>> idsCaptor;
	@Captor
	ArgumentCaptor<CustomerTotalsResponse> responseCaptor;

	@Test
	void givenTwoIdsAndABlankOne_thenQueriesOnlyTheTwoTrimmedIds(){
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		List<CustomerTotalsProjection> totals = List.of(new CustomerTotalsProjection(CUSTOMER_ID, 9L,
				BigDecimal.valueOf(410000), Instant.parse("2026-10-11T03:20:00Z")));
		willReturn(totals).given(gateway)
				.totals(any(TenantId.class), anySet());
		Set<String> requested = new HashSet<>(Set.of(' ' + CUSTOMER_ID + ' ', SECOND_CUSTOMER_ID, "  "));

		useCase.execute(new CustomerTotalsRequest(requested), principal, presenter);

		then(gateway).should()
				.totals(eq(new TenantId(TENANT_ID)), idsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(idsCaptor.getValue()).containsExactlyInAnyOrder(CUSTOMER_ID, SECOND_CUSTOMER_ID);
			softly.then(responseCaptor.getValue().totals()).isEqualTo(totals);
		});
	}

	@Test
	void givenNoIds_thenAnswersEmptyWithoutTouchingTheStore(){
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();

		useCase.execute(new CustomerTotalsRequest(null), principal, presenter);

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().totals()).isEmpty());
	}

	@Test
	void givenMoreThanAHundredIds_thenThrowsConstraintViolationException(){
		AnalyticsAuthPrincipal principal = AnalyticsAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Set<String> tooMany = IntStream.rangeClosed(1, 101)
				.mapToObj(i -> "01CUSTOMERBANYAK" + i)
				.collect(Collectors.toSet());

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new CustomerTotalsRequest(tooMany), principal,
						presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

}
