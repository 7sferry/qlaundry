package com.ferry.user.core.staff.logout;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import jakarta.validation.ConstraintViolationException;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffLogoutUseCaseTest{

	private static final String REFRESH_TOKEN = "logout-rt-raw-3344";
	private static final String HASHED_REFRESH_TOKEN = "logout-rt-hash-3344";
	private static final String USER_ID = "usr-667788";

	@Mock
	StaffLogoutGateway gateway;
	@Mock
	TokenProcessor tokenProcessor;
	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultStaffLogoutUseCase useCase;
	@Mock
	StaffLogoutPresenter presenter;
	@Captor
	ArgumentCaptor<UserSessionDomain> sessionCaptor;

	@Test
	void givenBlankRefreshToken_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffLogoutRequest(" "), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(tokenProcessor).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenSessionNotFound_thenPresentsByeMessageWithoutFurtherActions(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		willReturn(Optional.empty()).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);

		useCase.execute(new StaffLogoutRequest(REFRESH_TOKEN), presenter);

		then(gateway).should(never()).save(any());
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).should().present(new StaffLogoutResponse("bye!"));
	}

	@Test
	void givenSessionTypeIsNotStaff_thenPresentsByeMessageWithoutClearingCache(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.CUSTOMER);
		willReturn(Optional.of(session)).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);

		useCase.execute(new StaffLogoutRequest(REFRESH_TOKEN), presenter);

		then(gateway).should(never()).save(any());
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).should().present(new StaffLogoutResponse("bye!"));
	}

	@Test
	void givenSessionNotYetExpired_thenExpiresSessionAndClearsAllCaches(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().plusSeconds(3600), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);

		useCase.execute(new StaffLogoutRequest(REFRESH_TOKEN), presenter);

		then(gateway).should().save(sessionCaptor.capture());
		then(cacheManager).should().delete(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN);
		then(cacheManager).should().delete(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		then(cacheManager).should().delete(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN);
		then(presenter).should().present(new StaffLogoutResponse("bye!"));

		thenSoftly(softly -> softly.then(sessionCaptor.getValue().expirationTime()).isBeforeOrEqualTo(Instant.now()));
	}

	@Test
	void givenSessionAlreadyExpired_thenSkipsExpiringButStillClearsCaches(){
		willReturn(HASHED_REFRESH_TOKEN).given(tokenProcessor).hashToken(REFRESH_TOKEN);
		UserSessionDomain session = UserSessionDomain.create(HASHED_REFRESH_TOKEN, Instant.now().minusSeconds(60), USER_ID, SessionType.STAFF);
		willReturn(Optional.of(session)).given(gateway).findSessionById(HASHED_REFRESH_TOKEN);

		useCase.execute(new StaffLogoutRequest(REFRESH_TOKEN), presenter);

		then(gateway).should(never()).save(any());
		then(cacheManager).should().delete(TokenConstant.ROTATED_KEY + HASHED_REFRESH_TOKEN);
		then(cacheManager).should().delete(TokenConstant.ACCESS_KEY + HASHED_REFRESH_TOKEN);
		then(cacheManager).should().delete(TokenConstant.REFRESH_KEY + HASHED_REFRESH_TOKEN);
		then(presenter).should().present(new StaffLogoutResponse("bye!"));
	}

}
