package com.ferry.analytics.core.event.order;

import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEventDomain;
import com.ferry.analytics.domain.event.OrderItemSnapshotDomain;
import com.ferry.analytics.domain.event.OrderPromotionSnapshotDomain;
import com.ferry.analytics.domain.event.OrderSnapshotDomain;
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
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTCEMPAKAWANGI00000";
	private static final String ORDER_ID = "01ORDERJASKULITCOKLAT0000";
	private static final String EVENT_ID = "01EVENTORDERSTATUS0000000";

	@Mock
	OrderEventGateway gateway;
	@InjectMocks
	DefaultOrderEventUseCase useCase;
	@Mock
	OrderEventPresenter presenter;
	@Captor
	ArgumentCaptor<OrderSnapshotDomain> orderCaptor;
	@Captor
	ArgumentCaptor<List<OrderItemSnapshotDomain>> itemsCaptor;
	@Captor
	ArgumentCaptor<List<OrderPromotionSnapshotDomain>> promotionsCaptor;
	@Captor
	ArgumentCaptor<ConsumedEventDomain> consumedCaptor;
	@Captor
	ArgumentCaptor<OrderEventResponse> responseCaptor;

	@Test
	void givenBlankEventId_thenThrowsConstraintViolationException(){
		Instant now = Instant.now();
		OrderSnapshotDomain order = OrderSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.orderNumber("INV-20260914-K3M9QX")
				.serviceId("01SERVICEDRYCLEAN00000000")
				.serviceName("Dry Cleaning")
				.unit("ITEM")
				.unitPrice(BigDecimal.valueOf(35000))
				.quantity(1)
				.subtotal(BigDecimal.valueOf(35000))
				.discount(BigDecimal.ZERO)
				.totalPrice(BigDecimal.valueOf(35000))
				.priority("NORMAL")
				.paymentMethod("CASH")
				.paymentStatus("UNPAID")
				.status("PENDING")
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200L))
				.createdAt(now)
				.updatedAt(now)
				.version(0)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderEventRequest(" ", "ORDER_CREATED", order, List.of(), List.of()),
								presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenItemOfAnotherOrder_thenThrowsIllegalArgumentExceptionWithoutWriting(){
		Instant now = Instant.now();
		OrderSnapshotDomain order = OrderSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.orderNumber("INV-20260914-P7D2WA")
				.serviceId("01SERVICEWASHFOLD00000000")
				.serviceName("Wash & Fold")
				.unit("KG")
				.unitPrice(BigDecimal.valueOf(8000))
				.quantity(1)
				.weightKg(3.5)
				.subtotal(BigDecimal.valueOf(28000))
				.discount(BigDecimal.ZERO)
				.totalPrice(BigDecimal.valueOf(28000))
				.priority("NORMAL")
				.paymentMethod("CASH")
				.paymentStatus("UNPAID")
				.status("CONFIRMED")
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400L))
				.createdAt(now)
				.updatedAt(now)
				.version(1)
				.build();
		OrderItemSnapshotDomain strayItem = OrderItemSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId("01ORDERLAINMILIKTETANGGA0")
				.itemId("01ITEMKEMEJABATIK00000000")
				.type("SHIRT")
				.label("kemeja batik")
				.quantity(2)
				.version(0)
				.createdAt(now)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderEventRequest(EVENT_ID, "ORDER_STATUS_CHANGED", order,
								List.of(strayItem), List.of()), presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("01ITEMKEMEJABATIK00000000"));

		then(gateway).should(never())
				.upsert(any(OrderSnapshotDomain.class), anyList(), anyList());
		then(gateway).should(never())
				.recordConsumed(any(ConsumedEventDomain.class));
	}

	@Test
	void givenOrderSnapshotWithItemsAndPromotions_thenUpsertsAllAndRecordsTheConsumedEvent(){
		Instant now = Instant.now();
		OrderSnapshotDomain order = OrderSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.orderNumber("INV-20260914-B8N4TZ")
				.customerId("01CUSTOMERRINAMARLINA0000")
				.serviceId("01SERVICEWASHIRON00000000")
				.serviceName("Wash & Iron")
				.unit("KG")
				.unitPrice(BigDecimal.valueOf(12000))
				.quantity(1)
				.weightKg(4.0)
				.subtotal(BigDecimal.valueOf(48000))
				.discount(BigDecimal.valueOf(5000))
				.totalPrice(BigDecimal.valueOf(43000))
				.priority("EXPRESS")
				.paymentMethod("CASH")
				.paymentStatus("PAID")
				.status("PICKED_UP")
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(172800L))
				.createdAt(now)
				.updatedAt(now)
				.version(2)
				.build();
		OrderItemSnapshotDomain item = OrderItemSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.itemId("01ITEMCELANAJEANS00000000")
				.type("PANTS")
				.label("celana jeans")
				.quantity(3)
				.version(0)
				.createdAt(now)
				.build();
		OrderPromotionSnapshotDomain promotion = OrderPromotionSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.promotionId("01PROMOMERDEKA00000000000")
				.code("MERDEKA17")
				.discountAmount(BigDecimal.valueOf(5000))
				.version(0)
				.createdAt(now)
				.build();

		useCase.execute(new OrderEventRequest(EVENT_ID, "ORDER_CREATED", order, List.of(item), List.of(promotion)),
				presenter);

		then(gateway).should()
				.upsert(orderCaptor.capture(), itemsCaptor.capture(), promotionsCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		ConsumedEventDomain consumed = consumedCaptor.getValue();
		OrderEventResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(orderCaptor.getValue()).isEqualTo(order);
			softly.then(itemsCaptor.getValue()).containsExactly(item);
			softly.then(promotionsCaptor.getValue()).containsExactly(promotion);
			softly.then(consumed.eventId()).isEqualTo(EVENT_ID);
			softly.then(consumed.aggregate()).isEqualTo(AnalyticsAggregate.ORDER);
			softly.then(consumed.type()).isEqualTo("ORDER_CREATED");
			softly.then(consumed.aggregateId()).isEqualTo(ORDER_ID);
			softly.then(consumed.version()).isEqualTo(2);
			softly.then(response.itemCount()).isEqualTo(1);
			softly.then(response.promotionCount()).isEqualTo(1);
			softly.then(response.version()).isEqualTo(2);
		});
	}

	@Test
	void givenAnEventTypeTheConsumerHasNeverSeen_thenItIsStillTheSameUpsert(){
		Instant now = Instant.now();
		OrderSnapshotDomain order = OrderSnapshotDomain.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.orderNumber("INV-20260914-H2R6VC")
				.serviceId("01SERVICESNEAKER000000000")
				.serviceName("Sneaker Cleaning")
				.unit("ITEM")
				.unitPrice(BigDecimal.valueOf(50000))
				.quantity(2)
				.subtotal(BigDecimal.valueOf(100000))
				.discount(BigDecimal.ZERO)
				.totalPrice(BigDecimal.valueOf(100000))
				.priority("NORMAL")
				.paymentMethod("CASH")
				.paymentStatus("UNPAID")
				.status("CANCELLED")
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200L))
				.createdAt(now)
				.updatedAt(now)
				.version(5)
				.build();

		useCase.execute(new OrderEventRequest(EVENT_ID, "ORDER_REFUNDED", order, List.of(), List.of()), presenter);

		then(gateway).should()
				.upsert(eq(order), eq(List.of()), eq(List.of()));
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());

		thenSoftly(softly -> {
			softly.then(consumedCaptor.getValue().type()).isEqualTo("ORDER_REFUNDED");
			softly.then(consumedCaptor.getValue().tenantId()).isEqualTo(TENANT_ID);
		});
	}

}
