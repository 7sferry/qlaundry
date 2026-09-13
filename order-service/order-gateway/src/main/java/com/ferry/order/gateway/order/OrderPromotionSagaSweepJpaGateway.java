package com.ferry.order.gateway.order;

import com.ferry.order.core.order.saga.OrderPromotionSagaSweepGateway;
import com.ferry.order.domain.order.OrderNumberDomain;
import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.order.OrderPromotionSagaStatus;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.entity.OrderPromotionSagaJpaEntity;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionSagaJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionSagaStatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class OrderPromotionSagaSweepJpaGateway implements OrderPromotionSagaSweepGateway{
	private final OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository;
	private final OrderPromotionSagaStatusJpaRepository orderPromotionSagaStatusJpaRepository;
	private final OrderJpaRepository orderJpaRepository;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<OrderPromotionSagaDomain> findPendingUntouchedSince(Instant cutoff, int limit){
		return orderPromotionSagaJpaRepository.findStale(OrderPromotionSagaStatus.PENDING.getValue(), cutoff,
						PageRequest.ofSize(limit))
				.stream()
				.map(OrderPromotionSagaJpaEntity::construct)
				.toList();
	}

	@Override
	public boolean orderExists(OrderNumberDomain orderNumber, TenantIdDomain tenantId){
		return orderJpaRepository.existsByOrderNumberAndTenantId(orderNumber.value(), tenantId.value());
	}

	@Override
	public void markCommitted(OrderPromotionSagaDomain saga){
		transitionFromPending(saga);
	}

	@Override
	public void markReleased(OrderPromotionSagaDomain saga){
		transitionFromPending(saga);
	}

	@Override
	public void recordFailure(OrderPromotionSagaDomain saga){
		new TransactionTemplate(transactionManager).executeWithoutResult(_ ->
				orderPromotionSagaJpaRepository.recordFailure(saga.tenantId(), saga.referenceId(),
						OrderPromotionSagaStatus.PENDING.getValue(), saga.lastError(), saga.updatedBy(),
						saga.updatedAt()));
	}

	private void transitionFromPending(OrderPromotionSagaDomain saga){
		new TransactionTemplate(transactionManager).executeWithoutResult(_ ->
				orderPromotionSagaJpaRepository.transition(saga.tenantId(), saga.referenceId(),
						OrderPromotionSagaStatus.PENDING.getValue(),
						orderPromotionSagaStatusJpaRepository.getReferenceById(saga.status().getValue()),
						saga.updatedBy(), saga.updatedAt()));
	}

}
