package com.ferry.user.core.analytics.backfill;

import com.ferry.user.core.analytics.UserAnalyticsEventConfig;
import com.ferry.user.core.analytics.UserAnalyticsOutboxConstant;
import com.ferry.user.core.analytics.UserAnalyticsPublisher;
import com.ferry.user.domain.analytics.UserAnalyticsAggregate;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultUserAnalyticsBackfillUseCaseTest{

	private static final String TENANT_ID = "01TENANTSRIREJEKI00000000";
	private static final String ADMIN_ID = "01STAFFTONYHARTONO000000";

	@Mock
	UserAnalyticsBackfillGateway gateway;
	@Mock
	UserAnalyticsPublisher publisher;
	@Captor
	ArgumentCaptor<UserAnalyticsEventConfig> configCaptor;

	@Test
	void givenStaffAndCustomers_thenPublishesStaffFirstThenCustomersAsBackfilled(){
		Instant now = Instant.now();
		Staff tony = Staff.builder()
				.id(ADMIN_ID)
				.username(new Username("tonyhartono"))
				.fullName(new FullName("tony hartono"))
				.description(new Description("owner"))
				.tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF)
				.version(2)
				.createdAt(now)
				.createdBy(TENANT_ID)
				.updatedAt(now)
				.updatedBy(TENANT_ID)
				.build();
		Customer dian = Customer.builder()
				.id("01CUSTOMERDIANSASTRO00000")
				.tenantId(TENANT_ID)
				.fullName(new FullName("dian sastro"))
				.version(0)
				.createdAt(now)
				.createdBy(ADMIN_ID)
				.updatedAt(now)
				.updatedBy(ADMIN_ID)
				.build();
		Customer deletedReza = Customer.builder()
				.id("01CUSTOMERREZARAHADIAN000")
				.tenantId(TENANT_ID)
				.fullName(new FullName("reza rahadian"))
				.deleted(true)
				.version(1)
				.createdAt(now)
				.createdBy(ADMIN_ID)
				.updatedAt(now)
				.updatedBy(ADMIN_ID)
				.build();
		willReturn(List.of(tony)).given(gateway)
				.findStaffsAfter(isNull(), isNull(), anyInt());
		willReturn(List.of(dian, deletedReza)).given(gateway)
				.findCustomersAfter(isNull(), isNull(), anyInt());
		DefaultUserAnalyticsBackfillUseCase useCase = new DefaultUserAnalyticsBackfillUseCase(gateway, publisher, 500);

		UserAnalyticsBackfillResponse response = useCase.execute(new UserAnalyticsBackfillRequest(null));

		then(publisher).should(times(3))
				.save(configCaptor.capture());

		List<UserAnalyticsEventConfig> configs = configCaptor.getAllValues();

		thenSoftly(softly -> {
			softly.then(response.staffs()).isEqualTo(1);
			softly.then(response.customers()).isEqualTo(2);
			softly.then(configs).extracting(UserAnalyticsEventConfig::aggregate)
					.containsExactly(UserAnalyticsAggregate.STAFF, UserAnalyticsAggregate.CUSTOMER, UserAnalyticsAggregate.CUSTOMER);
			softly.then(configs).extracting(UserAnalyticsEventConfig::type)
					.containsExactly(UserAnalyticsEventType.STAFF_BACKFILLED, UserAnalyticsEventType.CUSTOMER_BACKFILLED,
							UserAnalyticsEventType.CUSTOMER_BACKFILLED);
			softly.then(configs).extracting(UserAnalyticsEventConfig::aggregateVersion).containsExactly(2, 0, 1);
			softly.then(configs).extracting(UserAnalyticsEventConfig::actor)
					.containsOnly(UserAnalyticsOutboxConstant.BACKFILL_ACTOR);
		});
	}

	@Test
	void givenAFullCustomerPage_thenAsksForTheNextPageAfterItsLastId(){
		Instant now = Instant.now();
		Customer first = Customer.builder()
				.id("01CUSTOMERAAAAAAAAAAAAAAA")
				.tenantId(TENANT_ID)
				.fullName(new FullName("ayu ting ting"))
				.version(0)
				.createdAt(now)
				.createdBy(ADMIN_ID)
				.updatedAt(now)
				.updatedBy(ADMIN_ID)
				.build();
		willReturn(List.of()).given(gateway)
				.findStaffsAfter(anyString(), isNull(), anyInt());
		willReturn(List.of(first)).given(gateway)
				.findCustomersAfter(anyString(), isNull(), anyInt());
		willReturn(List.of()).given(gateway)
				.findCustomersAfter(anyString(), anyString(), anyInt());
		DefaultUserAnalyticsBackfillUseCase useCase = new DefaultUserAnalyticsBackfillUseCase(gateway, publisher, 1);

		UserAnalyticsBackfillResponse response = useCase.execute(new UserAnalyticsBackfillRequest(TENANT_ID));

		then(gateway).should()
				.findCustomersAfter(eq(TENANT_ID), eq("01CUSTOMERAAAAAAAAAAAAAAA"), eq(1));

		thenSoftly(softly -> {
			softly.then(response.staffs()).isZero();
			softly.then(response.customers()).isEqualTo(1);
		});
	}

}
