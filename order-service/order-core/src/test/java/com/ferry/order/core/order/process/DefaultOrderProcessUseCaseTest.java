package com.ferry.order.core.order.process;

import com.ferry.order.core.analytics.OrderAnalyticsEvent;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.OrderAnalyticsAggregate;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;
import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.common.exception.MachineBusyException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.domain.machine.MachineType;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPriority;
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
import static org.mockito.ArgumentMatchers.anySet;
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
class DefaultOrderProcessUseCaseTest{

	private static final String TENANT_ID = "01TENANTCEMPAKA000000000";
	private static final String STAFF_ID = "01STAFFSITINURHALIZA0000";
	private static final String ORDER_ID = "01ORDERGORDEN00000000000";
	private static final String ORDER_NUMBER = "INV-20260821-6TB4WK";
	private static final String OTHER_ORDER_NUMBER = "INV-20260821-7XK2MQ";
	private static final String WASHER_ID = "01MACHINEWASHERSATU00000";
	private static final String DRYER_ID = "01MACHINEDRYERDUA0000000";

	@Mock
	OrderProcessGateway gateway;
	@Mock
	OrderAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<OrderAnalyticsEvent> analyticsCaptor;
	@InjectMocks
	DefaultOrderProcessUseCase useCase;
	@Mock
	OrderProcessPresenter presenter;
	@Captor
	ArgumentCaptor<Order> orderCaptor;
	@Captor
	ArgumentCaptor<OrderProcessResponse> responseCaptor;
	@Captor
	ArgumentCaptor<List<OrderMachine>> machinesCaptor;
	@Captor
	ArgumentCaptor<Set<MachineId>> machineIdsCaptor;

	@Test
	void givenBlankOrderId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderProcessRequest("", null, null), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenOrderNotFound_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderProcessRequest(ORDER_ID, null, null), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.save(any(Order.class));
		then(presenter).should(never())
				.present(any(OrderProcessResponse.class));
	}

	@Test
	void givenReadyOrder_thenProcessingItThrowsInvalidOrderStatusException(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.EXPRESS)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.READY)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(43200))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderProcessRequest(ORDER_ID, "already ready", null), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("Cannot change order status from READY to IN_PROGRESS"));

		then(gateway).should(never())
				.save(any(Order.class));
	}

	@Test
	void givenConfirmedOrder_thenMarksItInProgressAndSavesTheChange(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.EXPRESS)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.CONFIRMED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(43200))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<Order>getArgument(0)).given(gateway)
				.save(any(Order.class));
		willReturn(com.ferry.order.domain.analytics.OrderAnalyticsEvent.create(OrderAnalyticsAggregate.ORDER, OrderAnalyticsEventType.ORDER_STATUS_CHANGED,
				TENANT_ID, ORDER_ID, 1, "{}", STAFF_ID)).given(publisher)
				.save(any(OrderAnalyticsEvent.class));

		useCase.execute(new OrderProcessRequest(ORDER_ID, "washing started", null), principal, presenter);

		then(publisher).should()
				.save(analyticsCaptor.capture());
		then(publisher).should()
				.publish(any(com.ferry.order.domain.analytics.OrderAnalyticsEvent.class));
		then(gateway).should()
				.lockById(eq(new OrderId(ORDER_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		Order saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(analyticsCaptor.getValue().type()).isEqualTo(OrderAnalyticsEventType.ORDER_STATUS_CHANGED);
			softly.then(analyticsCaptor.getValue().aggregate()).isEqualTo(OrderAnalyticsAggregate.ORDER);
			softly.then(((OrderAnalyticsMessage) analyticsCaptor.getValue().payload()).status())
					.isEqualTo(OrderStatus.IN_PROGRESS);
			softly.then(saved.status()).isEqualTo(OrderStatus.IN_PROGRESS);
			softly.then(saved.completedAt()).isNull();
			softly.then(saved.staffNotesValue()).isEqualTo("washing started");
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
			softly.then(responseCaptor.getValue().order().status()).isEqualTo(OrderStatus.IN_PROGRESS);
		});
	}

	@Test
	void givenPickedUpOrder_thenMarksItInProgressAndSavesTheChange(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.EXPRESS)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PICKED_UP)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(43200))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<Order>getArgument(0)).given(gateway)
				.save(any(Order.class));

		useCase.execute(new OrderProcessRequest(ORDER_ID, "sorting the gorden", null), principal, presenter);

		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());
		then(gateway).should(never())
				.lockMachines(anySet(), any(TenantId.class));
		then(gateway).should(never())
				.findOccupancies(anySet(), any(OrderId.class));
		then(gateway).should(never())
				.saveMachines(anyList());

		Order saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.status()).isEqualTo(OrderStatus.IN_PROGRESS);
			softly.then(saved.staffNotesValue()).isEqualTo("sorting the gorden");
			softly.then(responseCaptor.getValue().order().status()).isEqualTo(OrderStatus.IN_PROGRESS);
			softly.then(responseCaptor.getValue().machines()).isEmpty();
		});
	}

	@Test
	void givenBlankMachineIdsOnly_thenProcessesWithoutMachines(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PICKED_UP)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willAnswer(invocation -> invocation.<Order>getArgument(0)).given(gateway)
				.save(any(Order.class));

		useCase.execute(new OrderProcessRequest(ORDER_ID, null, Set.of(" ", "")), principal, presenter);

		then(gateway).should(never())
				.lockMachines(anySet(), any(TenantId.class));
		then(gateway).should(never())
				.saveMachines(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().order().status()).isEqualTo(OrderStatus.IN_PROGRESS);
			softly.then(responseCaptor.getValue().machines()).isEmpty();
		});
	}

	@Test
	void givenUnknownMachine_thenThrowsNotFoundExceptionAndSavesNothing(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PICKED_UP)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Machine washer = Machine.builder()
				.id(WASHER_ID)
				.tenantId(TENANT_ID)
				.name("Mesin Cuci 1")
				.type(MachineType.WASHER)
				.capacityKg(8.0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(washer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(
						new OrderProcessRequest(ORDER_ID, null, Set.of(WASHER_ID, DRYER_ID)), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Machine Not Found"));

		then(gateway).should(never())
				.save(any(Order.class));
		then(gateway).should(never())
				.saveMachines(anyList());
	}

	@Test
	void givenInactiveMachine_thenThrowsNotFoundException(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.CONFIRMED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Machine retiredDryer = Machine.builder()
				.id(DRYER_ID)
				.tenantId(TENANT_ID)
				.name("Pengering Lama")
				.type(MachineType.DRYER)
				.active(false)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(retiredDryer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(
						new OrderProcessRequest(ORDER_ID, null, Set.of(DRYER_ID)), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Machine Not Found"));

		then(gateway).should(never())
				.findOccupancies(anySet(), any(OrderId.class));
		then(gateway).should(never())
				.save(any(Order.class));
	}

	@Test
	void givenMachineBusyOnAnotherOrder_thenThrowsMachineBusyExceptionNamingThatOrder(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PICKED_UP)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Machine washer = Machine.builder()
				.id(WASHER_ID)
				.tenantId(TENANT_ID)
				.name("Mesin Cuci 1")
				.type(MachineType.WASHER)
				.capacityKg(8.0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(washer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));
		willReturn(List.of(new MachineOccupancy(WASHER_ID, OTHER_ORDER_NUMBER))).given(gateway)
				.findOccupancies(anySet(), any(OrderId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(
						new OrderProcessRequest(ORDER_ID, null, Set.of(WASHER_ID)), principal, presenter))
				.isInstanceOf(MachineBusyException.class)
				.hasMessage("Machine Mesin Cuci 1 is in use by " + OTHER_ORDER_NUMBER));

		then(gateway).should()
				.findOccupancies(anySet(), eq(new OrderId(ORDER_ID)));
		then(gateway).should(never())
				.save(any(Order.class));
		then(gateway).should(never())
				.saveMachines(anyList());
	}

	@Test
	void givenAvailableMachines_thenAssignsEachWithANameSnapshot(){
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
				.customerName(new FullName("teguh santoso"))
				.customerPhone(new Phone("+6281377889900"))
				.serviceId("01SERVICEGORDEN000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.KG)
				.unitPrice(Money.of(15000L))
				.quantity(4)
				.weightKg(4.0)
				.subtotal(Money.of(60000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(60000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.PICKED_UP)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(86400))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Machine washer = Machine.builder()
				.id(WASHER_ID)
				.tenantId(TENANT_ID)
				.name("Mesin Cuci 1")
				.type(MachineType.WASHER)
				.capacityKg(8.0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Machine dryer = Machine.builder()
				.id(DRYER_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 2")
				.type(MachineType.DRYER)
				.capacityKg(10.0)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Set<String> machineIds = new LinkedHashSet<>(List.of(WASHER_ID, ' ' + DRYER_ID + ' ', WASHER_ID));
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(dryer, washer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));
		willReturn(List.of()).given(gateway)
				.findOccupancies(anySet(), any(OrderId.class));
		willAnswer(invocation -> invocation.<Order>getArgument(0)).given(gateway)
				.save(any(Order.class));
		willAnswer(invocation -> invocation.<List<OrderMachine>>getArgument(0)).given(gateway)
				.saveMachines(anyList());

		useCase.execute(new OrderProcessRequest(ORDER_ID, "two loads", machineIds), principal, presenter);

		then(gateway).should()
				.lockMachines(machineIdsCaptor.capture(), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.saveMachines(machinesCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<OrderMachine> assigned = machinesCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(machineIdsCaptor.getValue())
					.containsExactly(new MachineId(WASHER_ID), new MachineId(DRYER_ID));
			softly.then(assigned).extracting(OrderMachine::machineId).containsExactly(WASHER_ID, DRYER_ID);
			softly.then(assigned).extracting(OrderMachine::machineName).containsExactly("Mesin Cuci 1", "Pengering 2");
			softly.then(assigned).extracting(OrderMachine::machineType)
					.containsExactly(MachineType.WASHER, MachineType.DRYER);
			softly.then(assigned).extracting(OrderMachine::orderId).containsOnly(ORDER_ID);
			softly.then(assigned).extracting(OrderMachine::createdBy).containsOnly(STAFF_ID);
			softly.then(responseCaptor.getValue().machines()).isEqualTo(assigned);
			softly.then(responseCaptor.getValue().order().status()).isEqualTo(OrderStatus.IN_PROGRESS);
		});
	}

}
