package com.ferry.analytics.gateway.staff;

import com.ferry.analytics.core.staff.StaffSummaryGateway;
import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.staff.StaffSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ClickHouseStaffSummaryGateway implements StaffSummaryGateway{
	private static final String SUMMARY_QUERY = """
			SELECT
			    toInt64(count()) AS total_staff,
			    toInt64(countIf(created_at >= {from:DateTime64(3, 'UTC')} AND created_at < {to:DateTime64(3, 'UTC')})) AS new_staff
			FROM staffs_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public StaffSummaryProjection summarize(TenantId tenantId, PeriodWindow window){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"from", store.dateTime(window.from()),
				"to", store.dateTime(window.to()));
		return store.query(SUMMARY_QUERY, params,
						reader -> new StaffSummaryProjection(reader.getLong("total_staff"), reader.getLong("new_staff")))
				.stream()
				.findFirst()
				.orElse(new StaffSummaryProjection(0L, 0L));
	}

}
