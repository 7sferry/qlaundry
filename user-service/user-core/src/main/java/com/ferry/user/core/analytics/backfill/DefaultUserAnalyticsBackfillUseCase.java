package com.ferry.user.core.analytics.backfill;

import com.ferry.user.core.analytics.UserAnalyticsEvent;
import com.ferry.user.core.analytics.UserAnalyticsOutboxConstant;
import com.ferry.user.core.analytics.CustomerAnalyticsMessage;
import com.ferry.user.core.analytics.StaffAnalyticsMessage;
import com.ferry.user.core.analytics.UserAnalyticsPublisher;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.staff.Staff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultUserAnalyticsBackfillUseCase implements UserAnalyticsBackfillUseCase{
	private final UserAnalyticsBackfillGateway gateway;
	private final UserAnalyticsPublisher publisher;
	private final int batchSize;

	@Override
	public UserAnalyticsBackfillResponse execute(UserAnalyticsBackfillRequest request){
		String tenantId = request.tenantIdOrNull();
		int staffs = 0;
		String afterStaffId = null;
		List<Staff> staffPage;
		do{
			staffPage = gateway.findStaffsAfter(tenantId, afterStaffId, batchSize);
			for(Staff staff : staffPage){
				publisher.publish(publisher.save(UserAnalyticsEvent.staff(UserAnalyticsEventType.STAFF_BACKFILLED,
						StaffAnalyticsMessage.from(staff), UserAnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterStaffId = staff.id();
				staffs++;
			}
		}while(staffPage.size() == batchSize);
		int customers = 0;
		String afterCustomerId = null;
		List<Customer> customerPage;
		do{
			customerPage = gateway.findCustomersAfter(tenantId, afterCustomerId, batchSize);
			for(Customer customer : customerPage){
				publisher.publish(publisher.save(UserAnalyticsEvent.customer(UserAnalyticsEventType.CUSTOMER_BACKFILLED,
						CustomerAnalyticsMessage.from(customer), UserAnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterCustomerId = customer.id();
				customers++;
			}
			if(!customerPage.isEmpty()){
				log.info("Analytics backfill published {} customer(s) so far", customers);
			}
		}while(customerPage.size() == batchSize);
		return new UserAnalyticsBackfillResponse(staffs, customers);
	}

}
