package com.ferry.order.core.machine.create;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineType;
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
class DefaultMachineCreateUseCaseTest{

	private static final String TENANT_ID = "01TENANTMELATI00000000000";
	private static final String STAFF_ID = "01STAFFANGELINA0000000000";

	@Mock
	MachineCreateGateway gateway;
	@InjectMocks
	DefaultMachineCreateUseCase useCase;
	@Mock
	MachineCreatePresenter presenter;
	@Captor
	ArgumentCaptor<Machine> machineCaptor;
	@Captor
	ArgumentCaptor<MachineCreateResponse> responseCaptor;

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		MachineCreateRequest request = new MachineCreateRequest("Mesin Cuci 1", MachineType.WASHER, 8.0d, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(OrderForbiddenActionException.class)
				.hasMessage("Only super staff can manage machines"));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenMissingType_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		MachineCreateRequest request = new MachineCreateRequest("Mesin Cuci 1", null, 8.0d, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenNonPositiveCapacity_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		MachineCreateRequest request = new MachineCreateRequest("Pengering 1", MachineType.DRYER, 0d, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenDuplicateName_thenThrowsInvalidOrderStateException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		MachineCreateRequest request = new MachineCreateRequest("Mesin Cuci 1", MachineType.WASHER, 8.0d, null);
		willReturn(true).given(gateway)
				.existsByName(anyString(), any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidOrderStateException.class)
				.hasMessage("Machine name already exists"));

		then(gateway).should(never())
				.save(any(Machine.class));
	}

	@Test
	void givenValidRequest_thenCreatesAnActiveMachineWithTrimmedName(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();
		MachineCreateRequest request = new MachineCreateRequest("  Setrika Uap 2 ", MachineType.IRONER, null,
				"front counter");
		willReturn(false).given(gateway)
				.existsByName(anyString(), any(TenantId.class));
		willAnswer(invocation -> invocation.<Machine>getArgument(0).toBuilder().id("01MACHINESETRIKA000000000").build())
				.given(gateway)
				.save(any(Machine.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.existsByName(eq("Setrika Uap 2"), eq(new TenantId(TENANT_ID)));
		then(gateway).should()
				.save(machineCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		Machine saved = machineCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.tenantId()).isEqualTo(TENANT_ID);
			softly.then(saved.name()).isEqualTo("Setrika Uap 2");
			softly.then(saved.type()).isEqualTo(MachineType.IRONER);
			softly.then(saved.capacityKg()).isNull();
			softly.then(saved.descriptionValue()).isEqualTo("front counter");
			softly.then(saved.active()).isTrue();
			softly.then(saved.deleted()).isFalse();
			softly.then(saved.createdBy()).isEqualTo(STAFF_ID);
			softly.then(responseCaptor.getValue().machine().id()).isEqualTo("01MACHINESETRIKA000000000");
		});
	}

}
