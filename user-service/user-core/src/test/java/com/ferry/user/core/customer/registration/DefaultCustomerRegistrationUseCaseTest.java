package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.token.UserAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerRegistrationUseCaseTest{

	private static final String TENANT_ID = "01TENANTBOUGENVILLE000000";
	private static final String STAFF_ID = "01STAFFRIZKYPRATAMA000000";
	private static final String CUSTOMER_ID = "01CUSTOMERLESTARI00000000";
	private static final String FULL_NAME = "lestari wulandari";
	private static final String PHONE = "+6281244433221";

	@Mock
	CustomerRegistrationGateway gateway;
	@InjectMocks
	DefaultCustomerRegistrationUseCase useCase;
	@Mock
	CustomerRegistrationPresenter presenter;
	@Captor
	ArgumentCaptor<CustomerDomain> customerCaptor;
	@Captor
	ArgumentCaptor<CustomerPhoneDomain> phoneCaptor;
	@Captor
	ArgumentCaptor<CustomerEmailDomain> emailCaptor;
	@Captor
	ArgumentCaptor<CustomerAddressDomain> addressCaptor;
	@Captor
	ArgumentCaptor<CustomerRegistrationResponse> responseCaptor;

	@Test
	void givenBlankPhone_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(FULL_NAME, " ", null, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerRegistrationResponse.class));
	}

	@Test
	void givenPrincipalWithoutTenantId_thenThrowsIllegalArgumentException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.role(StaffRole.STAFF)
				.build();
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(FULL_NAME, PHONE, null, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@ParameterizedTest
	@CsvSource({
			"081244433221, +6281244433221",
			"0812-4443-3221, +6281244433221",
			"0812 4443 3221, +6281244433221",
			"6281244433221, +6281244433221",
			"+62 812 4443 3221, +6281244433221",
			"006281244433221, +6281244433221",
			"81244433221, +6281244433221"
	})
	void givenPhoneTypedTheWayPeopleSayIt_thenItIsStoredInE164(String typed, String stored){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(FULL_NAME, typed, null, null, null);
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0).toBuilder().id(CUSTOMER_ID).build())
				.given(gateway)
				.save(any(CustomerDomain.class));
		willAnswer(invocation -> invocation.<CustomerPhoneDomain>getArgument(0)).given(gateway)
				.save(any(CustomerPhoneDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(phoneCaptor.capture());

		thenSoftly(softly -> softly.then(phoneCaptor.getValue().phone().value()).isEqualTo(stored));
	}

	@Test
	void givenNewCustomerWithEveryContact_thenSavesEachOneAsItsOwnRow(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(FULL_NAME, PHONE,
				"lestari@laundry.test", "Jl. Bougenville No. 21", "prefers evening pickup");
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0).toBuilder().id(CUSTOMER_ID).build())
				.given(gateway)
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
				.save(phoneCaptor.capture());
		then(gateway).should()
				.save(emailCaptor.capture());
		then(gateway).should()
				.save(addressCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerDomain saved = customerCaptor.getValue();
		CustomerRegistrationResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.tenantId()).isEqualTo(TENANT_ID);
			softly.then(saved.fullNameValue()).isEqualTo(FULL_NAME);
			softly.then(saved.notesValue()).isEqualTo("prefers evening pickup");
			softly.then(saved.createdBy()).isEqualTo(STAFF_ID);
			softly.then(saved.deleted()).isFalse();
			softly.then(phoneCaptor.getValue().customerId()).isEqualTo(CUSTOMER_ID);
			softly.then(emailCaptor.getValue().email().value()).isEqualTo("lestari@laundry.test");
			softly.then(addressCaptor.getValue().addressLine().value()).isEqualTo("Jl. Bougenville No. 21");
			softly.then(response.customer().id()).isEqualTo(CUSTOMER_ID);
			softly.then(response.phones()).hasSize(1);
			softly.then(response.emails()).hasSize(1);
			softly.then(response.addresses()).hasSize(1);
		});
	}

	@Test
	void givenBlankEmailAndAddress_thenOnlyThePhoneRowIsSaved(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		CustomerRegistrationRequest request = new CustomerRegistrationRequest("bagas nugroho", "+6287811223344", "",
				"   ", null);
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0).toBuilder().id(CUSTOMER_ID).build())
				.given(gateway)
				.save(any(CustomerDomain.class));
		willAnswer(invocation -> invocation.<CustomerPhoneDomain>getArgument(0)).given(gateway)
				.save(any(CustomerPhoneDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should(never())
				.save(any(CustomerEmailDomain.class));
		then(gateway).should(never())
				.save(any(CustomerAddressDomain.class));
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerRegistrationResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.emails()).isEmpty();
			softly.then(response.addresses()).isEmpty();
			softly.then(response.customer().notesValue()).isNull();
		});
	}

	@Test
	void givenPhoneAlreadyUsedByAnotherCustomer_thenItIsStillAccepted(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		CustomerRegistrationRequest request = new CustomerRegistrationRequest("ayu pratiwi", PHONE, null, null, null);
		willAnswer(invocation -> invocation.<CustomerDomain>getArgument(0).toBuilder().id(CUSTOMER_ID).build())
				.given(gateway)
				.save(any(CustomerDomain.class));
		willAnswer(invocation -> invocation.<CustomerPhoneDomain>getArgument(0)).given(gateway)
				.save(any(CustomerPhoneDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(phoneCaptor.capture());
		then(presenter).should()
				.present(any(CustomerRegistrationResponse.class));

		thenSoftly(softly -> softly.then(phoneCaptor.getValue().phone().value()).isEqualTo(PHONE));
	}

}
