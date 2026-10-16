package com.ferry.order.core.service.create;

import com.ferry.order.core.analytics.OrderAnalyticsEvent;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.domain.analytics.OrderAnalyticsAggregate;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

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
	@Mock
	OrderAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<OrderAnalyticsEvent> analyticsCaptor;
	@InjectMocks
	DefaultLaundryServiceCreateUseCase useCase;
	@Mock
	LaundryServiceCreatePresenter presenter;
	@Captor
	ArgumentCaptor<LaundryService> serviceCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", new BigDecimal("9000"),
				ServiceUnit.KG, ServiceCategory.WASH, 24, 12, 1.75d, true);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Only super staff can manage the service price list"));

		then(gateway).shouldHaveNoInteractions();
	}

	static Stream<BigDecimal> invalidPricePerUnit(){
		return Stream.of(null, BigDecimal.ZERO, new BigDecimal("-9000"));
	}

	@ParameterizedTest
	@MethodSource("invalidPricePerUnit")
	void givenNullOrNonPositivePrice_thenThrowsConstraintViolationException(BigDecimal pricePerUnit){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", pricePerUnit,
				ServiceUnit.KG, ServiceCategory.WASH, 24, 12, 1.75d, true);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenDuplicateServiceName_thenThrowsInvalidOrderStateException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", new BigDecimal("9000"),
				ServiceUnit.KG, ServiceCategory.WASH, 24, 12, 1.75d, true);
		willReturn(true).given(gateway)
				.existsByName(anyString(), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidOrderStateException.class)
				.hasMessage("Service name already exists"));

		then(gateway).should(never())
				.save(any(LaundryService.class));
	}

	@Test
	void givenValidRequest_thenSavesAnActiveService(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest(SERVICE_NAME, "wash and iron", new BigDecimal("9000"),
				ServiceUnit.KG, ServiceCategory.WASH, 24, 12, 1.75d, true);
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantId.class));
		willAnswer(invocation -> invocation.<LaundryService>getArgument(0)).given(gateway)
				.save(any(LaundryService.class));

		willReturn(com.ferry.order.domain.analytics.OrderAnalyticsEvent.create(OrderAnalyticsAggregate.LAUNDRY_SERVICE, OrderAnalyticsEventType.LAUNDRY_SERVICE_CREATED, TENANT_ID,
				"01SERVICEBARU0000000000000", 1, "{}", STAFF_ID)).given(publisher)
				.save(any(OrderAnalyticsEvent.class));

		useCase.execute(request, principal, presenter);

		then(publisher).should()
				.save(analyticsCaptor.capture());
		then(publisher).should()
				.publish(any(com.ferry.order.domain.analytics.OrderAnalyticsEvent.class));
		then(gateway).should()
				.existsByName(eq(SERVICE_NAME), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.save(serviceCaptor.capture());
		then(presenter).should()
				.present(any(LaundryServiceCreateResponse.class));

		LaundryService saved = serviceCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(analyticsCaptor.getValue().type())
					.isEqualTo(OrderAnalyticsEventType.LAUNDRY_SERVICE_CREATED);
			softly.then(analyticsCaptor.getValue().aggregate()).isEqualTo(OrderAnalyticsAggregate.LAUNDRY_SERVICE);
			softly.then(((LaundryServiceAnalyticsMessage) analyticsCaptor.getValue().payload()).active())
					.isTrue();
			softly.then(saved.unit()).isEqualTo(ServiceUnit.KG);
			softly.then(saved.category()).isEqualTo(ServiceCategory.WASH);
			softly.then(saved.pricePerUnit().value()).isEqualByComparingTo(new BigDecimal("9000.00"));
			softly.then(saved.expressHours()).isEqualTo(12);
			softly.then(saved.expressMultiplier()).isEqualTo(1.75d);
			softly.then(saved.active()).isTrue();
			softly.then(saved.deleted()).isFalse();
			softly.then(saved.tenantId()).isEqualTo(TENANT_ID);
			softly.then(saved.createdBy()).isEqualTo(STAFF_ID);
		});
	}

	@Test
	void givenNoExpressHoursAndMultiplier_thenSavesAServiceWithoutExpress(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest("Cuci Sepatu", null, new BigDecimal("25000"), ServiceUnit.SET,
				ServiceCategory.SPECIALTY, 72, null, null, false);
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantId.class));
		willAnswer(invocation -> invocation.<LaundryService>getArgument(0)).given(gateway)
				.save(any(LaundryService.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(serviceCaptor.capture());

		thenSoftly(softly -> {
			softly.then(serviceCaptor.getValue().offersExpress()).isFalse();
			softly.then(serviceCaptor.getValue().expressHours()).isNull();
			softly.then(serviceCaptor.getValue().expressMultiplier()).isNull();
		});
	}

	@Test
	void givenExpressHoursWithoutMultiplier_thenThrowsInvalidOrderStateException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest("Cuci Boneka", null, new BigDecimal("20000"), ServiceUnit.ITEM,
				ServiceCategory.SPECIALTY, 48, 24, null, false);
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidOrderStateException.class)
				.hasMessage("Express hours and express multiplier must be set together"));

		then(gateway).should(never())
				.save(any(LaundryService.class));
	}

	@Test
	void givenExpressHoursAboveEstimatedHours_thenThrowsInvalidOrderStateException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryServiceCreateRequest request = new LaundryServiceCreateRequest("Cuci Gorden", "curtains", new BigDecimal("30000"), ServiceUnit.KG,
				ServiceCategory.SPECIALTY, 48, 60, 1.5d, true);
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidOrderStateException.class)
				.hasMessage("Express hours must be between 1 and the estimated hours"));

		then(gateway).should(never())
				.save(any(LaundryService.class));
	}

}
