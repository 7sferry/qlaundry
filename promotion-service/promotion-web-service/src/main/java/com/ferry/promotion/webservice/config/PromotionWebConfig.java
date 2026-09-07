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
import com.ferry.promotion.core.promotion.update.DefaultPromotionUpdateUseCase;
import com.ferry.promotion.core.promotion.update.PromotionUpdateGateway;
import com.ferry.promotion.core.promotion.update.PromotionUpdateUseCase;
import com.ferry.promotion.gateway.promotion.PromotionCreateJpaGateway;
import com.ferry.promotion.gateway.promotion.PromotionToggleJpaGateway;
import com.ferry.promotion.gateway.promotion.PromotionDetailJpaGateway;
import com.ferry.promotion.gateway.promotion.PromotionListJpaGateway;
import com.ferry.promotion.gateway.promotion.PromotionRedemptionJpaGateway;
import com.ferry.promotion.gateway.promotion.PromotionUpdateJpaGateway;
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
		return new PromotionCreateJpaGateway(promotionJpaRepository, idGenerator);
	}

	@Bean
	PromotionCreateUseCase promotionCreateUseCase(PromotionCreateGateway promotionCreateGateway){
		return new DefaultPromotionCreateUseCase(promotionCreateGateway);
	}

	@Bean
	PromotionUpdateGateway promotionUpdateGateway(PromotionJpaRepository promotionJpaRepository){
		return new PromotionUpdateJpaGateway(promotionJpaRepository);
	}

	@Bean
	PromotionUpdateUseCase promotionUpdateUseCase(PromotionUpdateGateway promotionUpdateGateway){
		return new DefaultPromotionUpdateUseCase(promotionUpdateGateway);
	}

	@Bean
	PromotionToggleGateway promotionToggleGateway(PromotionJpaRepository promotionJpaRepository){
		return new PromotionToggleJpaGateway(promotionJpaRepository);
	}

	@Bean
	PromotionToggleUseCase promotionToggleUseCase(PromotionToggleGateway promotionToggleGateway){
		return new DefaultPromotionToggleUseCase(promotionToggleGateway);
	}

	@Bean
	PromotionDetailGateway promotionDetailGateway(PromotionJpaRepository promotionJpaRepository){
		return new PromotionDetailJpaGateway(promotionJpaRepository);
	}

	@Bean
	PromotionDetailUseCase promotionDetailUseCase(PromotionDetailGateway promotionDetailGateway){
		return new DefaultPromotionDetailUseCase(promotionDetailGateway);
	}

	@Bean
	PromotionListGateway promotionListGateway(PromotionJpaRepository promotionJpaRepository){
		return new PromotionListJpaGateway(promotionJpaRepository);
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
		return new PromotionRedemptionJpaGateway(promotionJpaRepository, promotionRedemptionJpaRepository,
				idGenerator);
	}

	@Bean
	PromotionRedemptionUseCase promotionRedemptionUseCase(PromotionRedemptionGateway promotionRedemptionGateway){
		return new DefaultPromotionRedemptionUseCase(promotionRedemptionGateway);
	}

	@Bean
	PromotionPreviewUseCase promotionPreviewUseCase(){
		return new DefaultPromotionPreviewUseCase();
	}

}
