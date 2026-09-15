package com.ferry.order.gateway.analytics;

import com.ferry.order.domain.analytics.AnalyticsEventDomain;
import com.ferry.order.domain.analytics.AnalyticsEventStatus;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventStatusJpaRepository;
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
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultAnalyticsStreamWriter implements AnalyticsStreamWriter{
	private static final String EVENT_ID_FIELD = "eventId";
	private static final String AGGREGATE_FIELD = "aggregate";
	private static final String TYPE_FIELD = "type";
	private static final String TENANT_ID_FIELD = "tenantId";
	private static final String AGGREGATE_ID_FIELD = "aggregateId";
	private static final String VERSION_FIELD = "version";
	private static final String OCCURRED_AT_FIELD = "occurredAt";
	private static final String PAYLOAD_FIELD = "payload";

	private final StringRedisTemplate stringRedisTemplate;
	private final AnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final AnalyticsEventStatusJpaRepository analyticsEventStatusJpaRepository;
	private final PlatformTransactionManager transactionManager;
	private final String streamKeyPrefix;

	@Override
	public String streamOf(AnalyticsEventDomain event){
		return streamKeyPrefix + event.aggregate().name();
	}

	@Override
	public void append(AnalyticsEventDomain event){
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
	public void markPublished(AnalyticsEventDomain event, String updatedBy){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.executeWithoutResult(_ -> analyticsEventJpaRepository.transition(event.id(),
				AnalyticsEventStatus.CREATED.getValue(),
				analyticsEventStatusJpaRepository.getReferenceById(AnalyticsEventStatus.PUBLISHED.getValue()),
				updatedBy, Instant.now()));
	}

}
