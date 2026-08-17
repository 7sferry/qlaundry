package com.ferry.user.core.tenant.expiration;

import com.ferry.user.core.tenant.constant.TenantExpirationConstant;
import com.ferry.user.domain.tenant.TenantIdDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultTenantExpirationUseCaseTest{

	@Mock
	TenantExpirationGateway gateway;
	@InjectMocks
	DefaultTenantExpirationUseCase useCase;
	@Captor
	ArgumentCaptor<Instant> cutoffCaptor;

	@Test
	void givenNoPendingTenantsPastCutoff_thenExpiresNoneAndReturnsZero(){
		willReturn(List.of()).given(gateway).findPendingOlderThan(any(Instant.class));

		int expired = useCase.execute();

		thenSoftly(softly -> softly.then(expired).isEqualTo(0));
		then(gateway).should(never()).expire(any());
	}

	@Test
	void givenPendingTenantsPastCutoff_thenExpiresEachOneAndReturnsCount(){
		TenantIdDomain first = new TenantIdDomain("tnt-cirebon-01");
		TenantIdDomain second = new TenantIdDomain("tnt-cirebon-02");
		willReturn(List.of(first, second)).given(gateway).findPendingOlderThan(any(Instant.class));

		int expired = useCase.execute();

		thenSoftly(softly -> softly.then(expired).isEqualTo(2));
		then(gateway).should().expire(first);
		then(gateway).should().expire(second);
	}

	@Test
	void givenExecute_thenQueriesGatewayWithCutoffAtLeastThirtyDaysAgo(){
		willReturn(List.of()).given(gateway).findPendingOlderThan(cutoffCaptor.capture());

		Instant beforeCall = Instant.now().minus(TenantExpirationConstant.PENDING_EXPIRY_DURATION);
		useCase.execute();
		Instant afterCall = Instant.now().minus(TenantExpirationConstant.PENDING_EXPIRY_DURATION);

		thenSoftly(softly -> {
			softly.then(cutoffCaptor.getValue()).isAfterOrEqualTo(beforeCall);
			softly.then(cutoffCaptor.getValue()).isBeforeOrEqualTo(afterCall);
		});
	}

}
