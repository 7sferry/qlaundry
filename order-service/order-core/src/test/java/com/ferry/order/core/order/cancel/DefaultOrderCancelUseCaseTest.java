package com.ferry.order.core.order.cancel;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEventDomain;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
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
class DefaultOrderCancelUseCaseTest{

	private static final String TENANT_ID = "01TENANTSERUNI0000000000";
	private static final String STAFF_ID = "01STAFFNOVITASARI0000000";
	private static final String ORDER_ID = "01ORDERKARPET00000000000";
	private static final String ORDER_NUMBER = "INV-20260818-9WQ3HB";
	private static final String CUSTOMER_NAME = "dimas prakoso";
	private static final String CUSTOMER_PHONE = "+6281877766554";

	@Mock
	OrderCancelGateway gateway;
	@Mock
	AnalyticsEventPublisher publisher;
	@Captor
	ArgumentCaptor<AnalyticsEventConfig> analyticsCaptor;
	@InjectMocks
	DefaultOrderCancelUseCase useCase;
	@Mock
	OrderCancelPresenter presenter;
	@Captor
	ArgumentCaptor<OrderDomain> orderCaptor;

	@Test
	void givenInProgressOrder_thenCancelsItWithTheReasonInStaffNotes(){
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
				.customerName(new FullNameDomain(CUSTOMER_NAME))
				.customerPhone(new PhoneDomain(CUSTOMER_PHONE))
				.serviceId("01SERVICEKARPET000000000")
				.serviceName("Cuci Karpet")
				.unit(ServiceUnit.SET)
				.unitPrice(MoneyDomain.of(45000L))
				.quantity(2)
				.subtotal(MoneyDomain.of(90000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(90000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.IN_PROGRESS)
				.notes(new NoteDomain("customer waiting"))
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(172800))
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
		willReturn(AnalyticsEventDomain.create(AnalyticsAggregate.ORDER, AnalyticsEventType.ORDER_STATUS_CHANGED,
				TENANT_ID, ORDER_ID, 1, "{}", STAFF_ID)).given(publisher)
				.save(any(AnalyticsEventConfig.class));

		useCase.execute(new OrderCancelRequest(ORDER_ID, "customer changed their mind"), principal, presenter);

		then(publisher).should()
				.save(analyticsCaptor.capture());
		then(publisher).should()
				.publish(any(AnalyticsEventDomain.class));
		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(any(OrderCancelResponse.class));

		OrderDomain saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(analyticsCaptor.getValue().type()).isEqualTo(AnalyticsEventType.ORDER_STATUS_CHANGED);
			softly.then(analyticsCaptor.getValue().aggregate()).isEqualTo(AnalyticsAggregate.ORDER);
			softly.then(((OrderAnalyticsMessage) analyticsCaptor.getValue().payload()).status())
					.isEqualTo(OrderStatus.CANCELLED);
			softly.then(saved.status()).isEqualTo(OrderStatus.CANCELLED);
			softly.then(saved.completedAt()).isNull();
			softly.then(saved.staffNotesValue()).isEqualTo("customer changed their mind");
			softly.then(saved.notesValue()).isEqualTo("customer waiting");
		});
	}

	@Test
	void givenCompletedOrder_thenCancellingItThrowsInvalidOrderStatusException(){
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
				.customerName(new FullNameDomain(CUSTOMER_NAME))
				.customerPhone(new PhoneDomain(CUSTOMER_PHONE))
				.serviceId("01SERVICEKARPET000000000")
				.serviceName("Cuci Karpet")
				.unit(ServiceUnit.SET)
				.unitPrice(MoneyDomain.of(45000L))
				.quantity(2)
				.subtotal(MoneyDomain.of(90000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(90000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.PAID)
				.status(OrderStatus.COMPLETED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(172800))
				.completedAt(now)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderCancelRequest(ORDER_ID, "too late"), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("Cannot change order status from COMPLETED to CANCELLED"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

}
