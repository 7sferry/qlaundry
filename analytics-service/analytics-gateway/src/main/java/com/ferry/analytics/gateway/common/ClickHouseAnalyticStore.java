package com.ferry.analytics.gateway.common;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.query.QueryResponse;
import com.clickhouse.data.ClickHouseFormat;
import com.ferry.analytics.domain.common.exception.AnalyticsStoreException;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class ClickHouseAnalyticStore implements AnalyticStore{
	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
			.withZone(ZoneOffset.UTC);
	private static final String UTC_ZONE = "UTC";

	private final Client client;
	private final JsonManager jsonManager;
	private final Duration timeout;

	@Override
	public void insert(String table, List<Map<String, Object>> rows){
		if(rows.isEmpty()){
			return;
		}
		StringBuilder body = new StringBuilder();
		for(Map<String, Object> row : rows){
			body.append(jsonManager.writeValueAsString(row)).append('\n');
		}
		try(InputStream data = new ByteArrayInputStream(body.toString().getBytes(StandardCharsets.UTF_8));
		    InsertResponse _ = client.insert(table, data, ClickHouseFormat.JSONEachRow)
				    .get(timeout.toMillis(), TimeUnit.MILLISECONDS)){
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new AnalyticsStoreException("Interrupted while inserting into " + table, e);
		}catch(Exception e){
			throw new AnalyticsStoreException("Failed to insert " + rows.size() + " row(s) into " + table, e);
		}
	}

	@Override
	public <T> List<T> query(String sql, Map<String, Object> params, Function<ClickHouseBinaryFormatReader, T> mapper){
		try(QueryResponse response = client.query(sql, params).get(timeout.toMillis(), TimeUnit.MILLISECONDS)){
			ClickHouseBinaryFormatReader reader = client.newBinaryFormatReader(response);
			List<T> rows = new ArrayList<>();
			while(reader.hasNext()){
				reader.next();
				rows.add(mapper.apply(reader));
			}
			return rows;
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new AnalyticsStoreException("Interrupted while querying the analytics store", e);
		}catch(Exception e){
			throw new AnalyticsStoreException("Failed to query the analytics store", e);
		}
	}

	@Override
	public String dateTime(Instant instant){
		return instant == null ? null : DATE_TIME_FORMAT.format(instant);
	}

	@Override
	public String timeZone(ZoneId zone){
		return zone.normalized().equals(ZoneOffset.UTC) ? UTC_ZONE : zone.getId();
	}

	@Override
	public int flag(boolean value){
		return value ? 1 : 0;
	}

}
