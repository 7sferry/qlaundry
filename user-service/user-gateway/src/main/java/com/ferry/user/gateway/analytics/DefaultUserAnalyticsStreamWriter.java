package com.ferry.user.gateway.analytics;

import com.ferry.user.domain.analytics.UserAnalyticsEvent;
import com.ferry.user.domain.analytics.UserAnalyticsEventStatus;
import com.ferry.user.gateway.analytics.repository.UserAnalyticsEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultUserAnalyticsStreamWriter implements UserAnalyticsStreamWriter{
	private static final String EVENT_ID_FIELD = "eventId";
	private static final String AGGREGATE_FIELD = "aggregate";
	private static final String TYPE_FIELD = "type";
	private static final String TENANT_ID_FIELD = "tenantId";
	private static final String AGGREGATE_ID_FIELD = "aggregateId";
	private static final String VERSION_FIELD = "version";
	private static final String OCCURRED_AT_FIELD = "occurredAt";
	private static final String PAYLOAD_FIELD = "payload";

	private final StringRedisTemplate stringRedisTemplate;
	private final UserAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final PlatformTransactionManager transactionManager;
	private final String streamKeyPrefix;

	@Override
	public String streamOf(UserAnalyticsEvent event){
		return streamKeyPrefix + event.aggregate().name();
	}

	@Override
	public void append(UserAnalyticsEvent event){
		Map<String, String> fields = new LinkedHashMap<>();
		fields.put(EVENT_ID_FIELD, event.id());
		fields.put(AGGREGATE_FIELD, event.aggregate().name());
		fields.put(TYPE_FIELD, event.type().name());
		fields.put(TENANT_ID_FIELD, event.tenantId());
		fields.put(AGGREGATE_ID_FIELD, event.aggregateId());
		fields.put(VERSION_FIELD, String.valueOf(event.aggregateVersion()));
		fields.put(OCCURRED_AT_FIELD, String.valueOf(event.occurredAt().toEpochMilli()));
		fields.put(PAYLOAD_FIELD, event.payload());
		stringRedisTemplate.opsForStream().add(StreamRecords.newRecord().in(streamOf(event)).ofStrings(fields));
	}

	@Override
	public void markPublished(UserAnalyticsEvent event, String updatedBy){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transactionTemplate.executeWithoutResult(_ -> analyticsEventJpaRepository.transition(event.id(),
				UserAnalyticsEventStatus.CREATED.getValue(), UserAnalyticsEventStatus.PUBLISHED.getValue(),
				updatedBy, Instant.now()));
	}

}
