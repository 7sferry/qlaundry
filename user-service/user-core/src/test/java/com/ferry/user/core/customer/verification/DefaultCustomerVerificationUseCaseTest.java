package com.ferry.user.core.customer.verification;

import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerVerificationUseCaseTest{

	private static final String TENANT_ID = "01TENANTCEMPAKA0000000000";
	private static final String CUSTOMER_ID = "01CUSTOMERWAHYUDI00000000";

	@Mock
	CustomerVerificationGateway gateway;
	@InjectMocks
	DefaultCustomerVerificationUseCase useCase;
	@Mock
	CustomerVerificationPresenter presenter;
	@Captor
	ArgumentCaptor<CustomerVerificationResponse> responseCaptor;

	@Test
	void givenBlankCustomerId_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerVerificationRequest(" ", TENANT_ID), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenCustomerOwnedByTheTenant_thenAnswersValid(){
		willReturn(true).given(gateway)
				.existsByIdAndTenantId(any(CustomerIdDomain.class), any(TenantIdDomain.class));

		useCase.execute(new CustomerVerificationRequest(CUSTOMER_ID, TENANT_ID), presenter);

		then(gateway).should()
				.existsByIdAndTenantId(eq(new CustomerIdDomain(CUSTOMER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerVerificationResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.valid()).isTrue();
			softly.then(response.customerId()).isEqualTo(CUSTOMER_ID);
			softly.then(response.tenantId()).isEqualTo(TENANT_ID);
		});
	}

	@Test
	void givenCustomerOfAnotherTenant_thenAnswersInvalid(){
		willReturn(false).given(gateway)
				.existsByIdAndTenantId(any(CustomerIdDomain.class), any(TenantIdDomain.class));

		useCase.execute(new CustomerVerificationRequest(CUSTOMER_ID, "01TENANTBOUGENVILLE000000"), presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().valid()).isFalse());
	}

}
