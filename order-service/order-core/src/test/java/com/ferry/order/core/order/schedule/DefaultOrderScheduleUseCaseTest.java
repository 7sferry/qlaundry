package com.ferry.order.core.order.schedule;

import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.domain.order.schedule.OrderScheduleType;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderScheduleUseCaseTest{

	private static final String TENANT_ID = "01TENANTASTERUNGU00000000";
	private static final String STAFF_ID = "01STAFFFAJARNUGROHO000000";

	@Mock
	OrderScheduleGateway gateway;
	@Mock
	OrderSchedulePresenter presenter;
	@Captor
	ArgumentCaptor<OrderScheduleResponse> responseCaptor;

	@Test
	void givenImpossibleCalendarDate_thenThrowsDateTimeParseException(){
		DefaultOrderScheduleUseCase useCase = new DefaultOrderScheduleUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T02:00:00Z"), ZoneOffset.UTC));
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderScheduleRequest(LocalDate.parse("2026-02-30")), principal, presenter))
				.isInstanceOf(DateTimeParseException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenNoDateAndNoTenantTimeZone_thenUsesTodayInUtcAndMergesPickupsAndDeliveriesByTime(){
		DefaultOrderScheduleUseCase useCase = new DefaultOrderScheduleUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-13T18:30:00Z"), ZoneOffset.UTC));
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderScheduleProjection pickup = new OrderScheduleProjection("01ORDERJEMPUTKEBAYA000000",
				"INV-20260914-J3K8PL", "ayu lestari", Instant.parse("2026-09-14T03:00:00Z"),
				OrderStatus.CONFIRMED.getValue());
		OrderScheduleProjection delivery = new OrderScheduleProjection("01ORDERANTARGORDEN0000000",
				"INV-20260912-D7Q2MX", "reza firmansyah", Instant.parse("2026-09-14T01:30:00Z"),
				OrderStatus.OUT_FOR_DELIVERY.getValue());
		willReturn(List.of(pickup)).given(gateway)
				.findPickupsBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());
		willReturn(List.of(delivery)).given(gateway)
				.findDeliveriesBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());

		useCase.execute(new OrderScheduleRequest(null), principal, presenter);

		then(gateway).should()
				.findPickupsBetween(eq(new TenantId(TENANT_ID)), eq(Instant.parse("2026-09-13T00:00:00Z")),
						eq(Instant.parse("2026-09-14T00:00:00Z")),
						eq(Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED)));
		then(gateway).should()
				.findDeliveriesBetween(eq(new TenantId(TENANT_ID)), eq(Instant.parse("2026-09-13T00:00:00Z")),
						eq(Instant.parse("2026-09-14T00:00:00Z")),
						eq(Set.of(OrderStatus.READY, OrderStatus.OUT_FOR_DELIVERY)));
		then(presenter).should()
				.present(responseCaptor.capture());

		OrderScheduleResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.date()).isEqualTo(LocalDate.of(2026, 9, 13));
			softly.then(response.items()).hasSize(2);
			softly.then(response.items().getFirst().type()).isEqualTo(OrderScheduleType.DELIVERY);
			softly.then(response.items().getFirst().customerName()).isEqualTo("reza firmansyah");
			softly.then(response.items().getLast().type()).isEqualTo(OrderScheduleType.PICKUP);
			softly.then(response.items().getLast().status()).isEqualTo(OrderStatus.CONFIRMED);
		});
	}

	@Test
	void givenNoDateAndTenantTimeZone_thenUsesTodayInThatZoneInstead(){
		DefaultOrderScheduleUseCase useCase = new DefaultOrderScheduleUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-13T18:30:00Z"), ZoneOffset.UTC));
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Jakarta"))
				.role(StaffRole.STAFF)
				.build();
		willReturn(List.of()).given(gateway)
				.findPickupsBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());
		willReturn(List.of()).given(gateway)
				.findDeliveriesBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());

		useCase.execute(new OrderScheduleRequest(null), principal, presenter);

		then(gateway).should()
				.findPickupsBetween(eq(new TenantId(TENANT_ID)), eq(Instant.parse("2026-09-13T17:00:00Z")),
						eq(Instant.parse("2026-09-14T17:00:00Z")),
						eq(Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED)));
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().date()).isEqualTo(LocalDate.of(2026, 9, 14)));
	}

	@Test
	void givenExplicitDateAndNoTenantTimeZone_thenQueriesThatUtcDay(){
		DefaultOrderScheduleUseCase useCase = new DefaultOrderScheduleUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T02:00:00Z"), ZoneOffset.UTC));
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(List.of()).given(gateway)
				.findPickupsBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());
		willReturn(List.of()).given(gateway)
				.findDeliveriesBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());

		useCase.execute(new OrderScheduleRequest(LocalDate.parse("2026-12-31")), principal, presenter);

		then(gateway).should()
				.findPickupsBetween(eq(new TenantId(TENANT_ID)), eq(Instant.parse("2026-12-31T00:00:00Z")),
						eq(Instant.parse("2027-01-01T00:00:00Z")), anyCollection());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().date()).isEqualTo(LocalDate.of(2026, 12, 31));
			softly.then(responseCaptor.getValue().items()).isEmpty();
		});
	}

	@Test
	void givenExplicitDateAndTenantTimeZone_thenQueriesThatZonesDay(){
		DefaultOrderScheduleUseCase useCase = new DefaultOrderScheduleUseCase(gateway,
				Clock.fixed(Instant.parse("2026-09-14T02:00:00Z"), ZoneOffset.UTC));
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.timeZone(ZoneId.of("Asia/Jakarta"))
				.role(StaffRole.SUPER_STAFF)
				.build();
		willReturn(List.of()).given(gateway)
				.findPickupsBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());
		willReturn(List.of()).given(gateway)
				.findDeliveriesBetween(any(TenantId.class), any(Instant.class), any(Instant.class),
						anyCollection());

		useCase.execute(new OrderScheduleRequest(LocalDate.parse("2026-12-31")), principal, presenter);

		then(gateway).should()
				.findPickupsBetween(eq(new TenantId(TENANT_ID)), eq(Instant.parse("2026-12-30T17:00:00Z")),
						eq(Instant.parse("2026-12-31T17:00:00Z")), anyCollection());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().date()).isEqualTo(LocalDate.of(2026, 12, 31));
			softly.then(responseCaptor.getValue().items()).isEmpty();
		});
	}

}
