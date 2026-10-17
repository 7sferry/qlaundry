package com.ferry.user.core.tools;

import com.ferry.user.core.staff.constant.TokenConstant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffSessionRevokerTest{

	private static final String STAFF_ID = "stf-revoke-31";
	private static final String SESSION_ONE = "session-hash-aa11";
	private static final String SESSION_TWO = "session-hash-bb22";

	@Mock
	StaffSessionGateway gateway;
	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultStaffSessionRevoker revoker;
	@Captor
	ArgumentCaptor<Instant> nowCaptor;

	@Test
	void givenActiveSessions_thenExpiresThemAndClearsEveryCacheKeyOfEachSession(){
		willReturn(List.of(SESSION_ONE, SESSION_TWO)).given(gateway).expireActiveByUserId(eq(STAFF_ID), nowCaptor.capture());

		revoker.revokeAll(STAFF_ID);

		then(cacheManager).should().delete(TokenConstant.REFRESH_KEY + SESSION_ONE);
		then(cacheManager).should().delete(TokenConstant.ACCESS_KEY + SESSION_ONE);
		then(cacheManager).should().delete(TokenConstant.ROTATED_KEY + SESSION_ONE);
		then(cacheManager).should().delete(TokenConstant.REFRESH_KEY + SESSION_TWO);
		then(cacheManager).should().delete(TokenConstant.ACCESS_KEY + SESSION_TWO);
		then(cacheManager).should().delete(TokenConstant.ROTATED_KEY + SESSION_TWO);
		thenSoftly(softly -> softly.then(Duration.between(nowCaptor.getValue(), Instant.now()))
				.isLessThan(Duration.ofSeconds(5)));
	}

	@Test
	void givenNoActiveSessions_thenDoesNotTouchTheCache(){
		willReturn(List.of()).given(gateway).expireActiveByUserId(eq(STAFF_ID), nowCaptor.capture());

		revoker.revokeAll(STAFF_ID);

		then(cacheManager).should(never()).delete(eq(TokenConstant.REFRESH_KEY + SESSION_ONE));
		then(cacheManager).shouldHaveNoInteractions();
	}

}
