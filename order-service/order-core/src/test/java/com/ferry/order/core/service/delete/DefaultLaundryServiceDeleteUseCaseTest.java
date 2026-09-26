package com.ferry.order.core.service.delete;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEvent;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
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
import static org.mockito.BDDMockito.willAnswer;
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
	@Mock
	OrderAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<AnalyticsEventConfig> analyticsCaptor;
	@InjectMocks
	DefaultLaundryServiceDeleteUseCase useCase;
	@Mock
	LaundryServiceDeletePresenter presenter;
	@Captor
	ArgumentCaptor<LaundryService> serviceCaptor;

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
				.findById(any(LaundryServiceId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new LaundryServiceDeleteRequest(SERVICE_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Service Not Found"));

		then(gateway).should(never())
				.hasOpenOrders(any(LaundryServiceId.class), any(TenantId.class));
		then(gateway).should(never())
				.save(any(LaundryService.class));
	}

	@Test
	void givenServiceWithOpenOrders_thenThrowsForbiddenActionException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		LaundryService service = LaundryService.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Sepatu Deep Clean")
				.description(new Note("shoe deep clean"))
				.pricePerUnit(Money.of(30000L))
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
				.findById(any(LaundryServiceId.class), any(TenantId.class));
		willReturn(true).given(gateway)
				.hasOpenOrders(any(LaundryServiceId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new LaundryServiceDeleteRequest(SERVICE_ID), principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Cannot delete a service that still has orders in progress"));

		then(gateway).should(never())
				.save(any(LaundryService.class));
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
		LaundryService service = LaundryService.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Sepatu Deep Clean")
				.description(new Note("shoe deep clean"))
				.pricePerUnit(Money.of(30000L))
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
				.findById(any(LaundryServiceId.class), any(TenantId.class));
		willReturn(false).given(gateway)
				.hasOpenOrders(any(LaundryServiceId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<LaundryService>getArgument(0)).given(gateway)
				.save(any(LaundryService.class));
		willReturn(AnalyticsEvent.create(AnalyticsAggregate.LAUNDRY_SERVICE, AnalyticsEventType.LAUNDRY_SERVICE_DELETED,
				TENANT_ID, SERVICE_ID, 1, "{}", STAFF_ID)).given(publisher)
				.save(any(AnalyticsEventConfig.class));

		useCase.execute(new LaundryServiceDeleteRequest(SERVICE_ID), principal, presenter);

		then(publisher).should()
				.save(analyticsCaptor.capture());
		then(publisher).should()
				.publish(any(AnalyticsEvent.class));
		then(gateway).should()
				.hasOpenOrders(eq(new LaundryServiceId(SERVICE_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.save(serviceCaptor.capture());
		then(presenter).should()
				.present(eq(new LaundryServiceDeleteResponse(SERVICE_ID)));

		LaundryService saved = serviceCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(analyticsCaptor.getValue().type())
					.isEqualTo(AnalyticsEventType.LAUNDRY_SERVICE_DELETED);
			softly.then(analyticsCaptor.getValue().aggregate()).isEqualTo(AnalyticsAggregate.LAUNDRY_SERVICE);
			softly.then(((LaundryServiceAnalyticsMessage) analyticsCaptor.getValue().payload()).deleted())
					.isTrue();
			softly.then(saved.deleted()).isTrue();
			softly.then(saved.active()).isFalse();
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
		});
	}

}
