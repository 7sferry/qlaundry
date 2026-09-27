package com.ferry.analytics.core.event.order;

import com.ferry.analytics.core.event.order.OrderEventRequest.OrderEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.OrderItemSnapshot;
import com.ferry.analytics.domain.event.OrderPromotionSnapshot;
import com.ferry.analytics.domain.event.OrderSnapshot;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTCEMPAKAWANGI00000";
	private static final String ORDER_ID = "01ORDERJASKULITCOKLAT0000";
	private static final String SECOND_ORDER_ID = "01ORDERSELIMUTTEBAL000000";
	private static final String EVENT_ID = "01EVENTORDERSTATUS0000000";
	private static final String SECOND_EVENT_ID = "01EVENTORDERBARU000000000";

	@Mock
	OrderEventGateway gateway;
	@InjectMocks
	DefaultOrderEventUseCase useCase;
	@Mock
	OrderEventPresenter presenter;
	@Captor
	ArgumentCaptor<List<OrderSnapshot>> ordersCaptor;
	@Captor
	ArgumentCaptor<List<OrderItemSnapshot>> itemsCaptor;
	@Captor
	ArgumentCaptor<List<OrderPromotionSnapshot>> promotionsCaptor;
	@Captor
	ArgumentCaptor<List<ConsumedEvent>> consumedCaptor;
	@Captor
	ArgumentCaptor<OrderEventResponse> responseCaptor;

	@Test
	void givenNoEventList_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new OrderEventRequest(null), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTwoOrdersInOneBatch_thenWritesEachTableOnceForTheWholeBatch(){
		Instant now = Instant.now();
		OrderSnapshot jacket = OrderSnapshot.builder()
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
		OrderItemSnapshot jeans = OrderItemSnapshot.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.itemId("01ITEMCELANAJEANS00000000")
				.type("PANTS")
				.label("celana jeans")
				.quantity(3)
				.version(0)
				.createdAt(now)
				.build();
		OrderPromotionSnapshot merdeka = OrderPromotionSnapshot.builder()
				.tenantId(TENANT_ID)
				.orderId(ORDER_ID)
				.promotionId("01PROMOMERDEKA00000000000")
				.code("MERDEKA17")
				.discountAmount(BigDecimal.valueOf(5000))
				.version(0)
				.createdAt(now)
				.build();
		OrderSnapshot blanket = OrderSnapshot.builder()
				.tenantId(TENANT_ID)
				.orderId(SECOND_ORDER_ID)
				.orderNumber("INV-20260914-Q5C8LM")
				.serviceId("01SERVICEBEDCOVER00000000")
				.serviceName("Bed Cover")
				.unit("ITEM")
				.unitPrice(BigDecimal.valueOf(30000))
				.quantity(1)
				.subtotal(BigDecimal.valueOf(30000))
				.discount(BigDecimal.ZERO)
				.totalPrice(BigDecimal.valueOf(30000))
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
		OrderItemSnapshot bedCover = OrderItemSnapshot.builder()
				.tenantId(TENANT_ID)
				.orderId(SECOND_ORDER_ID)
				.itemId("01ITEMSELIMUTTEBAL0000000")
				.type("BED_LINEN")
				.label("selimut tebal")
				.quantity(1)
				.version(0)
				.createdAt(now)
				.build();

		useCase.execute(new OrderEventRequest(List.of(
				new OrderEvent(EVENT_ID, "ORDER_STATUS_CHANGED", jacket, List.of(jeans), List.of(merdeka)),
				new OrderEvent(SECOND_EVENT_ID, "ORDER_CREATED", blanket, List.of(bedCover), List.of()))), presenter);

		then(gateway).should()
				.upsert(ordersCaptor.capture(), itemsCaptor.capture(), promotionsCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<ConsumedEvent> consumed = consumedCaptor.getValue();
		OrderEventResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(ordersCaptor.getValue()).containsExactly(jacket, blanket);
			softly.then(itemsCaptor.getValue()).containsExactly(jeans, bedCover);
			softly.then(promotionsCaptor.getValue()).containsExactly(merdeka);
			softly.then(consumed).extracting(ConsumedEvent::eventId).containsExactly(EVENT_ID, SECOND_EVENT_ID);
			softly.then(consumed).extracting(ConsumedEvent::aggregate).containsOnly(AnalyticsAggregate.ORDER);
			softly.then(consumed).extracting(ConsumedEvent::type)
					.containsExactly("ORDER_STATUS_CHANGED", "ORDER_CREATED");
			softly.then(consumed.getFirst().version()).isEqualTo(2);
			softly.then(response.applied()).hasSize(2);
			softly.then(response.applied().getFirst().itemCount()).isEqualTo(1);
			softly.then(response.applied().getFirst().promotionCount()).isEqualTo(1);
			softly.then(response.applied().getLast().orderId()).isEqualTo(SECOND_ORDER_ID);
			softly.then(response.rejected()).isEmpty();
		});
	}

	@Test
	void givenItemOfAnotherOrder_thenRejectsOnlyThatEventAndStillAppliesTheRest(){
		Instant now = Instant.now();
		OrderSnapshot washFold = OrderSnapshot.builder()
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
		OrderItemSnapshot strayItem = OrderItemSnapshot.builder()
				.tenantId(TENANT_ID)
				.orderId("01ORDERLAINMILIKTETANGGA0")
				.itemId("01ITEMKEMEJABATIK00000000")
				.type("SHIRT")
				.label("kemeja batik")
				.quantity(2)
				.version(0)
				.createdAt(now)
				.build();
		OrderSnapshot dryClean = OrderSnapshot.builder()
				.tenantId(TENANT_ID)
				.orderId(SECOND_ORDER_ID)
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

		useCase.execute(new OrderEventRequest(List.of(
				new OrderEvent(EVENT_ID, "ORDER_STATUS_CHANGED", washFold, List.of(strayItem), List.of()),
				new OrderEvent(SECOND_EVENT_ID, "ORDER_CREATED", dryClean, List.of(), List.of()))), presenter);

		then(gateway).should()
				.upsert(ordersCaptor.capture(), itemsCaptor.capture(), promotionsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		OrderEventResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(ordersCaptor.getValue()).containsExactly(dryClean);
			softly.then(itemsCaptor.getValue()).isEmpty();
			softly.then(response.applied()).extracting(OrderEventResponse.AppliedOrderEvent::eventId)
					.containsExactly(SECOND_EVENT_ID);
			softly.then(response.rejected()).hasSize(1);
			softly.then(response.rejected().getFirst().eventId()).isEqualTo(EVENT_ID);
			softly.then(response.rejected().getFirst().reason()).contains("01ITEMKEMEJABATIK00000000");
		});
	}

	@Test
	void givenEveryEventInTheBatchIsInvalid_thenNeverWritesAndReportsThemAllAsRejected(){
		Instant now = Instant.now();
		OrderSnapshot sneaker = OrderSnapshot.builder()
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
				.status("CONFIRMED")
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200L))
				.createdAt(now)
				.updatedAt(now)
				.version(1)
				.build();

		useCase.execute(new OrderEventRequest(List.of(
				new OrderEvent(" ", "ORDER_STATUS_CHANGED", sneaker, List.of(), List.of()),
				new OrderEvent(SECOND_EVENT_ID, null, sneaker, List.of(), List.of()))), presenter);

		then(gateway).should(never())
				.upsert(anyList(), anyList(), anyList());
		then(gateway).should(never())
				.recordConsumed(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().applied()).isEmpty();
			softly.then(responseCaptor.getValue().rejected())
					.extracting(OrderEventResponse.RejectedOrderEvent::eventId)
					.containsExactly(" ", SECOND_EVENT_ID);
		});
	}

	@Test
	void givenAnEventTypeTheConsumerHasNeverSeen_thenItIsStillTheSameUpsert(){
		Instant now = Instant.now();
		OrderSnapshot sneaker = OrderSnapshot.builder()
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

		useCase.execute(new OrderEventRequest(List.of(
				new OrderEvent(EVENT_ID, "ORDER_REFUNDED", sneaker, List.of(), List.of()))), presenter);

		then(gateway).should()
				.upsert(ordersCaptor.capture(), itemsCaptor.capture(), promotionsCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());

		thenSoftly(softly -> {
			softly.then(ordersCaptor.getValue()).containsExactly(sneaker);
			softly.then(consumedCaptor.getValue().getFirst().type()).isEqualTo("ORDER_REFUNDED");
			softly.then(consumedCaptor.getValue().getFirst().tenantId()).isEqualTo(TENANT_ID);
		});
	}

}
