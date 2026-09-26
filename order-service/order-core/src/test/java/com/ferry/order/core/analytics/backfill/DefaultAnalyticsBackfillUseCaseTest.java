package com.ferry.order.core.analytics.backfill;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.AnalyticsOutboxConstant;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEvent;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultAnalyticsBackfillUseCaseTest{

	private static final String TENANT_ID = "01TENANTKAMBOJAHARUM00000";
	private static final String STAFF_ID = "01STAFFIRAWANSETIADI00000";
	private static final String ORDER_ID = "01ORDERSARUNGBANTAL000000";
	private static final String SERVICE_ID = "01SERVICECUCISELIMUT00000";
	private static final int BATCH_SIZE = 500;

	@Mock
	AnalyticsBackfillGateway gateway;
	@Mock
	OrderAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<AnalyticsEventConfig> configCaptor;

	@Test
	void givenOneTenantWithAServiceAndAnOrder_thenReplaysBothThroughTheOutboxAtTheirCurrentVersion(){
		Instant createdAt = Instant.now().minusSeconds(7776000L);
		LaundryService service = LaundryService.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Selimut")
				.description(new Note("blanket wash"))
				.pricePerUnit(Money.of(30000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.SPECIALTY)
				.estimatedHours(48)
				.expressMultiplier(1.5d)
				.popular(true)
				.active(true)
				.version(6)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber("INV-20260612-Q4Z7NB"))
				.tenantId(TENANT_ID)
				.customerId("01CUSTOMERWULANDARI000000")
				.customerName(new FullName("wulan dari"))
				.customerPhone(new Phone("+6281299887766"))
				.serviceId(SERVICE_ID)
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(30000L))
				.quantity(2)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.PAID)
				.status(OrderStatus.COMPLETED)
				.pickupAt(createdAt)
				.estimatedDeliveryAt(createdAt.plusSeconds(172800L))
				.completedAt(createdAt.plusSeconds(172800L))
				.version(5)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt.plusSeconds(172800L))
				.updatedBy(STAFF_ID)
				.build();
		OrderItem item = OrderItem.builder()
				.id("01ITEMSELIMUTTEBAL0000000")
				.orderId(ORDER_ID)
				.type(ClothingType.BED_LINEN)
				.label("selimut tebal")
				.quantity(2)
				.version(0)
				.deleted(false)
				.createdAt(createdAt)
				.createdBy(STAFF_ID)
				.updatedAt(createdAt)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(List.of(service)).given(gateway)
				.findServicesAfter(anyString(), isNull(), anyInt());
		willReturn(List.of(order)).given(gateway)
				.findOrdersAfter(anyString(), isNull(), anyInt());
		willReturn(List.of(item)).given(gateway)
				.findItemsByOrderId(any(OrderId.class));
		willReturn(List.of()).given(gateway)
				.findPromotionsByOrderId(any(OrderId.class));
		willReturn(AnalyticsEvent.create(AnalyticsAggregate.ORDER, AnalyticsEventType.ORDER_BACKFILLED,
				TENANT_ID, ORDER_ID, 5, "{}", AnalyticsOutboxConstant.BACKFILL_ACTOR)).given(publisher)
				.save(any(AnalyticsEventConfig.class));
		DefaultAnalyticsBackfillUseCase useCase = new DefaultAnalyticsBackfillUseCase(gateway, publisher, BATCH_SIZE);

		AnalyticsBackfillResponse response = useCase.execute(new AnalyticsBackfillRequest(' ' + TENANT_ID + ' '));

		then(gateway).should()
				.findServicesAfter(eq(TENANT_ID), isNull(), eq(BATCH_SIZE));
		then(gateway).should()
				.findOrdersAfter(eq(TENANT_ID), isNull(), eq(BATCH_SIZE));
		then(gateway).should()
				.findItemsByOrderId(eq(new OrderId(ORDER_ID)));
		then(publisher).should(times(2))
				.save(configCaptor.capture());
		then(publisher).should(times(2))
				.publish(any(AnalyticsEvent.class));

		AnalyticsEventConfig serviceEvent = configCaptor.getAllValues().getFirst();
		AnalyticsEventConfig orderEvent = configCaptor.getAllValues().getLast();

		thenSoftly(softly -> {
			softly.then(serviceEvent.type()).isEqualTo(AnalyticsEventType.LAUNDRY_SERVICE_BACKFILLED);
			softly.then(serviceEvent.aggregateVersion()).isEqualTo(6);
			softly.then(((LaundryServiceAnalyticsMessage) serviceEvent.payload()).name()).isEqualTo("Cuci Selimut");
			softly.then(orderEvent.type()).isEqualTo(AnalyticsEventType.ORDER_BACKFILLED);
			softly.then(orderEvent.aggregateId()).isEqualTo(ORDER_ID);
			softly.then(orderEvent.aggregateVersion()).isEqualTo(5);
			softly.then(orderEvent.actor()).isEqualTo(AnalyticsOutboxConstant.BACKFILL_ACTOR);
			softly.then(((OrderAnalyticsMessage) orderEvent.payload()).items()).hasSize(1);
			softly.then(((OrderAnalyticsMessage) orderEvent.payload()).status()).isEqualTo(OrderStatus.COMPLETED);
			softly.then(response.orders()).isEqualTo(1);
			softly.then(response.services()).isEqualTo(1);
		});
	}

	@Test
	void givenNoTenantAndAnEmptyDatabase_thenScansEveryTenantAndPublishesNothing(){
		willReturn(List.of()).given(gateway)
				.findServicesAfter(isNull(), isNull(), anyInt());
		willReturn(List.of()).given(gateway)
				.findOrdersAfter(isNull(), isNull(), anyInt());
		DefaultAnalyticsBackfillUseCase useCase = new DefaultAnalyticsBackfillUseCase(gateway, publisher, BATCH_SIZE);

		AnalyticsBackfillResponse response = useCase.execute(new AnalyticsBackfillRequest(""));

		then(publisher).should(never())
				.save(any(AnalyticsEventConfig.class));
		then(gateway).should(never())
				.findItemsByOrderId(any(OrderId.class));

		thenSoftly(softly -> {
			softly.then(response.orders()).isZero();
			softly.then(response.services()).isZero();
		});
	}

}
