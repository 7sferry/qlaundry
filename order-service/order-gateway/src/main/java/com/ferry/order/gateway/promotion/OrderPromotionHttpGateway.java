package com.ferry.order.gateway.promotion;

import com.ferry.order.core.order.create.OrderPromotionGateway;
import com.ferry.order.core.order.create.PromotionRedemptionHttpRequest;
import com.ferry.order.core.order.create.PromotionRedemptionHttpResponse;
import com.ferry.order.core.order.create.PromotionReleaseHttpRequest;
import com.ferry.order.domain.common.exception.PromotionUnavailableException;
import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.order.OrderPromotionSagaStatus;
import com.ferry.order.gateway.order.entity.OrderPromotionSagaJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPromotionSagaStatusJpaEntity;
import com.ferry.order.gateway.order.repository.OrderPromotionSagaJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionSagaStatusJpaRepository;
import com.ferry.promotion.client.PromotionRedemptionBatchResult;
import com.ferry.promotion.client.PromotionRedemptionParams;
import com.ferry.promotion.client.PromotionRedemptionResult;
import com.ferry.promotion.client.PromotionReleaseParams;
import com.ferry.promotion.client.PromotionServiceClient;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.httpclient.HttpClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class OrderPromotionHttpGateway implements OrderPromotionGateway{

	private final PromotionServiceClient promotionServiceClient;
	private final OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository;
	private final OrderPromotionSagaStatusJpaRepository orderPromotionSagaStatusJpaRepository;
	private final IdGenerator idGenerator;
	private final PlatformTransactionManager transactionManager;

	@Override
	public void openSaga(OrderPromotionSagaDomain saga){
		String id = idGenerator.generateId();
		newTransaction().executeWithoutResult(_ -> {
			OrderPromotionSagaStatusJpaEntity status = orderPromotionSagaStatusJpaRepository.getReferenceById(
					saga.status().getValue());
			orderPromotionSagaJpaRepository.saveAndFlush(OrderPromotionSagaJpaEntity.construct(id, saga, status));
		});
	}

	@Override
	public void markSagaCommittedAfterCommit(OrderPromotionSagaDomain saga){
		if(!TransactionSynchronizationManager.isSynchronizationActive()){
			transitionFromPending(saga);
			return;
		}
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
			@Override
			public void afterCommit(){
				try{
					transitionFromPending(saga);
				}catch(RuntimeException e){
					log.warn("Order {} committed but its promotion saga could not be marked {}; the saga sweeper will",
							saga.referenceId(), saga.status(), e);
				}
			}
		});
	}

	@Override
	public void markSagaReleased(OrderPromotionSagaDomain saga){
		transitionFromPending(saga);
	}

	private void transitionFromPending(OrderPromotionSagaDomain saga){
		newTransaction().executeWithoutResult(_ -> orderPromotionSagaJpaRepository.transition(saga.tenantId(),
				saga.referenceId(), OrderPromotionSagaStatus.PENDING.getValue(),
				orderPromotionSagaStatusJpaRepository.getReferenceById(saga.status().getValue()), saga.updatedBy(),
				saga.updatedAt()));
	}

	private TransactionTemplate newTransaction(){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		return transactionTemplate;
	}

	@Override
	public List<PromotionRedemptionHttpResponse> redeem(PromotionRedemptionHttpRequest request){
		PromotionRedemptionParams params = new PromotionRedemptionParams(request.tenantId(), request.codes(),
				request.subtotal(), request.referenceId(), request.customerId(), request.redeemedBy());
		try{
			PromotionRedemptionBatchResult result = promotionServiceClient.redeem(params);
			return result.redemptions().stream()
					.map(this::toHttpResponse)
					.toList();
		}catch(HttpClientException e){
			throw new PromotionUnavailableException("Promotion service is unavailable. Please try again.", e);
		}
	}

	@Override
	public void release(PromotionReleaseHttpRequest request){
		PromotionReleaseParams params = new PromotionReleaseParams(request.tenantId(), request.referenceId(),
				request.releasedBy());
		try{
			promotionServiceClient.release(params);
		}catch(HttpClientException e){
			throw new PromotionUnavailableException("Promotion service is unavailable. Please try again.", e);
		}
	}

	private PromotionRedemptionHttpResponse toHttpResponse(PromotionRedemptionResult result){
		return new PromotionRedemptionHttpResponse(result.applied(), result.message(), result.promotionId(),
				result.code(), result.discountAmount());
	}

}
