package com.ferry.analytics.gateway.report;

import com.ferry.analytics.core.report.ReportGateway;
import com.ferry.analytics.domain.report.ReportBucket;
import com.ferry.analytics.domain.report.ReportWindow;
import com.ferry.analytics.domain.report.RevenueBucketProjection;
import com.ferry.analytics.domain.report.ServiceBreakdownProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class ClickHouseReportGateway implements ReportGateway{
	private static final String TREND_QUERY = """
			SELECT
			    toString(%s) AS bucket_start,
			    toInt64(count()) AS orders,
			    toDecimal64(sum(total_price), 2) AS revenue
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND status != 'CANCELLED'
			  AND created_at >= {from:DateTime64(3, 'UTC')}
			  AND created_at < {to:DateTime64(3, 'UTC')}
			GROUP BY bucket_start
			ORDER BY bucket_start
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private static final String BREAKDOWN_QUERY = """
			SELECT
			    service_id,
			    argMax(service_name, updated_at) AS snapshot_service_name,
			    toInt64(count()) AS orders,
			    toDecimal64(sum(total_price), 2) AS revenue
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND status != 'CANCELLED'
			  AND created_at >= {from:DateTime64(3, 'UTC')}
			  AND created_at < {to:DateTime64(3, 'UTC')}
			GROUP BY service_id
			ORDER BY revenue DESC, service_id
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public List<RevenueBucketProjection> revenueTrend(TenantId tenantId, ReportWindow window){
		String sql = TREND_QUERY.formatted(bucketExpression(window.bucket()));
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"from", store.dateTime(window.fromInstant()),
				"to", store.dateTime(window.toExclusiveInstant()),
				"zone", store.timeZone(window.zone()));
		return store.query(sql, params,
				reader -> new RevenueBucketProjection(LocalDate.parse(reader.getString("bucket_start")),
						reader.getBigDecimal("revenue"), reader.getLong("orders")));
	}

	@Override
	public List<ServiceBreakdownProjection> serviceBreakdown(TenantId tenantId, ReportWindow window){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"from", store.dateTime(window.fromInstant()),
				"to", store.dateTime(window.toExclusiveInstant()));
		return store.query(BREAKDOWN_QUERY, params,
				reader -> new ServiceBreakdownProjection(reader.getString("service_id"),
						reader.getString("snapshot_service_name"), reader.getLong("orders"), reader.getBigDecimal("revenue")));
	}

	private String bucketExpression(ReportBucket bucket){
		return switch(bucket){
			case DAY -> "toDate(created_at, {zone:String})";
			case WEEK -> "toMonday(created_at, {zone:String})";
			case MONTH -> "toStartOfMonth(created_at, {zone:String})";
		};
	}

}
