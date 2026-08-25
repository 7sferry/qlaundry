package com.ferry.order.core.service.delete;

import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
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

import java.time.Instant;
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
class DefaultLaundryServiceDeleteUseCaseTest{

	private static final String TENANT_ID = "01TENANTMAWAR000000000000";
	private static final String STAFF_ID = "01STAFFPUTRIANDINI00000000";
	private static final String SERVICE_ID = "01SERVICECUCISEPATU000000";

	@Mock
	LaundryServiceDeleteGateway gateway;
	@InjectMocks
	DefaultLaundryServiceDeleteUseCase useCase;
	@Mock
	LaundryServiceDeletePresenter presenter;
	@Captor
	ArgumentCaptor<LaundryServiceDomain> serviceCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDeleteRequest request = new LaundryServiceDeleteRequest(SERVICE_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Only super staff can manage the service price list"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankServiceId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceDeleteRequest request = new LaundryServiceDeleteRequest("   ");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenServiceNotFound_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new LaundryServiceDeleteRequest(SERVICE_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Service Not Found"));

		then(gateway).should(never())
				.hasOpenOrders(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		then(gateway).should(never())
				.save(any(LaundryServiceDomain.class));
	}

	@Test
	void givenServiceWithOpenOrders_thenThrowsForbiddenActionException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Sepatu Deep Clean")
				.description(new NoteDomain("shoe deep clean"))
				.pricePerUnit(MoneyDomain.of(30000L))
				.unit(ServiceUnit.SET)
				.category(ServiceCategory.SPECIALTY)
				.estimatedHours(48)
				.expressMultiplier(1.5d)
				.popular(true)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(service)).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willReturn(true).given(gateway)
				.hasOpenOrders(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new LaundryServiceDeleteRequest(SERVICE_ID), principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Cannot delete a service that still has orders in progress"));

		then(gateway).should(never())
				.save(any(LaundryServiceDomain.class));
		then(presenter).should(never())
				.present(any(LaundryServiceDeleteResponse.class));
	}

	@Test
	void givenServiceWithNoOpenOrders_thenSoftDeletesAndPresentsServiceId(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Sepatu Deep Clean")
				.description(new NoteDomain("shoe deep clean"))
				.pricePerUnit(MoneyDomain.of(30000L))
				.unit(ServiceUnit.SET)
				.category(ServiceCategory.SPECIALTY)
				.estimatedHours(48)
				.expressMultiplier(1.5d)
				.popular(true)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy("01STAFFFOUNDINGOWNER0000000")
				.updatedAt(now)
				.updatedBy("01STAFFFOUNDINGOWNER0000000")
				.build();
		willReturn(Optional.of(service)).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willReturn(false).given(gateway)
				.hasOpenOrders(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		useCase.execute(new LaundryServiceDeleteRequest(SERVICE_ID), principal, presenter);

		then(gateway).should()
				.hasOpenOrders(eq(new LaundryServiceIdDomain(SERVICE_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(serviceCaptor.capture());
		then(presenter).should()
				.present(eq(new LaundryServiceDeleteResponse(SERVICE_ID)));

		LaundryServiceDomain saved = serviceCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.deleted()).isTrue();
			softly.then(saved.active()).isFalse();
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
		});
	}

}
