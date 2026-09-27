package com.ferry.analytics.webservice.event;

import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsListener{
	void onBatch(List<MapRecord<String, String, String>> records);
}
