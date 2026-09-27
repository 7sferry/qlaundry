package com.ferry.analytics.gateway.dashboard;

import com.ferry.analytics.core.dashboard.DashboardSummaryGateway;
import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.DashboardWindow;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class ClickHouseDashboardSummaryGateway implements DashboardSummaryGateway{
	private static final String PERIOD_QUERY = """
			SELECT
			    toInt64(countIf(created_at >= {dayFrom:DateTime64(3, 'UTC')} AND created_at < {dayTo:DateTime64(3, 'UTC')})) AS today_orders,
			    toDecimal64(sumIf(total_price,
			        created_at >= {dayFrom:DateTime64(3, 'UTC')} AND created_at < {dayTo:DateTime64(3, 'UTC')}), 2) AS today_revenue,
			    toInt64(countIf(created_at >= {monthFrom:DateTime64(3, 'UTC')})) AS month_orders,
			    toDecimal64(sumIf(total_price, created_at >= {monthFrom:DateTime64(3, 'UTC')}), 2) AS month_revenue,
			    toInt64(countIf(created_at < {monthFrom:DateTime64(3, 'UTC')})) AS last_month_orders,
			    toDecimal64(sumIf(total_price, created_at < {monthFrom:DateTime64(3, 'UTC')}), 2) AS last_month_revenue
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND status != 'CANCELLED'
			  AND created_at >= {lastMonthFrom:DateTime64(3, 'UTC')}
			  AND created_at < {monthTo:DateTime64(3, 'UTC')}
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private static final String OPEN_ORDERS_QUERY = """
			SELECT
			    toInt64(countIf(status = 'PENDING')) AS pending_orders,
			    toInt64(countIf(status = 'IN_PROGRESS')) AS in_progress_orders,
			    toInt64(countIf(status = 'READY')) AS ready_orders
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private static final String STATUS_DISTRIBUTION_QUERY = """
			SELECT status, toInt64(count()) AS orders
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND created_at >= {monthFrom:DateTime64(3, 'UTC')}
			  AND created_at < {monthTo:DateTime64(3, 'UTC')}
			GROUP BY status
			ORDER BY status
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public DashboardSummaryProjection summarize(TenantId tenantId, DashboardWindow window){
		Map<String, Object> periodParams = Map.of(
				"tenantId", tenantId.value(),
				"dayFrom", store.dateTime(window.dayFrom()),
				"dayTo", store.dateTime(window.dayTo()),
				"monthFrom", store.dateTime(window.monthFrom()),
				"monthTo", store.dateTime(window.monthTo()),
				"lastMonthFrom", store.dateTime(window.lastMonthFrom()));
		PeriodTotals period = store.query(PERIOD_QUERY, periodParams,
						reader -> new PeriodTotals(reader.getLong("today_orders"), reader.getBigDecimal("today_revenue"),
								reader.getLong("month_orders"), reader.getBigDecimal("month_revenue"),
								reader.getLong("last_month_orders"), reader.getBigDecimal("last_month_revenue")))
				.stream()
				.findFirst()
				.orElse(new PeriodTotals(0L, BigDecimal.ZERO, 0L, BigDecimal.ZERO, 0L, BigDecimal.ZERO));
		OpenOrders open = store.query(OPEN_ORDERS_QUERY, Map.of("tenantId", tenantId.value()),
						reader -> new OpenOrders(reader.getLong("pending_orders"), reader.getLong("in_progress_orders"),
								reader.getLong("ready_orders")))
				.stream()
				.findFirst()
				.orElse(new OpenOrders(0L, 0L, 0L));
		return new DashboardSummaryProjection(period.todayOrders(), period.todayRevenue(), period.monthOrders(),
				period.monthRevenue(), period.lastMonthOrders(), period.lastMonthRevenue(), open.pending(),
				open.inProgress(), open.ready());
	}

	@Override
	public List<StatusCountProjection> statusDistribution(TenantId tenantId, DashboardWindow window){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"monthFrom", store.dateTime(window.monthFrom()),
				"monthTo", store.dateTime(window.monthTo()));
		return store.query(STATUS_DISTRIBUTION_QUERY, params,
				reader -> new StatusCountProjection(reader.getString("status"), reader.getLong("orders")));
	}

	private record PeriodTotals(
		long todayOrders,
		BigDecimal todayRevenue,
		long monthOrders,
		BigDecimal monthRevenue,
		long lastMonthOrders,
		BigDecimal lastMonthRevenue){
	}

	private record OpenOrders(
		long pending,
		long inProgress,
		long ready){
	}

}
