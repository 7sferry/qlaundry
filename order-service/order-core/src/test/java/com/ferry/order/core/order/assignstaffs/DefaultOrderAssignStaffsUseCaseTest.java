package com.ferry.order.core.order.assignstaffs;

import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStaff;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderAssignStaffsUseCaseTest{

	private static final String TENANT_ID = "01TENANTLAUNDRYSEHAT0000";
	private static final String CALLER_ID = "01STAFFRATNASARI00000000";
	private static final String ORDER_ID = "01ORDERSELIMUT0000000000";
	private static final String ORDER_NUMBER = "INV-20261017-4KD9ZP";
	private static final String JOKO_ID = "01STAFFJOKOWIDODO0000000";
	private static final String SINTA_ID = "01STAFFSINTADEWI00000000";
	private static final String RIZKY_ID = "01STAFFRIZKYFIRMAN000000";

	@Mock
	OrderAssignStaffsGateway gateway;
	@Mock
	OrderAssignStaffsPresenter presenter;
	@Captor
	ArgumentCaptor<List<OrderStaff>> staffsCaptor;
	@Captor
	ArgumentCaptor<OrderAssignStaffsResponse> responseCaptor;
	@InjectMocks
	DefaultOrderAssignStaffsUseCase useCase;

	@Test
	void givenBlankOrderId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderAssignStaffsRequest(" ", Set.of(JOKO_ID)), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenOrderNotFound_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderAssignStaffsRequest(ORDER_ID, Set.of(JOKO_ID)), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.saveStaffs(anyList());
	}

	@Test
	void givenOrderNotInProgress_thenThrowsInvalidOrderStatusException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("wulan sari"))
				.customerPhone(new Phone("+6281233445566"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(12000L))
				.quantity(3)
				.weightKg(3.0)
				.subtotal(Money.of(36000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(36000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.READY)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(CALLER_ID)
				.updatedAt(now)
				.updatedBy(CALLER_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderAssignStaffsRequest(ORDER_ID, Set.of(JOKO_ID)), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("Staff can only be changed while the order is in progress"));

		then(gateway).should(never())
				.saveStaffs(anyList());
		then(presenter).should(never())
				.present(any(OrderAssignStaffsResponse.class));
	}

	@Test
	void givenNoStaffIds_thenReplacesEveryoneWithTheCurrentStaff(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("wulan sari"))
				.customerPhone(new Phone("+6281233445566"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(12000L))
				.quantity(3)
				.weightKg(3.0)
				.subtotal(Money.of(36000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(36000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.IN_PROGRESS)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(CALLER_ID)
				.updatedAt(now)
				.updatedBy(CALLER_ID)
				.build();
		OrderStaff joko = new OrderStaff("01ORDERSTAFFJOKO00000000", ORDER_ID, JOKO_ID, 0, false, now, CALLER_ID, now,
				CALLER_ID);
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(joko)).given(gateway)
				.findAllStaffsByOrderId(any(OrderId.class));
		willAnswer(invocation -> invocation.<List<OrderStaff>>getArgument(0)).given(gateway)
				.saveStaffs(anyList());

		useCase.execute(new OrderAssignStaffsRequest(ORDER_ID, null), principal, presenter);

		then(gateway).should()
				.lockById(eq(new OrderId(ORDER_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.saveStaffs(staffsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(staffsCaptor.getValue()).extracting(OrderStaff::staffId).containsExactly(JOKO_ID, CALLER_ID);
			softly.then(staffsCaptor.getValue()).extracting(OrderStaff::deleted).containsExactly(true, false);
			softly.then(responseCaptor.getValue().staffs()).extracting(OrderStaff::staffId)
					.containsExactly(CALLER_ID);
		});
	}

	@Test
	void givenSwappedStaff_thenKeepsRemovesAddsAndRestoresInOneReplace(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("wulan sari"))
				.customerPhone(new Phone("+6281233445566"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(12000L))
				.quantity(3)
				.weightKg(3.0)
				.subtotal(Money.of(36000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(36000L))
				.priority(OrderPriority.EXPRESS)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.IN_PROGRESS)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(43200))
				.deleted(false)
				.createdAt(now)
				.createdBy(CALLER_ID)
				.updatedAt(now)
				.updatedBy(CALLER_ID)
				.build();
		OrderStaff joko = new OrderStaff("01ORDERSTAFFJOKO00000000", ORDER_ID, JOKO_ID, 0, false, now, CALLER_ID, now,
				CALLER_ID);
		OrderStaff sinta = new OrderStaff("01ORDERSTAFFSINTA0000000", ORDER_ID, SINTA_ID, 0, false, now, CALLER_ID, now,
				CALLER_ID);
		OrderStaff rizkyBefore = new OrderStaff("01ORDERSTAFFRIZKY0000000", ORDER_ID, RIZKY_ID, 1, true, now, CALLER_ID,
				now, CALLER_ID);
		Set<String> requested = new LinkedHashSet<>(List.of(' ' + RIZKY_ID, JOKO_ID, " "));
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(joko, sinta, rizkyBefore)).given(gateway)
				.findAllStaffsByOrderId(any(OrderId.class));
		willAnswer(invocation -> invocation.<List<OrderStaff>>getArgument(0)).given(gateway)
				.saveStaffs(anyList());

		useCase.execute(new OrderAssignStaffsRequest(ORDER_ID, requested), principal, presenter);

		then(gateway).should()
				.saveStaffs(staffsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(staffsCaptor.getValue()).extracting(OrderStaff::staffId).containsExactly(SINTA_ID, RIZKY_ID);
			softly.then(staffsCaptor.getValue()).extracting(OrderStaff::deleted).containsExactly(true, false);
			softly.then(staffsCaptor.getValue().getLast().id()).isEqualTo(rizkyBefore.id());
			softly.then(staffsCaptor.getValue()).extracting(OrderStaff::updatedBy).containsOnly(CALLER_ID);
			softly.then(responseCaptor.getValue().staffs()).extracting(OrderStaff::staffId)
					.containsExactly(JOKO_ID, RIZKY_ID);
		});
	}

	@Test
	void givenSameStaff_thenSavesNothing(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("wulan sari"))
				.customerPhone(new Phone("+6281233445566"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(12000L))
				.quantity(3)
				.weightKg(3.0)
				.subtotal(Money.of(36000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(36000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.IN_PROGRESS)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(CALLER_ID)
				.updatedAt(now)
				.updatedBy(CALLER_ID)
				.build();
		OrderStaff sinta = new OrderStaff("01ORDERSTAFFSINTA0000000", ORDER_ID, SINTA_ID, 0, false, now, CALLER_ID, now,
				CALLER_ID);
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(sinta)).given(gateway)
				.findAllStaffsByOrderId(any(OrderId.class));

		useCase.execute(new OrderAssignStaffsRequest(ORDER_ID, Set.of(SINTA_ID)), principal, presenter);

		then(gateway).should(never())
				.saveStaffs(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().staffs()).containsExactly(sinta));
	}

}
