package com.ferry.user.core.staff.names;

import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.names.StaffNameProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffNamesUseCaseTest{

	private static final String TENANT_ID = "01TENANTKAMPUNGBAHARI00";
	private static final String CALLER_ID = "01STAFFDEWIKARTIKA000000";
	private static final String SITI_ID = "01STAFFSITIAMINAH0000000";
	private static final String AGUS_ID = "01STAFFAGUSSALIM00000000";

	@Mock
	StaffNamesGateway gateway;
	@InjectMocks
	DefaultStaffNamesUseCase useCase;
	@Mock
	StaffNamesPresenter presenter;
	@Captor
	ArgumentCaptor<Set<String>> idsCaptor;
	@Captor
	ArgumentCaptor<StaffNamesResponse> responseCaptor;

	@Test
	void givenTwoIdsAndABlankOne_thenLooksUpOnlyTheTrimmedIdsInTheCallersTenant(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		List<StaffNameProjection> names = List.of(new StaffNameProjection(SITI_ID, "siti aminah"),
				new StaffNameProjection(AGUS_ID, "agus salim"));
		willReturn(names).given(gateway)
				.findNames(any(TenantId.class), anySet());
		Set<String> requested = new HashSet<>(Set.of(' ' + SITI_ID + ' ', AGUS_ID, "   "));

		useCase.execute(new StaffNamesRequest(requested), principal, presenter);

		then(gateway).should()
				.findNames(eq(new TenantId(TENANT_ID)), idsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(idsCaptor.getValue()).containsExactlyInAnyOrder(SITI_ID, AGUS_ID);
			softly.then(responseCaptor.getValue().staffs()).isEqualTo(names);
		});
	}

	@Test
	void givenNoIds_thenAnswersEmptyWithoutQuerying(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();

		useCase.execute(new StaffNamesRequest(null), principal, presenter);

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().staffs()).isEmpty());
	}

	@Test
	void givenMoreThanAHundredIds_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(CALLER_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Set<String> tooMany = IntStream.rangeClosed(1, 101)
				.mapToObj(i -> "01STAFFTERLALUBANYAK" + i)
				.collect(Collectors.toSet());

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffNamesRequest(tooMany), principal,
						presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenPrincipalWithoutTenant_thenThrowsInvalidUserStateException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(CALLER_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(
						new StaffNamesRequest(Set.of(SITI_ID)), principal, presenter))
				.isInstanceOf(InvalidUserStateException.class));

		then(gateway).shouldHaveNoInteractions();
	}

}
