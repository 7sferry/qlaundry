package com.ferry.order.gateway.order;

import com.ferry.order.core.order.create.OrderPromotionSagaGateway;
import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.order.OrderPromotionSagaStatus;
import com.ferry.order.gateway.order.entity.OrderPromotionSagaJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPromotionSagaStatusJpaEntity;
import com.ferry.order.gateway.order.repository.OrderPromotionSagaJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionSagaStatusJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class OrderPromotionSagaJpaGateway implements OrderPromotionSagaGateway{
	private final OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository;
	private final OrderPromotionSagaStatusJpaRepository orderPromotionSagaStatusJpaRepository;
	private final IdGenerator idGenerator;
	private final PlatformTransactionManager transactionManager;

	@Override
	public void open(OrderPromotionSagaDomain saga){
		String id = idGenerator.generateId();
		newTransaction().executeWithoutResult(_ -> {
			OrderPromotionSagaStatusJpaEntity status = orderPromotionSagaStatusJpaRepository.getReferenceById(
					saga.status().getValue());
			orderPromotionSagaJpaRepository.saveAndFlush(OrderPromotionSagaJpaEntity.construct(id, saga, status));
		});
	}

	@Override
	public void markCommittedAfterCommit(OrderPromotionSagaDomain saga){
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
	public void markReleased(OrderPromotionSagaDomain saga){
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
		return transactionTemplate;
	}

}
