package com.ferry.user.core.customer.delete;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.staff.StaffRole;
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

import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerDeleteUseCaseTest{

	private static final String TENANT_ID = "01TENANTKARTINI000000000";
	private static final String CUSTOMER_ID = "01CUSTOMERSETIAWAN000000";
	private static final String PRINCIPAL_ID = "01STAFFKARTIKA00000000000";

	@Mock
	CustomerDeleteGateway gateway;
	@InjectMocks
	DefaultCustomerDeleteUseCase useCase;
	@Mock
	CustomerDeletePresenter presenter;
	@Captor
	ArgumentCaptor<Customer> customerCaptor;
	@Captor
	ArgumentCaptor<CustomerDeleteResponse> responseCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(ForbiddenActionException.class)
				.hasMessage("Only super staff can delete customer"));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerDeleteResponse.class));
	}

	@Test
	void givenBlankCustomerId_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDeleteRequest(" "), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerDeleteResponse.class));
	}

	@Test
	void givenPrincipalWithoutTenantId_thenThrowsInvalidUserStateException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(InvalidUserStateException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(CustomerDeleteResponse.class));
	}

	@Test
	void givenCustomerNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.findById(any(CustomerId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Customer Not Found"));

		then(gateway).should(never())
				.save(any(Customer.class));
		then(gateway).should(never())
				.deleteContacts(anyString(), anyString());
		then(presenter).should(never())
				.present(any(CustomerDeleteResponse.class));
	}

	@Test
	void givenValidSuperStaffDeletingCustomer_thenSoftDeletesCustomerAndItsContacts(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Customer existing = Customer.register(TENANT_ID, new FullName("setiawan wibowo"),
						new Description("regular customer"), PRINCIPAL_ID)
				.toBuilder().id(CUSTOMER_ID).build();
		willReturn(Optional.of(existing)).given(gateway)
				.findById(any(CustomerId.class), any(TenantId.class));

		useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter);

		then(gateway).should()
				.findById(eq(new CustomerId(CUSTOMER_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.save(customerCaptor.capture());
		then(gateway).should()
				.deleteContacts(CUSTOMER_ID, PRINCIPAL_ID);
		then(presenter).should()
				.present(responseCaptor.capture());

		Customer saved = customerCaptor.getValue();
		CustomerDeleteResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.deleted()).isTrue();
			softly.then(saved.id()).isEqualTo(CUSTOMER_ID);
			softly.then(saved.updatedBy()).isEqualTo(PRINCIPAL_ID);
			softly.then(response.customerId()).isEqualTo(CUSTOMER_ID);
		});
	}

}
