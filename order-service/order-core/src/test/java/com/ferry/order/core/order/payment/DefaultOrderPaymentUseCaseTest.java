package com.ferry.order.core.order.payment;

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
class DefaultOrderPaymentUseCaseTest{

	private static final String TENANT_ID = "01TENANTANGGREK0000000000";
	private static final String STAFF_ID = "01STAFFYULIANA00000000000";
	private static final String ORDER_ID = "01ORDERDRYCLEAN0000000000";
	private static final String ORDER_NUMBER = "INV-20260818-7TB2ZM";
	private static final String CUSTOMER_NAME = "hendra gunawan";
	private static final String CUSTOMER_PHONE = "+6285677712345";

	@Mock
	OrderPaymentGateway gateway;
	@InjectMocks
	DefaultOrderPaymentUseCase useCase;
	@Mock
	OrderPaymentPresenter presenter;
	@Captor
	ArgumentCaptor<OrderDomain> orderCaptor;

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
						useCase.execute(new OrderPaymentRequest(ORDER_ID, null), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenAlreadyPaidOrder_thenThrowsIllegalArgumentException(){
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
				.serviceId("01SERVICEDRYCLEAN00000000")
				.serviceName("Dry Clean Jas")
				.unit(ServiceUnit.ITEM)
				.unitPrice(MoneyDomain.of(35000L))
				.quantity(1)
				.subtotal(MoneyDomain.of(35000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(35000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.PAID)
				.status(OrderStatus.READY)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderPaymentRequest(ORDER_ID, PaymentMethod.CASH), principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Order is already paid"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenCancelledOrder_thenThrowsInvalidOrderStatusException(){
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
				.serviceId("01SERVICEDRYCLEAN00000000")
				.serviceName("Dry Clean Jas")
				.unit(ServiceUnit.ITEM)
				.unitPrice(MoneyDomain.of(35000L))
				.quantity(1)
				.subtotal(MoneyDomain.of(35000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(35000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.CANCELLED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderPaymentRequest(ORDER_ID, null), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("A cancelled order cannot be paid"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenUnpaidOrder_thenMarksItPaid(){
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
				.serviceId("01SERVICEDRYCLEAN00000000")
				.serviceName("Dry Clean Jas")
				.unit(ServiceUnit.ITEM)
				.unitPrice(MoneyDomain.of(35000L))
				.quantity(2)
				.subtotal(MoneyDomain.of(70000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(70000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.OUT_FOR_DELIVERY)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200))
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

		useCase.execute(new OrderPaymentRequest(ORDER_ID, PaymentMethod.CASH), principal, presenter);

		then(gateway).should()
				.findById(eq(new OrderIdDomain(ORDER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(any(OrderPaymentResponse.class));

		OrderDomain saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.paymentStatus()).isEqualTo(PaymentStatus.PAID);
			softly.then(saved.status()).isEqualTo(OrderStatus.OUT_FOR_DELIVERY);
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
		});
	}

}
