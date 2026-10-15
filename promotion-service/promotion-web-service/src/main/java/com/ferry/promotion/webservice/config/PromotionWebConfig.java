package com.ferry.promotion.webservice.config;

import com.ferry.promotion.core.analytics.PromotionAnalyticsPublisher;
import com.ferry.promotion.core.analytics.backfill.PromotionAnalyticsBackfillGateway;
import com.ferry.promotion.core.analytics.backfill.PromotionAnalyticsBackfillRequest;
import com.ferry.promotion.core.analytics.backfill.PromotionAnalyticsBackfillResponse;
import com.ferry.promotion.core.analytics.backfill.PromotionAnalyticsBackfillUseCase;
import com.ferry.promotion.core.analytics.backfill.DefaultPromotionAnalyticsBackfillUseCase;
import com.ferry.promotion.core.analytics.sweep.PromotionAnalyticsOutboxSweepGateway;
import com.ferry.promotion.core.analytics.sweep.PromotionAnalyticsOutboxSweepUseCase;
import com.ferry.promotion.core.analytics.sweep.DefaultPromotionAnalyticsOutboxSweepUseCase;
import com.ferry.promotion.core.promotion.create.DefaultPromotionCreateUseCase;
import com.ferry.promotion.core.promotion.create.PromotionCreateGateway;
import com.ferry.promotion.core.promotion.create.PromotionCreateUseCase;
import com.ferry.promotion.core.promotion.toggle.DefaultPromotionToggleUseCase;
import com.ferry.promotion.core.promotion.toggle.PromotionToggleGateway;
import com.ferry.promotion.core.promotion.toggle.PromotionToggleUseCase;
import com.ferry.promotion.core.promotion.detail.DefaultPromotionDetailUseCase;
import com.ferry.promotion.core.promotion.detail.PromotionDetailGateway;
import com.ferry.promotion.core.promotion.detail.PromotionDetailUseCase;
import com.ferry.promotion.core.promotion.list.DefaultPromotionListUseCase;
import com.ferry.promotion.core.promotion.list.PromotionListGateway;
import com.ferry.promotion.core.promotion.list.PromotionListUseCase;
import com.ferry.promotion.core.promotion.preview.DefaultPromotionPreviewUseCase;
import com.ferry.promotion.core.promotion.preview.PromotionPreviewUseCase;
import com.ferry.promotion.core.promotion.redemption.DefaultPromotionRedemptionUseCase;
import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionGateway;
import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionUseCase;
import com.ferry.promotion.core.promotion.release.DefaultPromotionReleaseUseCase;
import com.ferry.promotion.core.promotion.release.PromotionReleaseGateway;
import com.ferry.promotion.core.promotion.release.PromotionReleaseUseCase;
import com.ferry.promotion.core.promotion.update.DefaultPromotionUpdateUseCase;
import com.ferry.promotion.core.promotion.update.PromotionUpdateGateway;
import com.ferry.promotion.core.promotion.update.PromotionUpdateUseCase;
import com.ferry.promotion.gateway.analytics.PromotionAnalyticsStreamWriter;
import com.ferry.promotion.gateway.analytics.DefaultPromotionAnalyticsStreamWriter;
import com.ferry.promotion.gateway.analytics.JpaPromotionAnalyticsBackfillGateway;
import com.ferry.promotion.gateway.analytics.JpaPromotionAnalyticsOutboxSweepGateway;
import com.ferry.promotion.gateway.analytics.RedisPromotionAnalyticsPublisher;
import com.ferry.promotion.gateway.analytics.repository.PromotionAnalyticsEventJpaRepository;
import com.ferry.promotion.gateway.promotion.JpaPromotionCreateGateway;
import com.ferry.promotion.gateway.promotion.JpaPromotionToggleGateway;
import com.ferry.promotion.gateway.promotion.JpaPromotionDetailGateway;
import com.ferry.promotion.gateway.promotion.JpaPromotionListGateway;
import com.ferry.promotion.gateway.promotion.JpaPromotionRedemptionGateway;
import com.ferry.promotion.gateway.promotion.JpaPromotionReleaseGateway;
import com.ferry.promotion.gateway.promotion.JpaPromotionUpdateGateway;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import com.ferry.promotion.gateway.promotion.repository.PromotionRedemptionJpaRepository;
import com.ferry.utils.cache.CacheHandler;
import com.ferry.utils.cache.DefaultCacheHandler;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import com.ferry.promotion.webservice.analytics.sweep.PromotionAnalyticsOutboxScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
@Configuration
@Lazy
@EnableConfigurationProperties(PromotionInternalKeysProperties.class)
public class PromotionWebConfig{

	@Bean
	IdGenerator idGenerator(){
		return new UlidGenerator();
	}

	@Bean
	CacheHandler cacheHandler(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper){
		return new DefaultCacheHandler(stringRedisTemplate, objectMapper);
	}

	@Bean
	PromotionCreateGateway promotionCreateGateway(PromotionJpaRepository promotionJpaRepository,
	                                              IdGenerator idGenerator){
		return new JpaPromotionCreateGateway(promotionJpaRepository, idGenerator);
	}

	@Bean
	PromotionCreateUseCase promotionCreateUseCase(PromotionCreateGateway promotionCreateGateway,
	                                              PromotionAnalyticsPublisher promotionAnalyticsPublisher){
		return new DefaultPromotionCreateUseCase(promotionCreateGateway, promotionAnalyticsPublisher);
	}

	@Bean
	PromotionUpdateGateway promotionUpdateGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionUpdateGateway(promotionJpaRepository);
	}

	@Bean
	PromotionUpdateUseCase promotionUpdateUseCase(PromotionUpdateGateway promotionUpdateGateway,
	                                              PromotionAnalyticsPublisher promotionAnalyticsPublisher){
		return new DefaultPromotionUpdateUseCase(promotionUpdateGateway, promotionAnalyticsPublisher);
	}

	@Bean
	PromotionToggleGateway promotionToggleGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionToggleGateway(promotionJpaRepository);
	}

	@Bean
	PromotionToggleUseCase promotionToggleUseCase(PromotionToggleGateway promotionToggleGateway,
	                                              PromotionAnalyticsPublisher promotionAnalyticsPublisher){
		return new DefaultPromotionToggleUseCase(promotionToggleGateway, promotionAnalyticsPublisher);
	}

	@Bean
	PromotionDetailGateway promotionDetailGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionDetailGateway(promotionJpaRepository);
	}

	@Bean
	PromotionDetailUseCase promotionDetailUseCase(PromotionDetailGateway promotionDetailGateway){
		return new DefaultPromotionDetailUseCase(promotionDetailGateway);
	}

	@Bean
	PromotionListGateway promotionListGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionListGateway(promotionJpaRepository);
	}

	@Bean
	PromotionListUseCase promotionListUseCase(PromotionListGateway promotionListGateway){
		return new DefaultPromotionListUseCase(promotionListGateway);
	}

	@Bean
	PromotionRedemptionGateway promotionRedemptionGateway(
			PromotionJpaRepository promotionJpaRepository,
			PromotionRedemptionJpaRepository promotionRedemptionJpaRepository,
			IdGenerator idGenerator){
		return new JpaPromotionRedemptionGateway(promotionJpaRepository, promotionRedemptionJpaRepository,
				idGenerator);
	}

	@Bean
	PromotionRedemptionUseCase promotionRedemptionUseCase(PromotionRedemptionGateway promotionRedemptionGateway){
		return new DefaultPromotionRedemptionUseCase(promotionRedemptionGateway);
	}

	@Bean
	PromotionReleaseGateway promotionReleaseGateway(PromotionJpaRepository promotionJpaRepository,
	                                                PromotionRedemptionJpaRepository promotionRedemptionJpaRepository){
		return new JpaPromotionReleaseGateway(promotionJpaRepository, promotionRedemptionJpaRepository);
	}

	@Bean
	PromotionReleaseUseCase promotionReleaseUseCase(PromotionReleaseGateway promotionReleaseGateway){
		return new DefaultPromotionReleaseUseCase(promotionReleaseGateway);
	}

	@Bean
	PromotionPreviewUseCase promotionPreviewUseCase(){
		return new DefaultPromotionPreviewUseCase();
	}

	@Bean
	JsonManager jsonManager(ObjectMapper objectMapper){
		return new DefaultJsonManager(objectMapper);
	}

	@Bean
	PromotionAnalyticsStreamWriter analyticsStreamWriter(StringRedisTemplate stringRedisTemplate,
	                                                     PromotionAnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                     PlatformTransactionManager transactionManager,
	                                                     @Value("${app.analytics.stream.event.key}") String streamKeyPrefix){
		return new DefaultPromotionAnalyticsStreamWriter(stringRedisTemplate, analyticsEventJpaRepository,
				transactionManager, streamKeyPrefix);
	}

	@Bean
	PromotionAnalyticsPublisher promotionAnalyticsPublisher(PromotionAnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                        PromotionAnalyticsStreamWriter analyticsStreamWriter,
	                                                        IdGenerator idGenerator,
	                                                        JsonManager jsonManager,
	                                                        PlatformTransactionManager transactionManager){
		return new RedisPromotionAnalyticsPublisher(analyticsEventJpaRepository, analyticsStreamWriter, idGenerator,
				jsonManager, transactionManager);
	}

	@Bean
	PromotionAnalyticsOutboxSweepGateway analyticsOutboxSweepGateway(PromotionAnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                                 PromotionAnalyticsStreamWriter analyticsStreamWriter,
	                                                                 PlatformTransactionManager transactionManager){
		return new JpaPromotionAnalyticsOutboxSweepGateway(analyticsEventJpaRepository, analyticsStreamWriter,
				transactionManager);
	}

	@Bean
	PromotionAnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase(PromotionAnalyticsOutboxSweepGateway analyticsOutboxSweepGateway,
	                                                                 @Value("${app.analytics.outbox.grace-period}") Duration gracePeriod,
	                                                                 @Value("${app.analytics.outbox.sweep-batch-size}") int sweepBatchSize){
		return new DefaultPromotionAnalyticsOutboxSweepUseCase(analyticsOutboxSweepGateway, gracePeriod, sweepBatchSize);
	}

	@Bean
	@Lazy(false)
	PromotionAnalyticsOutboxScheduler analyticsOutboxScheduler(PromotionAnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase){
		PromotionAnalyticsOutboxScheduler scheduler = new PromotionAnalyticsOutboxScheduler(analyticsOutboxSweepUseCase);
		Thread.startVirtualThread(scheduler::sweep);
		return scheduler;
	}

	@Bean
	PromotionAnalyticsBackfillGateway analyticsBackfillGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionAnalyticsBackfillGateway(promotionJpaRepository);
	}

	@Bean
	PromotionAnalyticsBackfillUseCase analyticsBackfillUseCase(PromotionAnalyticsBackfillGateway analyticsBackfillGateway,
	                                                           PromotionAnalyticsPublisher promotionAnalyticsPublisher,
	                                                           @Value("${app.analytics.outbox.backfill-batch-size}") int backfillBatchSize){
		return new DefaultPromotionAnalyticsBackfillUseCase(analyticsBackfillGateway, promotionAnalyticsPublisher,
				backfillBatchSize);
	}

//	@Bean
//	@Lazy(false)
//	@Profile("analytics-backfill")
	ApplicationRunner analyticsBackfillRunner(PromotionAnalyticsBackfillUseCase analyticsBackfillUseCase,
	                                          @Value("${app.analytics.backfill.tenant-id:}") String tenantId){
		return _ -> {
			PromotionAnalyticsBackfillResponse response = analyticsBackfillUseCase.execute(new PromotionAnalyticsBackfillRequest(tenantId));
			log.info("Analytics backfill done: {} promotion(s) published", response.promotions());
		};
	}

}
