package com.ferry.order.core.order.ready;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEvent;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
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
class DefaultOrderReadyUseCaseTest{

	private static final String TENANT_ID = "01TENANTDAHLIA000000000";
	private static final String STAFF_ID = "01STAFFWAHYUPRATAMA00000";
	private static final String ORDER_ID = "01ORDERBANTAL00000000000";
	private static final String ORDER_NUMBER = "INV-20260822-3ZM8QY";

	@Mock
	OrderReadyGateway gateway;
	@Mock
	OrderAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<AnalyticsEventConfig> analyticsCaptor;
	@InjectMocks
	DefaultOrderReadyUseCase useCase;
	@Mock
	OrderReadyPresenter presenter;
	@Captor
	ArgumentCaptor<Order> orderCaptor;
	@Captor
	ArgumentCaptor<OrderReadyResponse> responseCaptor;

	@Test
	void givenBlankOrderId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderReadyRequest("   ", null), principal, presenter))
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
				.findById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderReadyRequest(ORDER_ID, null), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.save(any(Order.class));
		then(presenter).should(never())
				.present(any(OrderReadyResponse.class));
	}

	@Test
	void givenPickedUpOrder_thenMarkingItReadyThrowsInvalidOrderStatusException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("nurul aini"))
				.customerPhone(new Phone("+6281388990011"))
				.serviceId("01SERVICEBANTAL000000000")
				.serviceName("Cuci Bantal")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(10000L))
				.quantity(2)
				.subtotal(Money.of(20000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(20000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PICKED_UP)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderReadyRequest(ORDER_ID, "not washed yet"), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("Cannot change order status from PICKED_UP to READY"));

		then(gateway).should(never())
				.save(any(Order.class));
	}

	@Test
	void givenInProgressOrder_thenMarksItReadyAndSavesTheChange(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("nurul aini"))
				.customerPhone(new Phone("+6281388990011"))
				.serviceId("01SERVICEBANTAL000000000")
				.serviceName("Cuci Bantal")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(10000L))
				.quantity(2)
				.subtotal(Money.of(20000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(20000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.IN_PROGRESS)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<Order>getArgument(0)).given(gateway)
				.save(any(Order.class));
		willReturn(AnalyticsEvent.create(AnalyticsAggregate.ORDER, AnalyticsEventType.ORDER_STATUS_CHANGED,
				TENANT_ID, ORDER_ID, 1, "{}", STAFF_ID)).given(publisher)
				.save(any(AnalyticsEventConfig.class));

		useCase.execute(new OrderReadyRequest(ORDER_ID, "dried and folded"), principal, presenter);

		then(publisher).should()
				.save(analyticsCaptor.capture());
		then(publisher).should()
				.publish(any(AnalyticsEvent.class));
		then(gateway).should()
				.findById(eq(new OrderId(ORDER_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		Order saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(analyticsCaptor.getValue().type()).isEqualTo(AnalyticsEventType.ORDER_STATUS_CHANGED);
			softly.then(analyticsCaptor.getValue().aggregate()).isEqualTo(AnalyticsAggregate.ORDER);
			softly.then(((OrderAnalyticsMessage) analyticsCaptor.getValue().payload()).status())
					.isEqualTo(OrderStatus.READY);
			softly.then(saved.status()).isEqualTo(OrderStatus.READY);
			softly.then(saved.completedAt()).isNull();
			softly.then(saved.staffNotesValue()).isEqualTo("dried and folded");
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
			softly.then(responseCaptor.getValue().order().status()).isEqualTo(OrderStatus.READY);
		});
	}

}
