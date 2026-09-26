package com.ferry.order.core.order.detail;

import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderPromotion;
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
import java.util.List;
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
class DefaultOrderDetailUseCaseTest{

	private static final String TENANT_ID = "01TENANTFLAMBOYAN000000000";
	private static final String STAFF_ID = "01STAFFAGUSSANTOSO000000000";
	private static final String ORDER_ID = "01ORDERSEPRAI0000000000000";
	private static final String ORDER_NUMBER = "INV-20260812-9KP1XZ";
	private static final String CUSTOMER_NAME = "made wirawan";
	private static final String CUSTOMER_PHONE = "+6281299981234";

	@Mock
	OrderDetailGateway gateway;
	@InjectMocks
	DefaultOrderDetailUseCase useCase;
	@Mock
	OrderDetailPresenter presenter;
	@Captor
	ArgumentCaptor<OrderDetailResponse> responseCaptor;

	@Test
	void givenBlankOrderId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderDetailRequest request = new OrderDetailRequest("  ");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(OrderDetailResponse.class));
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
						useCase.execute(new OrderDetailRequest(ORDER_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.findItemsByOrderId(any(OrderId.class));
		then(presenter).should(never())
				.present(any(OrderDetailResponse.class));
	}

	@Test
	void givenOrderFound_thenPresentsOrderWithItems(){
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
				.customerName(new FullName(CUSTOMER_NAME))
				.customerPhone(new Phone(CUSTOMER_PHONE))
				.serviceId("01SERVICESEPRAI00000000000")
				.serviceName("Cuci Seprai")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(8000L))
				.quantity(1)
				.weightKg(4.0d)
				.subtotal(Money.of(32000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(32000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.IN_PROGRESS)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(172800))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		List<OrderItem> items = List.of(OrderItem.builder()
				.id("01ITEMSEPRAI000000000000000")
				.orderId(ORDER_ID)
				.type(ClothingType.BED_LINEN)
				.label("Seprai Queen")
				.quantity(2)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build());
		List<OrderPromotion> promotions = List.of(OrderPromotion.builder()
				.id("01ORDERPROMOSEPRAI00000000")
				.orderId(ORDER_ID)
				.promotionId("01PROMODISKONSEPRAI0000000")
				.code("SEPRAI20")
				.discountAmount(Money.of(9000L))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build());
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderId.class), any(TenantId.class));
		willReturn(items).given(gateway)
				.findItemsByOrderId(any(OrderId.class));
		willReturn(promotions).given(gateway)
				.findPromotionsByOrderId(any(OrderId.class));

		useCase.execute(new OrderDetailRequest(ORDER_ID), principal, presenter);

		then(gateway).should()
				.findById(eq(new OrderId(ORDER_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.findItemsByOrderId(eq(new OrderId(ORDER_ID)));
		then(gateway).should()
				.findPromotionsByOrderId(eq(new OrderId(ORDER_ID)));
		then(presenter).should()
				.present(responseCaptor.capture());

		OrderDetailResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.order()).isEqualTo(order);
			softly.then(response.items()).isEqualTo(items);
			softly.then(response.promotions()).isEqualTo(promotions);
			softly.then(response.promotions().getFirst().code()).isEqualTo("SEPRAI20");
		});
	}

}
