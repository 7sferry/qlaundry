package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerDetailUseCaseTest{

	private static final String TENANT_ID = "01TENANTPRAMBANAN0000000";
	private static final String CUSTOMER_ID = "01CUSTOMERNURAINI0000000";
	private static final String PRINCIPAL_ID = "01STAFFYOGA0000000000000";

	@Mock
	CustomerDetailGateway gateway;
	@InjectMocks
	DefaultCustomerDetailUseCase useCase;
	@Mock
	CustomerDetailPresenter presenter;
	@Captor
	ArgumentCaptor<CustomerDetailResponse> responseCaptor;

	@Test
	void givenBlankCustomerId_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.tenantId(TENANT_ID)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDetailRequest(" "), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerDetailResponse.class));
	}

	@Test
	void givenPrincipalWithoutTenantId_thenThrowsInvalidUserStateException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDetailRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(InvalidUserStateException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerDetailResponse.class));
	}

	@Test
	void givenCustomerNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.tenantId(TENANT_ID)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.findById(any(CustomerId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDetailRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Customer Not Found"));

		then(gateway).should(never())
				.findEmailsByFilter(any(CustomerEmailFilter.class));
		then(presenter).should(never())
				.present(any(CustomerDetailResponse.class));
	}

	@Test
	void givenCustomerFound_thenAggregatesContactsScopedToCustomerAndPresentsResponse(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.tenantId(TENANT_ID)
				.build();
		Customer existing = Customer.register(TENANT_ID, new FullName("nuraini safitri"),
						new Description("prefers cold wash"), PRINCIPAL_ID)
				.toBuilder().id(CUSTOMER_ID).build();
		willReturn(Optional.of(existing)).given(gateway)
				.findById(any(CustomerId.class), any(TenantId.class));
		List<CustomerEmail> emails = List.of(CustomerEmail.register(CUSTOMER_ID,
				new Email("nuraini@laundry.test"), PRINCIPAL_ID));
		List<CustomerPhone> phones = List.of(CustomerPhone.register(CUSTOMER_ID,
				new Phone("+6285566778899"), PRINCIPAL_ID));
		List<CustomerAddress> addresses = List.of(CustomerAddress.register(CUSTOMER_ID,
				new AddressLine("Jl. Prambanan No. 7"), PRINCIPAL_ID));
		willReturn(emails).given(gateway)
				.findEmailsByFilter(any(CustomerEmailFilter.class));
		willReturn(phones).given(gateway)
				.findPhonesByFilter(any(CustomerPhoneFilter.class));
		willReturn(addresses).given(gateway)
				.findAddressesByFilter(any(CustomerAddressFilter.class));

		useCase.execute(new CustomerDetailRequest(CUSTOMER_ID), principal, presenter);

		then(gateway).should()
				.findEmailsByFilter(eq(CustomerEmailFilter.builder().customerId(CUSTOMER_ID).build()));
		then(gateway).should()
				.findPhonesByFilter(eq(CustomerPhoneFilter.builder().customerId(CUSTOMER_ID).build()));
		then(gateway).should()
				.findAddressesByFilter(eq(CustomerAddressFilter.builder().customerId(CUSTOMER_ID).build()));
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerDetailResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.customer().id()).isEqualTo(CUSTOMER_ID);
			softly.then(response.emails()).isEqualTo(emails);
			softly.then(response.phones()).isEqualTo(phones);
			softly.then(response.addresses()).isEqualTo(addresses);
		});
	}

}
