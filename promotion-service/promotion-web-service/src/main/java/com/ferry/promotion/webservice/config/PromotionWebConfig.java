package com.ferry.promotion.webservice.config;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

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
	PromotionCreateUseCase promotionCreateUseCase(PromotionCreateGateway promotionCreateGateway){
		return new DefaultPromotionCreateUseCase(promotionCreateGateway);
	}

	@Bean
	PromotionUpdateGateway promotionUpdateGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionUpdateGateway(promotionJpaRepository);
	}

	@Bean
	PromotionUpdateUseCase promotionUpdateUseCase(PromotionUpdateGateway promotionUpdateGateway){
		return new DefaultPromotionUpdateUseCase(promotionUpdateGateway);
	}

	@Bean
	PromotionToggleGateway promotionToggleGateway(PromotionJpaRepository promotionJpaRepository){
		return new JpaPromotionToggleGateway(promotionJpaRepository);
	}

	@Bean
	PromotionToggleUseCase promotionToggleUseCase(PromotionToggleGateway promotionToggleGateway){
		return new DefaultPromotionToggleUseCase(promotionToggleGateway);
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

}
