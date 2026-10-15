package com.ferry.analytics.core.event.staff;

import com.ferry.analytics.core.event.staff.StaffEventRequest.StaffEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.StaffSnapshot;
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

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffEventUseCaseTest{

	private static final String TENANT_ID = "01TENANTMELATIWANGI000000";
	private static final String STAFF_ID = "01STAFFDEWIANGGRAINI00000";
	private static final String SECOND_STAFF_ID = "01STAFFJOKOSUSILO00000000";

	@Mock
	StaffEventGateway gateway;
	@InjectMocks
	DefaultStaffEventUseCase useCase;
	@Mock
	StaffEventPresenter presenter;
	@Captor
	ArgumentCaptor<List<StaffSnapshot>> staffsCaptor;
	@Captor
	ArgumentCaptor<List<ConsumedEvent>> consumedCaptor;
	@Captor
	ArgumentCaptor<StaffEventResponse> responseCaptor;

	@Test
	void givenNoEventList_thenThrowsConstraintViolationException(){
		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffEventRequest(null), presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
	}

	@Test
	void givenTwoStaffSnapshots_thenUpsertsBothAndRecordsThemAsConsumed(){
		Instant now = Instant.now();
		StaffSnapshot dewi = StaffSnapshot.builder()
				.tenantId(TENANT_ID)
				.staffId(STAFF_ID)
				.role("SUPER_STAFF")
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();
		StaffSnapshot deletedJoko = StaffSnapshot.builder()
				.tenantId(TENANT_ID)
				.staffId(SECOND_STAFF_ID)
				.role("STAFF")
				.deleted(true)
				.version(1)
				.createdAt(now.minusSeconds(86400L))
				.updatedAt(now)
				.build();

		useCase.execute(new StaffEventRequest(List.of(
				new StaffEvent("01EVENTDEWIDAFTAR00000000", "STAFF_REGISTERED", dewi),
				new StaffEvent("01EVENTJOKOHAPUS000000000", "STAFF_DELETED", deletedJoko))), presenter);

		then(gateway).should()
				.upsert(staffsCaptor.capture());
		then(gateway).should()
				.recordConsumed(consumedCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<ConsumedEvent> consumed = consumedCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(staffsCaptor.getValue()).containsExactly(dewi, deletedJoko);
			softly.then(consumed).extracting(ConsumedEvent::aggregate).containsOnly(AnalyticsAggregate.STAFF);
			softly.then(consumed).extracting(ConsumedEvent::aggregateId).containsExactly(STAFF_ID, SECOND_STAFF_ID);
			softly.then(consumed.getLast().version()).isEqualTo(1);
			softly.then(responseCaptor.getValue().applied()).hasSize(2);
			softly.then(responseCaptor.getValue().rejected()).isEmpty();
		});
	}

	@Test
	void givenOneEventWithoutType_thenRejectsOnlyThatOne(){
		Instant now = Instant.now();
		StaffSnapshot dewi = StaffSnapshot.builder()
				.tenantId(TENANT_ID)
				.staffId(STAFF_ID)
				.role("STAFF")
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();
		StaffSnapshot joko = StaffSnapshot.builder()
				.tenantId(TENANT_ID)
				.staffId(SECOND_STAFF_ID)
				.role("STAFF")
				.version(0)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new StaffEventRequest(List.of(
				new StaffEvent("01EVENTTANPATIPE000000000", " ", dewi),
				new StaffEvent("01EVENTJOKODAFTAR00000000", "STAFF_REGISTERED", joko))), presenter);

		then(gateway).should()
				.upsert(staffsCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(staffsCaptor.getValue()).containsExactly(joko);
			softly.then(responseCaptor.getValue().rejected()).hasSize(1);
			softly.then(responseCaptor.getValue().rejected().getFirst().eventId())
					.isEqualTo("01EVENTTANPATIPE000000000");
		});
	}

	@Test
	void givenEveryEventIsInvalid_thenNeverWrites(){
		Instant now = Instant.now();
		StaffSnapshot dewi = StaffSnapshot.builder()
				.tenantId(TENANT_ID)
				.staffId(STAFF_ID)
				.role("STAFF")
				.version(3)
				.createdAt(now)
				.updatedAt(now)
				.build();

		useCase.execute(new StaffEventRequest(List.of(new StaffEvent(null, "STAFF_DELETED", dewi))), presenter);

		then(gateway).should(never())
				.upsert(anyList());
		then(gateway).should(never())
				.recordConsumed(anyList());
		then(presenter).should()
				.present(responseCaptor.capture());

		thenSoftly(softly -> {
			softly.then(responseCaptor.getValue().applied()).isEmpty();
			softly.then(responseCaptor.getValue().rejected()).hasSize(1);
		});
	}

}
