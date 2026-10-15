package com.ferry.user.core.customer.names;

import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.customer.names.CustomerNameProjection;
import com.ferry.user.domain.staff.StaffRole;
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
class DefaultCustomerNamesUseCaseTest{

	private static final String TENANT_ID = "01TENANTBOROBUDUR0000000";
	private static final String STAFF_ID = "01STAFFGALIHPERMANA00000";
	private static final String CUSTOMER_ID = "01CUSTOMERRATNASARI00000";
	private static final String SECOND_CUSTOMER_ID = "01CUSTOMERBENNYSUSANTO00";

	@Mock
	CustomerNamesGateway gateway;
	@InjectMocks
	DefaultCustomerNamesUseCase useCase;
	@Mock
	CustomerNamesPresenter presenter;
	@Captor
	ArgumentCaptor<Set<String>> idsCaptor;
	@Captor
	ArgumentCaptor<CustomerNamesResponse> responseCaptor;

	@Test
	void givenTwoIdsAndABlankOne_thenLooksUpOnlyTheTrimmedIdsInTheCallersTenant(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		List<CustomerNameProjection> names = List.of(new CustomerNameProjection(CUSTOMER_ID, "ratna sari"),
				new CustomerNameProjection(SECOND_CUSTOMER_ID, "benny susanto"));
		willReturn(names).given(gateway)
				.findNames(any(TenantId.class), anySet());
		Set<String> requested = new HashSet<>(Set.of(' ' + CUSTOMER_ID + ' ', SECOND_CUSTOMER_ID, "   "));

		useCase.execute(new CustomerNamesRequest(requested), principal, presenter);

		then(gateway).should()
				.findNames(eq(new TenantId(TENANT_ID)), idsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(idsCaptor.getValue()).containsExactlyInAnyOrder(CUSTOMER_ID, SECOND_CUSTOMER_ID);
			softly.then(responseCaptor.getValue().customers()).isEqualTo(names);
		});
	}

	@Test
	void givenNoIds_thenAnswersEmptyWithoutQuerying(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.build();

		useCase.execute(new CustomerNamesRequest(null), principal, presenter);

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> softly.then(responseCaptor.getValue().customers()).isEmpty());
	}

	@Test
	void givenMoreThanAHundredIds_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Set<String> tooMany = IntStream.rangeClosed(1, 101)
				.mapToObj(i -> "01CUSTOMERKEBANYAKAN" + i)
				.collect(Collectors.toSet());

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new CustomerNamesRequest(tooMany), principal,
						presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenPrincipalWithoutTenant_thenThrowsInvalidUserStateException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.userId(STAFF_ID)
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(
						new CustomerNamesRequest(Set.of(CUSTOMER_ID)), principal, presenter))
				.isInstanceOf(InvalidUserStateException.class));

		then(gateway).shouldHaveNoInteractions();
	}

}
