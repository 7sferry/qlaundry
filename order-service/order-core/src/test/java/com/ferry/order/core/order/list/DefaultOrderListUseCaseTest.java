package com.ferry.order.core.order.list;

import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.order.*;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.pagination.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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
class DefaultOrderListUseCaseTest{

	private static final String TENANT_ID = "01TENANTCENDANA00000000000";
	private static final String STAFF_ID = "01STAFFWAHYUPRATAMA000000000";
	private static final String ORDER_ID_1 = "01ORDERSATU0000000000000000";
	private static final String ORDER_ID_2 = "01ORDERDUA00000000000000000";

	@Mock
	OrderListGateway gateway;
	@InjectMocks
	DefaultOrderListUseCase useCase;
	@Mock
	OrderListPresenter presenter;
	@Captor
	ArgumentCaptor<OrderFilter> filterCaptor;
	@Captor
	ArgumentCaptor<OrderListResponse> responseCaptor;

	@Test
	void givenNoFiltersAndNoCursor_thenBuildsFilterWithDefaultsAndTenantScope(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderListRequest request = new OrderListRequest(null, null, null, null, null, null, null, null, null, null);
		willReturn(new CursorFetch<OrderDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(OrderFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());
		then(gateway).should(never())
				.findItemsByOrderIds(any(Set.class));

		OrderFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.tenantId()).isEqualTo(TENANT_ID);
			softly.then(filter.status()).isNull();
			softly.then(filter.priority()).isNull();
			softly.then(filter.customerId()).isNull();
			softly.then(filter.orderNumber()).isNull();
			softly.then(filter.from()).isNull();
			softly.then(filter.to()).isNull();
			softly.then(filter.sortBy()).isEqualTo(OrderListSortBy.ID);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.DESC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.NEXT);
			softly.then(filter.cursor()).isNull();
		});
	}

	@Test
	void givenStatusPriorityCustomerOrderNumberAndDateRangeFilters_thenPassesThemThroughToTheGateway(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		long from = 1755043200000L;
		long to = 1755129600000L;
		OrderListRequest request = new OrderListRequest(OrderStatus.IN_PROGRESS, OrderPriority.EXPRESS,
				"01CUSTOMERRATNA00000000000", "inv-20260813", from, to, null, null, null, null);
		willReturn(new CursorFetch<OrderDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(OrderFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		OrderFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.status()).isEqualTo(OrderStatus.IN_PROGRESS);
			softly.then(filter.priority()).isEqualTo(OrderPriority.EXPRESS);
			softly.then(filter.customerId()).isEqualTo("01CUSTOMERRATNA00000000000");
			softly.then(filter.orderNumber()).isEqualTo("inv-20260813");
			softly.then(filter.from()).isEqualTo(Instant.ofEpochMilli(from));
			softly.then(filter.to()).isEqualTo(Instant.ofEpochMilli(to));
		});
	}

	@Test
	void givenExplicitCursorSortAndDirection_thenDecodesCursorAndAppliesRequestedSortAndDirection(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		String cursorToken = CursorCodec.encode("budi santoso", ORDER_ID_1);
		OrderListRequest request = new OrderListRequest(null, null, null, null, null, null, cursorToken,
				PageDirection.PREV, OrderListSortBy.CUSTOMER_NAME, SortDirection.ASC);
		willReturn(new CursorFetch<OrderDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(OrderFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		OrderFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.cursor()).isEqualTo(new PageCursor("budi santoso", ORDER_ID_1));
			softly.then(filter.sortBy()).isEqualTo(OrderListSortBy.CUSTOMER_NAME);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.ASC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.PREV);
		});
	}

	@Test
	void givenOrdersReturned_thenGroupsItemsByOrderIdAndPresentsResponse(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderDomain order1 = OrderDomain.builder()
				.id(ORDER_ID_1)
				.orderNumber(new OrderNumberDomain("INV-20260813-AB12CD"))
				.tenantId(TENANT_ID)
				.customerName(new FullNameDomain("rina kusuma"))
				.customerPhone(new PhoneDomain("+6281255512345"))
				.serviceId("01SERVICEKAOS0000000000000")
				.serviceName("Cuci Kaos")
				.unit(ServiceUnit.ITEM)
				.unitPrice(MoneyDomain.of(6000L))
				.quantity(3)
				.subtotal(MoneyDomain.of(18000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(18000L))
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
		OrderDomain order2 = OrderDomain.builder()
				.id(ORDER_ID_2)
				.orderNumber(new OrderNumberDomain("INV-20260813-EF34GH"))
				.tenantId(TENANT_ID)
				.customerName(new FullNameDomain("dedi kurniawan"))
				.customerPhone(new PhoneDomain("+6281266623456"))
				.serviceId("01SERVICEJAKET00000000000")
				.serviceName("Dry Clean Jaket")
				.unit(ServiceUnit.ITEM)
				.unitPrice(MoneyDomain.of(30000L))
				.quantity(1)
				.subtotal(MoneyDomain.of(30000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(30000L))
				.priority(OrderPriority.EXPRESS)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.CONFIRMED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(172800))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderItemDomain item1a = OrderItemDomain.builder()
				.id("01ITEMSATU0000000000000000")
				.orderId(ORDER_ID_1)
				.type(ClothingType.SHIRT)
				.label("Kaos Polo")
				.quantity(3)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderItemDomain item2a = OrderItemDomain.builder()
				.id("01ITEMDUA0000000000000000A")
				.orderId(ORDER_ID_2)
				.type(ClothingType.JACKET)
				.label("Jaket Kulit")
				.quantity(1)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderPromotionDomain promotion2a = OrderPromotionDomain.builder()
				.id("01ORDERPROMOKEDUA000000000")
				.orderId(ORDER_ID_2)
				.promotionId("01PROMOAKHIRPEKAN00000000")
				.code("AKHIRPEKAN")
				.discountAmount(MoneyDomain.of(4000L))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderListRequest request = new OrderListRequest(null, null, null, null, null, null, null, null, null, null);
		willReturn(new CursorFetch<>(List.of(order1, order2), false)).given(gateway)
				.findByFilter(any(OrderFilter.class));
		willReturn(List.of(item1a, item2a)).given(gateway)
				.findItemsByOrderIds(eq(Set.of(ORDER_ID_1, ORDER_ID_2)));
		willReturn(List.of(promotion2a)).given(gateway)
				.findPromotionsByOrderIds(eq(Set.of(ORDER_ID_1, ORDER_ID_2)));

		useCase.execute(request, principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		OrderListResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.orders()).containsExactly(order1, order2);
			softly.then(response.itemsByOrderId().get(ORDER_ID_1)).containsExactly(item1a);
			softly.then(response.itemsByOrderId().get(ORDER_ID_2)).containsExactly(item2a);
			softly.then(response.promotionsByOrderId().get(ORDER_ID_1)).isNull();
			softly.then(response.promotionsByOrderId().get(ORDER_ID_2)).containsExactly(promotion2a);
			softly.then(response.nextCursor()).isNull();
			softly.then(response.prevCursor()).isNull();
		});
	}

	@Test
	void givenNoOrdersReturned_thenSkipsItemsLookupAndPresentsEmptyResponse(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderListRequest request = new OrderListRequest(null, null, null, null, null, null, null, null, null, null);
		willReturn(new CursorFetch<OrderDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(OrderFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should(never())
				.findItemsByOrderIds(any(Set.class));
		then(gateway).should(never())
				.findPromotionsByOrderIds(any(Set.class));
		then(presenter).should()
				.present(responseCaptor.capture());

		OrderListResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.orders()).isEmpty();
			softly.then(response.itemsByOrderId()).isEmpty();
			softly.then(response.promotionsByOrderId()).isEmpty();
			softly.then(response.nextCursor()).isNull();
			softly.then(response.prevCursor()).isNull();
		});
	}

}
