package com.ferry.user.core.customer.delete;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.staff.StaffRole;
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
	ArgumentCaptor<CustomerDomain> customerCaptor;
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
	void givenPrincipalWithoutTenantId_thenThrowsIllegalArgumentException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(IllegalArgumentException.class));

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
				.findById(any(CustomerIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Customer Not Found"));

		then(gateway).should(never())
				.save(any(CustomerDomain.class));
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
		CustomerDomain existing = CustomerDomain.register(TENANT_ID, new FullNameDomain("setiawan wibowo"),
						new DescriptionDomain("regular customer"), PRINCIPAL_ID)
				.toBuilder().id(CUSTOMER_ID).build();
		willReturn(Optional.of(existing)).given(gateway)
				.findById(any(CustomerIdDomain.class), any(TenantIdDomain.class));

		useCase.execute(new CustomerDeleteRequest(CUSTOMER_ID), principal, presenter);

		then(gateway).should()
				.findById(eq(new CustomerIdDomain(CUSTOMER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(customerCaptor.capture());
		then(gateway).should()
				.deleteContacts(CUSTOMER_ID, PRINCIPAL_ID);
		then(presenter).should()
				.present(responseCaptor.capture());

		CustomerDomain saved = customerCaptor.getValue();
		CustomerDeleteResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.deleted()).isTrue();
			softly.then(saved.id()).isEqualTo(CUSTOMER_ID);
			softly.then(saved.updatedBy()).isEqualTo(PRINCIPAL_ID);
			softly.then(response.customerId()).isEqualTo(CUSTOMER_ID);
		});
	}

}
