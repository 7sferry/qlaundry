package com.ferry.order.core.machine.update;

import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.MachineBusyException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineType;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyString;
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
class DefaultMachineUpdateUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENANGA0000000000";
	private static final String STAFF_ID = "01STAFFBUDIHARTONO0000000";
	private static final String MACHINE_ID = "01MACHINEPENGERING000000";
	private static final String ORDER_NUMBER = "INV-20261016-3QZ8HN";

	@Mock
	MachineUpdateGateway gateway;
	@InjectMocks
	DefaultMachineUpdateUseCase useCase;
	@Mock
	MachineUpdatePresenter presenter;
	@Captor
	ArgumentCaptor<Machine> machineCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		MachineUpdateRequest request = new MachineUpdateRequest(MACHINE_ID, "Pengering 3", MachineType.DRYER,
				null, true);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Only super staff can manage machines"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenMachineNotFound_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		MachineUpdateRequest request = new MachineUpdateRequest(MACHINE_ID, "Pengering 3", MachineType.DRYER,
				null, true);
		willReturn(Optional.empty()).given(gateway)
				.lockById(any(MachineId.class), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Machine Not Found"));

		then(gateway).should(never())
				.save(any(Machine.class));
	}

	@Test
	void givenNameTakenByAnotherMachine_thenThrowsInvalidOrderStateException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Machine existing = Machine.builder()
				.id(MACHINE_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 3")
				.type(MachineType.DRYER)
				.active(true)
				.version(2)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		MachineUpdateRequest request = new MachineUpdateRequest(MACHINE_ID, "Pengering 1", MachineType.DRYER,
				null, true);
		willReturn(Optional.of(existing)).given(gateway)
				.lockById(any(MachineId.class), any(TenantId.class));
		willReturn(true).given(gateway)
				.existsByNameExcept(anyString(), any(TenantId.class), any(MachineId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidOrderStateException.class)
				.hasMessage("Machine name already exists"));

		then(gateway).should(never())
				.save(any(Machine.class));
	}

	@Test
	void givenDeactivatingABusyMachine_thenThrowsMachineBusyException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Machine existing = Machine.builder()
				.id(MACHINE_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 3")
				.type(MachineType.DRYER)
				.active(true)
				.version(2)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		MachineUpdateRequest request = new MachineUpdateRequest(MACHINE_ID, "Pengering 3", MachineType.DRYER,
				null, false);
		willReturn(Optional.of(existing)).given(gateway)
				.lockById(any(MachineId.class), any(TenantId.class));
		willReturn(false).given(gateway)
				.existsByNameExcept(anyString(), any(TenantId.class), any(MachineId.class));
		willReturn(Optional.of(ORDER_NUMBER)).given(gateway)
				.findCurrentOrderNumber(any(MachineId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(MachineBusyException.class)
				.hasMessage("Machine Pengering 3 is in use by " + ORDER_NUMBER + " and cannot be deactivated"));

		then(gateway).should(never())
				.save(any(Machine.class));
	}

	@Test
	void givenDeactivatingAnIdleMachine_thenSavesItInactive(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Machine existing = Machine.builder()
				.id(MACHINE_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 3")
				.type(MachineType.DRYER)
				.active(true)
				.version(2)
				.deleted(false)
				.createdAt(now)
				.createdBy("01STAFFPEMBUAT00000000000")
				.updatedAt(now)
				.updatedBy("01STAFFPEMBUAT00000000000")
				.build();
		MachineUpdateRequest request = new MachineUpdateRequest(MACHINE_ID, "Pengering 3 (rusak)", MachineType.DRYER,
				"drum noisy", false);
		willReturn(Optional.of(existing)).given(gateway)
				.lockById(any(MachineId.class), any(TenantId.class));
		willReturn(false).given(gateway)
				.existsByNameExcept(anyString(), any(TenantId.class), any(MachineId.class));
		willReturn(Optional.empty()).given(gateway)
				.findCurrentOrderNumber(any(MachineId.class));
		willAnswer(invocation -> invocation.<Machine>getArgument(0)).given(gateway)
				.save(any(Machine.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.lockById(eq(new MachineId(MACHINE_ID)), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.existsByNameExcept(eq("Pengering 3 (rusak)"), eq(new TenantId(TENANT_ID)), eq(new MachineId(MACHINE_ID)));
		then(gateway).should()
				.save(machineCaptor.capture());

		Machine saved = machineCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.active()).isFalse();
			softly.then(saved.name()).isEqualTo("Pengering 3 (rusak)");
			softly.then(saved.description()).isEqualTo(new Note("drum noisy"));
			softly.then(saved.version()).isEqualTo(2);
			softly.then(saved.createdBy()).isEqualTo("01STAFFPEMBUAT00000000000");
			softly.then(saved.updatedBy()).isEqualTo(STAFF_ID);
		});
	}

	@Test
	void givenRenamingABusyMachineAndKeepingItActive_thenSkipsTheBusyCheck(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		Machine existing = Machine.builder()
				.id(MACHINE_ID)
				.tenantId(TENANT_ID)
				.name("Pengering 3")
				.type(MachineType.DRYER)
				.active(true)
				.version(5)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		MachineUpdateRequest request = new MachineUpdateRequest(MACHINE_ID, "Pengering Besar", MachineType.DRYER,
				null, null);
		willReturn(Optional.of(existing)).given(gateway)
				.lockById(any(MachineId.class), any(TenantId.class));
		willReturn(false).given(gateway)
				.existsByNameExcept(anyString(), any(TenantId.class), any(MachineId.class));
		willAnswer(invocation -> invocation.<Machine>getArgument(0)).given(gateway)
				.save(any(Machine.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should(never())
				.findCurrentOrderNumber(any(MachineId.class));
		then(gateway).should()
				.save(machineCaptor.capture());

		thenSoftly(softly -> {
			softly.then(machineCaptor.getValue().active()).isTrue();
			softly.then(machineCaptor.getValue().name()).isEqualTo("Pengering Besar");
		});
	}

}
