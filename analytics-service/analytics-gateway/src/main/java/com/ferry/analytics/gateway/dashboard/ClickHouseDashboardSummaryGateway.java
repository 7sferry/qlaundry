package com.ferry.analytics.gateway.dashboard;

import com.ferry.analytics.core.dashboard.DashboardSummaryGateway;
import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class ClickHouseDashboardSummaryGateway implements DashboardSummaryGateway{
	private static final String SUMMARY_QUERY = """
			SELECT
			    toInt64(countIf(status != 'CANCELLED' AND day = {today:Date})) AS today_orders,
			    toDecimal64(sumIf(total_price, status != 'CANCELLED' AND day = {today:Date}), 2) AS today_revenue,
			    toInt64(countIf(status != 'CANCELLED' AND day >= {monthStart:Date})) AS month_orders,
			    toDecimal64(sumIf(total_price, status != 'CANCELLED' AND day >= {monthStart:Date}), 2) AS month_revenue,
			    toInt64(countIf(status != 'CANCELLED' AND day >= {lastMonthStart:Date} AND day < {monthStart:Date})) AS last_month_orders,
			    toDecimal64(sumIf(total_price, status != 'CANCELLED' AND day >= {lastMonthStart:Date} AND day < {monthStart:Date}), 2) AS last_month_revenue,
			    toInt64(countIf(status = 'PENDING')) AS pending_orders,
			    toInt64(countIf(status = 'IN_PROGRESS')) AS in_progress_orders,
			    toInt64(countIf(status = 'READY')) AS ready_orders
			FROM (
			    SELECT status, total_price, toDate(created_at, 'Asia/Jakarta') AS day
			    FROM orders_current FINAL
			    WHERE tenant_id = {tenantId:String} AND deleted = 0
			)
			""";

	private static final String STATUS_DISTRIBUTION_QUERY = """
			SELECT status, toInt64(count()) AS orders
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND toDate(created_at, 'Asia/Jakarta') >= {monthStart:Date}
			  AND toDate(created_at, 'Asia/Jakarta') < {nextMonthStart:Date}
			GROUP BY status
			ORDER BY status
			""";

	private final AnalyticStore store;

	@Override
	public DashboardSummaryProjection summarize(TenantId tenantId, LocalDate today, LocalDate monthStart,
	                                            LocalDate lastMonthStart){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"today", today.toString(),
				"monthStart", monthStart.toString(),
				"lastMonthStart", lastMonthStart.toString());
		List<DashboardSummaryProjection> rows = store.query(SUMMARY_QUERY, params,
				reader -> new DashboardSummaryProjection(reader.getLong("today_orders"),
						reader.getBigDecimal("today_revenue"), reader.getLong("month_orders"),
						reader.getBigDecimal("month_revenue"), reader.getLong("last_month_orders"),
						reader.getBigDecimal("last_month_revenue"), reader.getLong("pending_orders"),
						reader.getLong("in_progress_orders"), reader.getLong("ready_orders")));
		return rows.isEmpty()
				? new DashboardSummaryProjection(0L, BigDecimal.ZERO, 0L, BigDecimal.ZERO, 0L, BigDecimal.ZERO, 0L, 0L, 0L)
				: rows.getFirst();
	}

	@Override
	public List<StatusCountProjection> statusDistribution(TenantId tenantId, LocalDate monthStart,
	                                                      LocalDate nextMonthStart){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"monthStart", monthStart.toString(),
				"nextMonthStart", nextMonthStart.toString());
		return store.query(STATUS_DISTRIBUTION_QUERY, params,
				reader -> new StatusCountProjection(reader.getString("status"), reader.getLong("orders")));
	}

}
