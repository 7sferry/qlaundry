package com.ferry.user.gateway.tenant;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.core.staff.registration.StaffRegistrationUseCase;
import com.ferry.user.core.tenant.registration.TenantRegistrationGateway;
import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.token.UserPrincipal;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
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
	private final StaffRegistrationUseCase staffRegistrationUseCase;

	@Override
	public TenantDomain save(TenantDomain tenant){
		String id = idGenerator.generateId();
		TenantJpaEntity saved = tenantRepository.save(TenantJpaEntity.create(id, tenant));
		return new TenantDomain(saved.getId(), new FullNameDomain(saved.getFullName()),
				new DescriptionDomain(saved.getDescription()), saved.getVersion(), saved.isDeleted(),
				saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

	@Override
	public StaffRegistrationResponse registerAdmin(StaffRegistrationRequest request, String userId){
		StaffRegistrationResponse[] result = new StaffRegistrationResponse[1];
		staffRegistrationUseCase.execute(request, UserPrincipal.ofUserId(userId), response -> result[0] = response);
		return result[0];
	}

}
