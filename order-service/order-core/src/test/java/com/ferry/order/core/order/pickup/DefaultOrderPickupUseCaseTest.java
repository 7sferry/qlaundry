package com.ferry.order.core.order.pickup;

import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderNumberDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
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
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderPickupUseCaseTest{

	private static final String TENANT_ID = "01TENANTANGGREK000000000";
	private static final String STAFF_ID = "01STAFFROBERTHALIM000000";
	private static final String ORDER_ID = "01ORDERSELIMUT0000000000";
	private static final String ORDER_NUMBER = "INV-20260820-8HN2LC";

	@Mock
	OrderPickupGateway gateway;
	@InjectMocks
	DefaultOrderPickupUseCase useCase;
	@Mock
	OrderPickupPresenter presenter;
	@Captor
	ArgumentCaptor<OrderDomain> orderCaptor;
	@Captor
	ArgumentCaptor<OrderPickupResponse> responseCaptor;

	@Test
	void givenBlankOrderId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderPickupRequest("  ", null), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenOrderNotFound_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderPickupRequest(ORDER_ID, null), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
		then(presenter).should(never())
				.present(any(OrderPickupResponse.class));
	}

	@Test
	void givenPendingOrder_thenPickingItUpThrowsInvalidOrderStatusException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderDomain order = OrderDomain.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumberDomain(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullNameDomain("citra maharani"))
				.customerPhone(new PhoneDomain("+6281344556677"))
				.serviceId("01SERVICESELIMUT000000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.KG)
				.unitPrice(MoneyDomain.of(12000L))
				.quantity(3)
				.weightKg(3.0)
				.subtotal(MoneyDomain.of(36000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(36000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PENDING)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderPickupRequest(ORDER_ID, "driver on the way"), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("Cannot change order status from PENDING to PICKED_UP"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenConfirmedOrder_thenMarksItPickedUpAndSavesTheChange(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		OrderDomain order = OrderDomain.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumberDomain(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullNameDomain("citra maharani"))
				.customerPhone(new PhoneDomain("+6281344556677"))
				.serviceId("01SERVICESELIMUT000000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.KG)
				.unitPrice(MoneyDomain.of(12000L))
				.quantity(3)
				.weightKg(3.0)
				.subtotal(MoneyDomain.of(36000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(36000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.CONFIRMED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<OrderDomain>getArgument(0)).given(gateway)
				.save(any(OrderDomain.class));

		useCase.execute(new OrderPickupRequest(ORDER_ID, "picked up by driver joko"), principal, presenter);

		then(gateway).should()
				.findById(eq(new OrderIdDomain(ORDER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		OrderDomain saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.status()).isEqualTo(OrderStatus.PICKED_UP);
			softly.then(saved.completedAt()).isNull();
			softly.then(saved.staffNotesValue()).isEqualTo("picked up by driver joko");
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
			softly.then(responseCaptor.getValue().order().status()).isEqualTo(OrderStatus.PICKED_UP);
		});
	}

}
