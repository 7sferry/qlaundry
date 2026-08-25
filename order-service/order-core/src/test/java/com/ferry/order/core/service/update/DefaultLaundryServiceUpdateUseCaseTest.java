package com.ferry.order.core.service.update;

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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
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
class DefaultLaundryServiceUpdateUseCaseTest{

	private static final String TENANT_ID = "01TENANTBOUGENVILLE0000000";
	private static final String STAFF_ID = "01STAFFRAHMATHIDAYAT0000000";
	private static final String SERVICE_ID = "01SERVICESETRIKA00000000000";
	private static final String SERVICE_NAME = "Setrika Ekspres";

	@Mock
	LaundryServiceUpdateGateway gateway;
	@InjectMocks
	DefaultLaundryServiceUpdateUseCase useCase;
	@Mock
	LaundryServiceUpdatePresenter presenter;
	@Captor
	ArgumentCaptor<LaundryServiceDomain> serviceCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceUpdateRequest request = new LaundryServiceUpdateRequest(SERVICE_ID, SERVICE_NAME,
				"iron within the day", new BigDecimal("6000"), ServiceUnit.ITEM, ServiceCategory.IRON, 12, 1.5d,
				true, true);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Only super staff can manage the service price list"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenNonPositivePrice_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceUpdateRequest request = new LaundryServiceUpdateRequest(SERVICE_ID, SERVICE_NAME,
				"iron within the day", BigDecimal.ZERO, ServiceUnit.ITEM, ServiceCategory.IRON, 12, 1.5d, true, true);

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
		LaundryServiceUpdateRequest request = new LaundryServiceUpdateRequest(SERVICE_ID, SERVICE_NAME,
				"iron within the day", new BigDecimal("6000"), ServiceUnit.ITEM, ServiceCategory.IRON, 12, 1.5d,
				true, true);
		willReturn(Optional.empty()).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Service Not Found"));

		then(gateway).should(never())
				.save(any(LaundryServiceDomain.class));
	}

	@Test
	void givenValidRequest_thenUpdatesAndSavesTheService(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceDomain existing = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Setrika Reguler")
				.description(new NoteDomain("iron next day"))
				.pricePerUnit(MoneyDomain.of(4000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.IRON)
				.estimatedHours(24)
				.expressMultiplier(1.0d)
				.popular(false)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy("01STAFFPREVIOUSOWNER000000")
				.updatedAt(now)
				.updatedBy("01STAFFPREVIOUSOWNER000000")
				.build();
		LaundryServiceUpdateRequest request = new LaundryServiceUpdateRequest(SERVICE_ID, SERVICE_NAME,
				"iron within the day", new BigDecimal("6000"), ServiceUnit.ITEM, ServiceCategory.IRON, 12, 1.5d,
				true, true);
		willReturn(Optional.of(existing)).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<LaundryServiceDomain>getArgument(0)).given(gateway)
				.save(any(LaundryServiceDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findById(eq(new LaundryServiceIdDomain(SERVICE_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(serviceCaptor.capture());
		then(presenter).should()
				.present(any(LaundryServiceUpdateResponse.class));

		LaundryServiceDomain saved = serviceCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.id()).isEqualTo(SERVICE_ID);
			softly.then(saved.name()).isEqualTo(SERVICE_NAME);
			softly.then(saved.descriptionValue()).isEqualTo("iron within the day");
			softly.then(saved.pricePerUnit().value()).isEqualByComparingTo(new BigDecimal("6000.00"));
			softly.then(saved.estimatedHours()).isEqualTo(12);
			softly.then(saved.expressMultiplier()).isEqualTo(1.5d);
			softly.then(saved.popular()).isTrue();
			softly.then(saved.active()).isTrue();
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
			softly.then(saved.createdBy()).isEqualTo("01STAFFPREVIOUSOWNER000000");
		});
	}

	@Test
	void givenNullExpressMultiplier_thenDefaultsToOne(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceDomain existing = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Setrika Reguler")
				.description(new NoteDomain("iron next day"))
				.pricePerUnit(MoneyDomain.of(4000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.IRON)
				.estimatedHours(24)
				.expressMultiplier(1.0d)
				.popular(false)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		LaundryServiceUpdateRequest request = new LaundryServiceUpdateRequest(SERVICE_ID, SERVICE_NAME,
				"iron within the day", new BigDecimal("6000"), ServiceUnit.ITEM, ServiceCategory.IRON, 12, null,
				true, true);
		willReturn(Optional.of(existing)).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<LaundryServiceDomain>getArgument(0)).given(gateway)
				.save(any(LaundryServiceDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(serviceCaptor.capture());

		thenSoftly(softly -> softly.then(serviceCaptor.getValue().expressMultiplier()).isEqualTo(1.0d));
	}

	@Test
	void givenNullActiveFlag_thenDefaultsToActiveTrue(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceDomain existing = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Setrika Reguler")
				.description(new NoteDomain("iron next day"))
				.pricePerUnit(MoneyDomain.of(4000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.IRON)
				.estimatedHours(24)
				.expressMultiplier(1.0d)
				.popular(false)
				.active(false)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		LaundryServiceUpdateRequest request = new LaundryServiceUpdateRequest(SERVICE_ID, SERVICE_NAME,
				"iron within the day", new BigDecimal("6000"), ServiceUnit.ITEM, ServiceCategory.IRON, 12, 1.2d,
				true, null);
		willReturn(Optional.of(existing)).given(gateway)
				.findById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<LaundryServiceDomain>getArgument(0)).given(gateway)
				.save(any(LaundryServiceDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(serviceCaptor.capture());

		thenSoftly(softly -> softly.then(serviceCaptor.getValue().active()).isTrue());
	}

}
