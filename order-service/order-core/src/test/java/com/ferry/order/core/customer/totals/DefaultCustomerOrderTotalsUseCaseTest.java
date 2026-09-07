package com.ferry.order.core.customer.totals;

import com.ferry.order.domain.customer.totals.CustomerOrderTotalsProjection;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
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
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerOrderTotalsUseCaseTest{

	private static final String TENANT_ID = "01TENANTMELATI000000000000";
	private static final String STAFF_ID = "01STAFFDEWIANGGRAENI0000000";
	private static final String CUSTOMER_ID = "01CUSTOMERSITINURHALIZA0000";

	@Mock
	CustomerOrderTotalsGateway gateway;
	@InjectMocks
	DefaultCustomerOrderTotalsUseCase useCase;
	@Mock
	CustomerOrderTotalsPresenter presenter;
	@Captor
	ArgumentCaptor<CustomerOrderTotalsResponse> responseCaptor;

	@Test
	void givenEmptyCustomerIds_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		CustomerOrderTotalsRequest request = new CustomerOrderTotalsRequest(Set.of());

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerOrderTotalsResponse.class));
	}

	@Test
	void givenCustomerIds_thenPresentsTotalsScopedToTenant(){
		Instant lastOrderAt = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		List<CustomerOrderTotalsProjection> totals = List.of(
				new CustomerOrderTotalsProjection(CUSTOMER_ID, 4L, BigDecimal.valueOf(128000L), lastOrderAt));
		willReturn(totals).given(gateway)
				.findTotals(anySet(), any(TenantIdDomain.class));

		useCase.execute(new CustomerOrderTotalsRequest(Set.of(CUSTOMER_ID)), principal, presenter);

		then(gateway).should()
				.findTotals(eq(Set.of(CUSTOMER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerOrderTotalsResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.totals()).isEqualTo(totals);
			softly.then(response.totals().getFirst().customerId()).isEqualTo(CUSTOMER_ID);
			softly.then(response.totals().getFirst().totalOrders()).isEqualTo(4L);
		});
	}

}
