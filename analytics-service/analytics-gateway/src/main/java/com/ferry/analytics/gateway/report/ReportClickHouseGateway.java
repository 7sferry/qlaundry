package com.ferry.analytics.gateway.report;

import com.ferry.analytics.core.report.ReportGateway;
import com.ferry.analytics.domain.report.ReportBucket;
import com.ferry.analytics.domain.report.ReportWindow;
import com.ferry.analytics.domain.report.RevenueBucketProjection;
import com.ferry.analytics.domain.report.ServiceBreakdownProjection;
import com.ferry.analytics.domain.tenant.TenantIdDomain;
import com.ferry.analytics.gateway.clickhouse.ClickHouseStore;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class ReportClickHouseGateway implements ReportGateway{
	private static final String TREND_QUERY = """
			SELECT
			    toString(%s) AS bucket_start,
			    toInt64(countIf(status != 'CANCELLED')) AS orders,
			    toDecimal64(sumIf(total_price, status != 'CANCELLED'), 2) AS revenue
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND toDate(created_at, 'Asia/Jakarta') >= {from:Date}
			  AND toDate(created_at, 'Asia/Jakarta') < {to:Date}
			GROUP BY bucket_start
			ORDER BY bucket_start
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
			  AND toDate(created_at, 'Asia/Jakarta') >= {from:Date}
			  AND toDate(created_at, 'Asia/Jakarta') < {to:Date}
			GROUP BY service_id
			ORDER BY revenue DESC, service_id
			""";

	private final ClickHouseStore store;

	@Override
	public List<RevenueBucketProjection> revenueTrend(TenantIdDomain tenantId, ReportWindow window){
		String sql = TREND_QUERY.formatted(bucketExpression(window.bucket()));
		return store.query(sql, params(tenantId, window),
				reader -> new RevenueBucketProjection(LocalDate.parse(reader.getString("bucket_start")),
						reader.getBigDecimal("revenue"), reader.getLong("orders")));
	}

	@Override
	public List<ServiceBreakdownProjection> serviceBreakdown(TenantIdDomain tenantId, ReportWindow window){
		return store.query(BREAKDOWN_QUERY, params(tenantId, window),
				reader -> new ServiceBreakdownProjection(reader.getString("service_id"),
						reader.getString("snapshot_service_name"), reader.getLong("orders"), reader.getBigDecimal("revenue")));
	}

	private Map<String, Object> params(TenantIdDomain tenantId, ReportWindow window){
		return Map.of(
				"tenantId", tenantId.value(),
				"from", window.from().toString(),
				"to", window.toExclusive().toString());
	}

	private String bucketExpression(ReportBucket bucket){
		return switch(bucket){
			case DAY -> "toDate(created_at, 'Asia/Jakarta')";
			case WEEK -> "toMonday(created_at, 'Asia/Jakarta')";
			case MONTH -> "toStartOfMonth(created_at, 'Asia/Jakarta')";
		};
	}

}
