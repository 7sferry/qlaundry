package com.ferry.order.core.order.schedule;

import com.ferry.order.core.order.schedule.OrderScheduleResponse.Item;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.domain.order.schedule.OrderScheduleType;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderScheduleUseCase implements OrderScheduleUseCase{
	private static final Set<OrderStatus> PICKUP_STATUSES = Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);
	private static final Set<OrderStatus> DELIVERY_STATUSES = Set.of(OrderStatus.READY, OrderStatus.OUT_FOR_DELIVERY);

	private final OrderScheduleGateway gateway;
	private final Clock clock;

	@Override
	public void execute(OrderScheduleRequest request, OrderAuthPrincipal principal, OrderSchedulePresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		ZoneId zone = resolveZone(request.zone());
		LocalDate date = resolveDate(request.date(), zone);
		Instant from = date.atStartOfDay(zone).toInstant();
		Instant to = date.plusDays(1).atStartOfDay(zone).toInstant();
		Stream<Item> pickups = gateway.findPickupsBetween(tenantId, from, to, PICKUP_STATUSES).stream()
				.map(order -> item(order, OrderScheduleType.PICKUP));
		Stream<Item> deliveries = gateway.findDeliveriesBetween(tenantId, from, to, DELIVERY_STATUSES).stream()
				.map(order -> item(order, OrderScheduleType.DELIVERY));
		List<Item> items = Stream.concat(pickups, deliveries)
				.sorted(Comparator.comparing(Item::scheduledAt).thenComparing(Item::orderId))
				.toList();
		presenter.present(new OrderScheduleResponse(date, items));
	}

	private ZoneId resolveZone(String zone){
		return zone == null || zone.isBlank() ? ZoneOffset.UTC : ZoneId.of(zone);
	}

	private LocalDate resolveDate(LocalDate date, ZoneId zone){
		if(date == null){
			return LocalDate.ofInstant(clock.instant(), zone);
		}
		return date;
	}

	private Item item(OrderScheduleProjection order, OrderScheduleType type){
		return new Item(order.orderId(), order.orderNumber(), order.customerName(), type, order.scheduledAt(),
				order.status());
	}

}
