package com.ferry.user.core.customer.update;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerUpdateUseCaseTest{

	private static final String TENANT_ID = "01TENANTSEMERU0000000000";
	private static final String CUSTOMER_ID = "01CUSTOMERHARTONO0000000";
	private static final String PRINCIPAL_ID = "01STAFFINDRA00000000000";
	private static final String FULL_NAME = "hartono saputra";
	private static final String PHONE = "+6281399988877";

	@Mock
	CustomerUpdateGateway gateway;
	@InjectMocks
	DefaultCustomerUpdateUseCase useCase;
	@Mock
	CustomerUpdatePresenter presenter;
	@Captor
	ArgumentCaptor<CustomerDomain> customerCaptor;
	@Captor
	ArgumentCaptor<CustomerPhoneDomain> phoneCaptor;
	@Captor
	ArgumentCaptor<CustomerUpdateResponse> responseCaptor;

	@Test
	void givenBlankFullName_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).userId(PRINCIPAL_ID).build();
		CustomerUpdateRequest request = new CustomerUpdateRequest(CUSTOMER_ID, " ", PHONE, null, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerUpdateResponse.class));
	}

	@Test
	void givenBlankPhone_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).userId(PRINCIPAL_ID).build();
		CustomerUpdateRequest request = new CustomerUpdateRequest(CUSTOMER_ID, FULL_NAME, "   ", null, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenCustomerNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).userId(PRINCIPAL_ID).build();
		CustomerUpdateRequest request = new CustomerUpdateRequest(CUSTOMER_ID, FULL_NAME, PHONE, null, null, null);
		willReturn(Optional.empty()).given(gateway)
				.findById(any(CustomerIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Customer Not Found"));

		then(gateway).should(never())
				.save(any(CustomerDomain.class));
		then(presenter).should(never())
				.present(any(CustomerUpdateResponse.class));
	}

	@Test
	void givenValidRequest_thenReplacesPhoneEmailAddressAndPresentsResponse(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).userId(PRINCIPAL_ID).build();
		CustomerUpdateRequest request = new CustomerUpdateRequest(CUSTOMER_ID, FULL_NAME, PHONE,
				"hartono@laundry.test", "Jl. Semeru No. 3", "prefers weekend pickup");
		CustomerDomain existingCustomer = CustomerDomain.register(TENANT_ID, new FullNameDomain("old name"),
						new DescriptionDomain("old notes"), PRINCIPAL_ID)
				.toBuilder().id(CUSTOMER_ID).build();
		willReturn(Optional.of(existingCustomer)).given(gateway)
				.findById(any(CustomerIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0)).given(gateway)
				.save(any(CustomerDomain.class));
		willAnswer(invocation -> invocation.<CustomerPhoneDomain>getArgument(0)).given(gateway)
				.save(any(CustomerPhoneDomain.class));
		willAnswer(invocation -> invocation.<CustomerEmailDomain>getArgument(0)).given(gateway)
				.save(any(CustomerEmailDomain.class));
		willAnswer(invocation -> invocation.<CustomerAddressDomain>getArgument(0)).given(gateway)
				.save(any(CustomerAddressDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(customerCaptor.capture());
		then(gateway).should()
				.deletePhones(CUSTOMER_ID, PRINCIPAL_ID);
		then(gateway).should()
				.deleteEmails(CUSTOMER_ID, PRINCIPAL_ID);
		then(gateway).should()
				.deleteAddresses(CUSTOMER_ID, PRINCIPAL_ID);
		then(gateway).should()
				.save(phoneCaptor.capture());
		then(gateway).should()
				.save(any(CustomerEmailDomain.class));
		then(gateway).should()
				.save(any(CustomerAddressDomain.class));
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerDomain saved = customerCaptor.getValue();
		CustomerUpdateResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.fullNameValue()).isEqualTo(FULL_NAME);
			softly.then(saved.notesValue()).isEqualTo("prefers weekend pickup");
			softly.then(saved.updatedBy()).isEqualTo(PRINCIPAL_ID);
			softly.then(phoneCaptor.getValue().customerId()).isEqualTo(CUSTOMER_ID);
			softly.then(phoneCaptor.getValue().phone().value()).isEqualTo(PHONE);
			softly.then(response.phones()).hasSize(1);
			softly.then(response.emails()).hasSize(1);
			softly.then(response.addresses()).hasSize(1);
		});
	}

	@Test
	void givenBlankEmailAndAddress_thenOnlyPhoneRowIsSaved(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).userId(PRINCIPAL_ID).build();
		CustomerUpdateRequest request = new CustomerUpdateRequest(CUSTOMER_ID, FULL_NAME, PHONE, "", "   ", null);
		CustomerDomain existingCustomer = CustomerDomain.register(TENANT_ID, new FullNameDomain("old name"),
						new DescriptionDomain("old notes"), PRINCIPAL_ID)
				.toBuilder().id(CUSTOMER_ID).build();
		willReturn(Optional.of(existingCustomer)).given(gateway)
				.findById(any(CustomerIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0)).given(gateway)
				.save(any(CustomerDomain.class));
		willAnswer(invocation -> invocation.<CustomerPhoneDomain>getArgument(0)).given(gateway)
				.save(any(CustomerPhoneDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should(never())
				.save(any(CustomerEmailDomain.class));
		then(gateway).should(never())
				.save(any(CustomerAddressDomain.class));
		then(gateway).should()
				.deleteEmails(CUSTOMER_ID, PRINCIPAL_ID);
		then(gateway).should()
				.deleteAddresses(CUSTOMER_ID, PRINCIPAL_ID);
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerUpdateResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.emails()).isEmpty();
			softly.then(response.addresses()).isEmpty();
		});
	}

	@Test
	void givenPhoneTypedTheWayPeopleSayIt_thenItIsStoredInE164(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).userId(PRINCIPAL_ID).build();
		CustomerUpdateRequest request = new CustomerUpdateRequest(CUSTOMER_ID, FULL_NAME, "0813 9998 8877",
				null, null, null);
		CustomerDomain existingCustomer = CustomerDomain.register(TENANT_ID, new FullNameDomain("old name"),
						new DescriptionDomain("old notes"), PRINCIPAL_ID)
				.toBuilder().id(CUSTOMER_ID).build();
		willReturn(Optional.of(existingCustomer)).given(gateway)
				.findById(any(CustomerIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0)).given(gateway)
				.save(any(CustomerDomain.class));
		willAnswer(invocation -> invocation.<CustomerPhoneDomain>getArgument(0)).given(gateway)
				.save(any(CustomerPhoneDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(phoneCaptor.capture());
		thenSoftly(softly -> softly.then(phoneCaptor.getValue().phone().value()).isEqualTo(PHONE));
	}

}
