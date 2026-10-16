package com.ferry.order.core.order.assignmachines;

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
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderAssignMachinesUseCaseTest{

	private static final String TENANT_ID = "01TENANTSEROJA00000000000";
	private static final String STAFF_ID = "01STAFFDEWISARTIKA0000000";
	private static final String ORDER_ID = "01ORDERSELIMUT00000000000";
	private static final String ORDER_NUMBER = "INV-20261016-4MN7RT";
	private static final String WASHER_ID = "01MACHINEWASHERTIGA000000";
	private static final String DRYER_ID = "01MACHINEDRYEREMPAT000000";
	private static final String IRONER_ID = "01MACHINEIRONERLIMA000000";

	@Mock
	OrderAssignMachinesGateway gateway;
	@InjectMocks
	DefaultOrderAssignMachinesUseCase useCase;
	@Mock
	OrderAssignMachinesPresenter presenter;
	@Captor
	ArgumentCaptor<List<OrderMachine>> machinesCaptor;
	@Captor
	ArgumentCaptor<Set<MachineId>> machineIdsCaptor;
	@Captor
	ArgumentCaptor<OrderAssignMachinesResponse> responseCaptor;

	@Test
	void givenNullMachineIds_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, null), principal, presenter))
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
						useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of(WASHER_ID)), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.saveMachines(anyList());
	}

	@Test
	void givenOrderNotInProgress_thenThrowsInvalidOrderStatusException(){
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
				.customerName(new FullName("angel wijaya"))
				.customerPhone(new Phone("+6285711223344"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(25000L))
				.quantity(2)
				.subtotal(Money.of(50000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(50000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.READY)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(172800))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of(WASHER_ID)), principal, presenter))
				.isInstanceOf(InvalidOrderStatusException.class)
				.hasMessage("Machines can only be changed while the order is in progress"));

		then(gateway).should(never())
				.findAllMachinesByOrderId(any(OrderId.class));
		then(gateway).should(never())
				.saveMachines(anyList());
	}

	@Test
	void givenAddedMachineBusyElsewhere_thenThrowsMachineBusyException(){
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
				.customerName(new FullName("angel wijaya"))
				.customerPhone(new Phone("+6285711223344"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(25000L))
				.quantity(2)
				.subtotal(Money.of(50000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(50000L))
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
		Machine dryer = Machine.builder()
				.id(DRYER_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 4")
				.type(MachineType.DRYER)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of()).given(gateway)
				.findAllMachinesByOrderId(any(OrderId.class));
		willReturn(List.of(dryer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));
		willReturn(List.of(new MachineOccupancy(DRYER_ID, "INV-20261016-8BC1XY"))).given(gateway)
				.findOccupancies(anySet(), any(OrderId.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of(DRYER_ID)), principal, presenter))
				.isInstanceOf(MachineBusyException.class)
				.hasMessage("Machine Pengering 4 is in use by INV-20261016-8BC1XY"));

		then(gateway).should()
				.findOccupancies(anySet(), eq(new OrderId(ORDER_ID)));
		then(gateway).should(never())
				.saveMachines(anyList());
	}

	@Test
	void givenWasherKeptDryerAddedIronerDropped_thenOnlyChecksTheAddedMachine(){
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
				.customerName(new FullName("angel wijaya"))
				.customerPhone(new Phone("+6285711223344"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(25000L))
				.quantity(2)
				.subtotal(Money.of(50000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(50000L))
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
		OrderMachine keptWasher = new OrderMachine("01ORDERMACHINECUCI0000000", ORDER_ID, WASHER_ID,
				"Mesin Cuci 3", MachineType.WASHER, 0, false, now, STAFF_ID, now, STAFF_ID);
		OrderMachine droppedIroner = new OrderMachine("01ORDERMACHINESETRIKA0000", ORDER_ID, IRONER_ID,
				"Setrika 5", MachineType.IRONER, 0, false, now, STAFF_ID, now, STAFF_ID);
		Machine dryer = Machine.builder()
				.id(DRYER_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 4")
				.type(MachineType.DRYER)
				.capacityKg(9d)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(keptWasher, droppedIroner)).given(gateway)
				.findAllMachinesByOrderId(any(OrderId.class));
		willReturn(List.of(dryer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));
		willReturn(List.of()).given(gateway)
				.findOccupancies(anySet(), any(OrderId.class));
		willAnswer(invocation -> invocation.<List<OrderMachine>>getArgument(0)).given(gateway)
				.saveMachines(anyList());

		useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of(WASHER_ID, DRYER_ID)), principal, presenter);

		then(gateway).should()
				.lockMachines(machineIdsCaptor.capture(), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.saveMachines(machinesCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<OrderMachine> changes = machinesCaptor.getValue();
		List<OrderMachine> result = responseCaptor.getValue().machines();

		thenSoftly(softly -> {
			softly.then(machineIdsCaptor.getValue()).containsExactly(new MachineId(DRYER_ID));
			softly.then(changes).hasSize(2);
			softly.then(changes.getFirst().id()).isEqualTo("01ORDERMACHINESETRIKA0000");
			softly.then(changes.getFirst().deleted()).isTrue();
			softly.then(changes.getFirst().updatedBy()).isEqualTo(STAFF_ID);
			softly.then(changes.getLast().id()).isNull();
			softly.then(changes.getLast().machineId()).isEqualTo(DRYER_ID);
			softly.then(changes.getLast().machineName()).isEqualTo("Pengering 4");
			softly.then(changes.getLast().orderId()).isEqualTo(ORDER_ID);
			softly.then(result).extracting(OrderMachine::machineId).containsExactly(WASHER_ID, DRYER_ID);
			softly.then(responseCaptor.getValue().order()).isEqualTo(order);
		});
	}

	@Test
	void givenEmptySet_thenRemovesEveryMachineWithoutLocking(){
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
				.customerName(new FullName("angel wijaya"))
				.customerPhone(new Phone("+6285711223344"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(25000L))
				.quantity(2)
				.subtotal(Money.of(50000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(50000L))
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
		OrderMachine washer = new OrderMachine("01ORDERMACHINECUCI0000000", ORDER_ID, WASHER_ID,
				"Mesin Cuci 3", MachineType.WASHER, 0, false, now, STAFF_ID, now, STAFF_ID);
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(washer)).given(gateway)
				.findAllMachinesByOrderId(any(OrderId.class));
		willAnswer(invocation -> invocation.<List<OrderMachine>>getArgument(0)).given(gateway)
				.saveMachines(anyList());

		useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of()), principal, presenter);

		then(gateway).should(never())
				.lockMachines(anySet(), any(TenantId.class));
		then(gateway).should()
				.saveMachines(machinesCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(machinesCaptor.getValue()).singleElement()
					.satisfies(machine -> {
						softly.then(machine.machineId()).isEqualTo(WASHER_ID);
						softly.then(machine.deleted()).isTrue();
					});
			softly.then(responseCaptor.getValue().machines()).isEmpty();
		});
	}

	@Test
	void givenSameMachinesAsBefore_thenSavesNothing(){
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
				.customerName(new FullName("angel wijaya"))
				.customerPhone(new Phone("+6285711223344"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(25000L))
				.quantity(2)
				.subtotal(Money.of(50000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(50000L))
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
		OrderMachine washer = new OrderMachine("01ORDERMACHINECUCI0000000", ORDER_ID, WASHER_ID,
				"Mesin Cuci 3", MachineType.WASHER, 0, false, now, STAFF_ID, now, STAFF_ID);
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(washer)).given(gateway)
				.findAllMachinesByOrderId(any(OrderId.class));

		useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of(WASHER_ID)), principal, presenter);

		then(gateway).should(never())
				.lockMachines(anySet(), any(TenantId.class));
		then(gateway).should(never())
				.saveMachines(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().machines()).containsExactly(washer));
	}

	@Test
	void givenPreviouslyRemovedMachineAddedBack_thenRestoresItsRowWithAFreshSnapshot(){
		Instant now = Instant.now();
		Instant removedAt = now.minusSeconds(1800);
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Order order = Order.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumber(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullName("angel wijaya"))
				.customerPhone(new Phone("+6285711223344"))
				.serviceId("01SERVICESELIMUT00000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(Money.of(25000L))
				.quantity(2)
				.subtotal(Money.of(50000L))
				.discount(Money.ZERO)
				.totalPrice(Money.of(50000L))
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
		OrderMachine removedDryer = new OrderMachine("01ORDERMACHINEKERING00000", ORDER_ID, DRYER_ID,
				"Pengering Lama", MachineType.DRYER, 3, true, removedAt, "01STAFFPERTAMA00000000000", removedAt,
				"01STAFFPERTAMA00000000000");
		Machine dryer = Machine.builder()
				.id(DRYER_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 4")
				.type(MachineType.DRYER)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.lockById(any(OrderId.class), any(TenantId.class));
		willReturn(List.of(removedDryer)).given(gateway)
				.findAllMachinesByOrderId(any(OrderId.class));
		willReturn(List.of(dryer)).given(gateway)
				.lockMachines(anySet(), any(TenantId.class));
		willReturn(List.of()).given(gateway)
				.findOccupancies(anySet(), any(OrderId.class));
		willAnswer(invocation -> invocation.<List<OrderMachine>>getArgument(0)).given(gateway)
				.saveMachines(anyList());

		useCase.execute(new OrderAssignMachinesRequest(ORDER_ID, Set.of(DRYER_ID)), principal, presenter);

		then(gateway).should()
				.lockById(eq(new OrderId(ORDER_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.saveMachines(machinesCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(machinesCaptor.getValue()).singleElement()
					.satisfies(machine -> {
						softly.then(machine.id()).isEqualTo("01ORDERMACHINEKERING00000");
						softly.then(machine.deleted()).isFalse();
						softly.then(machine.version()).isEqualTo(3);
						softly.then(machine.machineName()).isEqualTo("Pengering 4");
						softly.then(machine.createdBy()).isEqualTo("01STAFFPERTAMA00000000000");
						softly.then(machine.updatedBy()).isEqualTo(STAFF_ID);
					});
			softly.then(responseCaptor.getValue().machines()).extracting(OrderMachine::machineId)
					.containsExactly(DRYER_ID);
		});
	}

}
