package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.promotion.*;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import com.ferry.utils.pagination.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPromotionListUseCaseTest{

	private static final String TENANT_ID = "01TENANTMAWARPUTIH000000";
	private static final String STAFF_ID = "01STAFFDIMASPRABOWO00000";
	private static final String PROMOTION_ID_1 = "01PROMOKEMERDEKAAN000000";
	private static final String PROMOTION_ID_2 = "01PROMOTAHUNBARU00000000";

	@Mock
	PromotionListGateway gateway;
	@InjectMocks
	DefaultPromotionListUseCase useCase;
	@Mock
	PromotionListPresenter presenter;
	@Captor
	ArgumentCaptor<PromotionFilter> filterCaptor;
	@Captor
	ArgumentCaptor<PromotionListResponse> responseCaptor;

	@Test
	void givenNoFiltersAndNoCursor_thenBuildsFilterWithDefaultsAndActiveOnlyTrue(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionListRequest request = new PromotionListRequest(null, null, null, null, null, null, null, null, null);
		willReturn(new CursorFetch<PromotionDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(PromotionFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		PromotionFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.tenantId()).isEqualTo(TENANT_ID);
			softly.then(filter.code()).isNull();
			softly.then(filter.name()).isNull();
			softly.then(filter.type()).isNull();
			softly.then(filter.activeOnly()).isTrue();
			softly.then(filter.currentOnly()).isFalse();
			softly.then(filter.sortBy()).isEqualTo(PromotionListSortBy.ID);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.DESC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.NEXT);
			softly.then(filter.cursor()).isNull();
		});
	}

	@Test
	void givenCodeNameTypeAndActiveOnlyFalse_thenPassesThemThroughToTheGateway(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionListRequest request = new PromotionListRequest("merdeka", "diskon", PromotionType.FIXED_AMOUNT,
				false, null, null, null, null, null);
		willReturn(new CursorFetch<PromotionDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(PromotionFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		PromotionFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.code()).isEqualTo("merdeka");
			softly.then(filter.codeStartsWith()).isEqualTo("MERDEKA%");
			softly.then(filter.name()).isEqualTo("diskon");
			softly.then(filter.nameStartsWith()).isEqualTo("diskon%");
			softly.then(filter.type()).isEqualTo(PromotionType.FIXED_AMOUNT);
			softly.then(filter.typeValue()).isEqualTo((short) 2);
			softly.then(filter.activeOnly()).isFalse();
		});
	}

	@Test
	void givenCurrentOnlyTrue_thenPassesItThroughToTheGateway(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionListRequest request = new PromotionListRequest(null, null, null, null, true, null, null, null, null);
		willReturn(new CursorFetch<PromotionDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(PromotionFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		thenSoftly(softly -> softly.then(filterCaptor.getValue().currentOnly()).isTrue());
	}

	@Test
	void givenExplicitCursorSortAndDirection_thenDecodesCursorAndAppliesRequestedSortAndDirection(){
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		String cursorToken = CursorCodec.encode("Diskon Kemerdekaan", PROMOTION_ID_1);
		PromotionListRequest request = new PromotionListRequest(null, null, null, null, null, cursorToken,
				PageDirection.PREV, PromotionListSortBy.NAME, SortDirection.ASC);
		willReturn(new CursorFetch<PromotionDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(PromotionFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		PromotionFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.cursor()).isEqualTo(new PageCursor("Diskon Kemerdekaan", PROMOTION_ID_1));
			softly.then(filter.sortBy()).isEqualTo(PromotionListSortBy.NAME);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.ASC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.PREV);
		});
	}

	@Test
	void givenPromotionsReturnedWithMoreRowsAndCursorProvided_thenPresentsPageWithNextAndPrevCursors(){
		Instant now = Instant.now();
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionDomain promotion1 = PromotionDomain.builder()
				.id(PROMOTION_ID_1)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("MERDEKA17"))
				.name("Diskon Kemerdekaan")
				.description(new NoteDomain("august only"))
				.type(PromotionType.PERCENTAGE)
				.percentage(new BigDecimal("17"))
				.usageLimit(170)
				.usedCount(45)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionDomain promotion2 = PromotionDomain.builder()
				.id(PROMOTION_ID_2)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("TAHUNBARU"))
				.name("Diskon Tahun Baru")
				.description(new NoteDomain("new year"))
				.type(PromotionType.FIXED_AMOUNT)
				.amount(MoneyDomain.of(10000L))
				.usedCount(2)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		String cursorToken = CursorCodec.encode(PROMOTION_ID_1, PROMOTION_ID_1);
		PromotionListRequest request = new PromotionListRequest(null, null, null, null, null, cursorToken,
				PageDirection.NEXT, PromotionListSortBy.ID, SortDirection.DESC);
		willReturn(new CursorFetch<>(List.of(promotion1, promotion2), true)).given(gateway)
				.findByFilter(any(PromotionFilter.class));

		useCase.execute(request, principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		PromotionListResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.promotions()).containsExactly(promotion1, promotion2);
			softly.then(response.nextCursor()).isEqualTo(CursorCodec.encode(PROMOTION_ID_2, PROMOTION_ID_2));
			softly.then(response.prevCursor()).isEqualTo(CursorCodec.encode(PROMOTION_ID_1, PROMOTION_ID_1));
			softly.then(response.promotions().getFirst().remainingUsage()).isEqualTo(125);
			softly.then(response.promotions().getLast().remainingUsage()).isNull();
		});
	}

	@Test
	void givenSortByCodeWithMoreRowsAndCursorProvided_thenEncodesTheCodeIntoTheNextCursor(){
		Instant now = Instant.now();
		PromotionAuthPrincipal principal = PromotionAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		PromotionDomain promotion1 = PromotionDomain.builder()
				.id(PROMOTION_ID_1)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("GAJIAN30"))
				.name("Diskon Gajian")
				.description(new NoteDomain("payday sale"))
				.type(PromotionType.PERCENTAGE)
				.percentage(new BigDecimal("30"))
				.usageLimit(200)
				.usedCount(11)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		PromotionDomain promotion2 = PromotionDomain.builder()
				.id(PROMOTION_ID_2)
				.tenantId(TENANT_ID)
				.code(new PromotionCodeDomain("LEBARAN25"))
				.name("Diskon Lebaran")
				.description(new NoteDomain("seasonal"))
				.type(PromotionType.PERCENTAGE)
				.percentage(new BigDecimal("25"))
				.usedCount(3)
				.startAt(now.minusSeconds(864000L))
				.endAt(now.plusSeconds(864000L))
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		String cursorToken = CursorCodec.encode("GAJIAN30", PROMOTION_ID_1);
		PromotionListRequest request = new PromotionListRequest(null, null, null, null, null, cursorToken,
				PageDirection.NEXT, PromotionListSortBy.CODE, SortDirection.ASC);
		willReturn(new CursorFetch<>(List.of(promotion1, promotion2), true)).given(gateway)
				.findByFilter(any(PromotionFilter.class));

		useCase.execute(request, principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		PromotionListResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.promotions()).containsExactly(promotion1, promotion2);
			softly.then(response.nextCursor()).isEqualTo(CursorCodec.encode("LEBARAN25", PROMOTION_ID_2));
			softly.then(response.prevCursor()).isEqualTo(CursorCodec.encode("GAJIAN30", PROMOTION_ID_1));
		});
	}

}
