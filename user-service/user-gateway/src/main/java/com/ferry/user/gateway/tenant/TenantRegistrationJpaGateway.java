package com.ferry.user.gateway.tenant;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.core.staff.registration.StaffRegistrationUseCase;
import com.ferry.user.core.tenant.registration.TenantRegistrationGateway;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.token.UserPrincipal;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.entity.TenantStatusJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.user.gateway.tenant.repository.TenantStatusJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class TenantRegistrationJpaGateway implements TenantRegistrationGateway{
	private final IdGenerator idGenerator;
	private final TenantJpaRepository tenantRepository;
	private final TenantStatusJpaRepository tenantStatusRepository;
	private final StaffJpaRepository staffRepository;
	private final StaffRegistrationUseCase staffRegistrationUseCase;

	@Override
	public boolean existsByUsername(UsernameDomain username){
		return staffRepository.existsByUsername(username.value());
	}

	@Override
	public TenantDomain save(TenantDomain tenant){
		String id = idGenerator.generateId();
		TenantStatusJpaEntity status = tenantStatusRepository.getReferenceById(TenantStatus.PENDING.getValue());
		TenantJpaEntity saved = tenantRepository.save(TenantJpaEntity.construct(id, tenant, status));
		return TenantJpaEntity.construct(saved);
	}

	@Override
	public StaffRegistrationResponse registerAdmin(StaffRegistrationRequest request, TenantDomain tenant){
		StaffRegistrationResponse[] result = new StaffRegistrationResponse[1];
		UserPrincipal principal = new UserPrincipal(tenant.id(), request.username(), request.fullName(),
				tenant.fullNameValue(), tenant.id(), SessionType.STAFF, StaffRole.SUPER_STAFF);
		staffRegistrationUseCase.execute(request, principal, response -> result[0] = response);
		return result[0];
	}

}
