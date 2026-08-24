package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.expiration.TenantExpirationGateway;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantIdProjection;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class TenantExpirationJpaGateway implements TenantExpirationGateway{
	private final TenantJpaRepository tenantJpaRepository;
	private final StaffJpaRepository staffJpaRepository;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<TenantIdDomain> findPendingOlderThan(Instant cutoff){
		return tenantJpaRepository.findAllByStatusIdAndDeletedIsFalseAndCreatedAtBefore(TenantStatus.PENDING.getValue(), cutoff, TenantIdProjection.class)
				.stream()
				.map(entity -> new TenantIdDomain(entity.id()))
				.toList();
	}

	@Override
	public void expire(TenantIdDomain tenantId){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.executeWithoutResult(_ -> {
			tenantJpaRepository.markDeleted(tenantId.value());
			staffJpaRepository.clearUsernamesByTenantId(tenantId.value());
		});
	}

}
