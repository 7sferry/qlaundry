package com.ferry.analytics.gateway.common;

import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticStore{
	void insert(String table, List<Map<String, Object>> rows);

	<T> List<T> query(String sql, Map<String, Object> params, Function<ClickHouseBinaryFormatReader, T> mapper);

	String dateTime(Instant instant);

	String timeZone(ZoneId zone);

	int flag(boolean value);
}
