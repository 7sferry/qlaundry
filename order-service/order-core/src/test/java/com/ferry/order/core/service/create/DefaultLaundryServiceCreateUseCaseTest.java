package com.ferry.order.core.service.create;

import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.service.LaundryServiceDomain;
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

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyString;
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
class DefaultLaundryServiceCreateUseCaseTest{

	private static final String TENANT_ID = "01TENANTCEMPAKA00000000000";
	private static final String STAFF_ID = "01STAFFDEWIANGGRAINI000000";
	private static final String SERVICE_NAME = "Cuci Setrika Kilat";

	@Mock
	LaundryServiceCreateGateway gateway;
	@InjectMocks
	DefaultLaundryServiceCreateUseCase useCase;
	@Mock
	LaundryServiceCreatePresenter presenter;
	@Captor
	ArgumentCaptor<LaundryServiceDomain> serviceCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", new BigDecimal("9000"),
				ServiceUnit.KG, ServiceCategory.WASH, 24, 1.75d, true);

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
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", BigDecimal.ZERO,
				ServiceUnit.KG, ServiceCategory.WASH, 24, 1.75d, true);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenDuplicateServiceName_thenThrowsIllegalArgumentException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", new BigDecimal("9000"),
				ServiceUnit.KG, ServiceCategory.WASH, 24, 1.75d, true);
		willReturn(true).given(gateway)
				.existsByName(anyString(), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Service name already exists"));

		then(gateway).should(never())
				.save(any(LaundryServiceDomain.class));
	}

	@Test
	void givenValidRequest_thenSavesAnActiveService(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", new BigDecimal("9000"),
				ServiceUnit.KG, ServiceCategory.WASH, 24, 1.75d, true);
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<LaundryServiceDomain>getArgument(0)).given(gateway)
				.save(any(LaundryServiceDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.existsByName(eq(SERVICE_NAME), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(serviceCaptor.capture());
		then(presenter).should()
				.present(any(LaundryServiceCreateResponse.class));

		LaundryServiceDomain saved = serviceCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.unit()).isEqualTo(ServiceUnit.KG);
			softly.then(saved.category()).isEqualTo(ServiceCategory.WASH);
			softly.then(saved.pricePerUnit().value()).isEqualByComparingTo(new BigDecimal("9000.00"));
			softly.then(saved.expressMultiplier()).isEqualTo(1.75d);
			softly.then(saved.active()).isTrue();
			softly.then(saved.deleted()).isFalse();
			softly.then(saved.tenantId()).isEqualTo(TENANT_ID);
			softly.then(saved.createdBy()).isEqualTo(STAFF_ID);
		});
	}

	@Test
	void givenMissingExpressMultiplier_thenDefaultsToOne(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest("Cuci Sepatu", null, new BigDecimal("25000"), ServiceUnit.SET,
				ServiceCategory.SPECIALTY, 72, null, false);
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<LaundryServiceDomain>getArgument(0)).given(gateway)
				.save(any(LaundryServiceDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(serviceCaptor.capture());

		thenSoftly(softly -> softly.then(serviceCaptor.getValue().expressMultiplier()).isEqualTo(1.0d));
	}

}
