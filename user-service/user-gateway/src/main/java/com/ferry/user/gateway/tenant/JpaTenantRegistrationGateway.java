package com.ferry.user.gateway.tenant;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.core.staff.registration.StaffRegistrationUseCase;
import com.ferry.user.core.tenant.registration.TenantRegistrationGateway;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.token.UserAuthPrincipal;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.entity.TenantStatusJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.user.gateway.tenant.repository.TenantStatusJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaTenantRegistrationGateway implements TenantRegistrationGateway{
	private final IdGenerator idGenerator;
	private final TenantJpaRepository tenantRepository;
	private final TenantStatusJpaRepository tenantStatusRepository;
	private final StaffJpaRepository staffRepository;
	private final StaffRegistrationUseCase staffRegistrationUseCase;

	@Override
	public boolean existsByUsername(Username username){
		return staffRepository.existsByUsername(username.value());
	}

	@Override
	public Tenant save(Tenant tenant){
		String id = idGenerator.generateId();
		TenantStatusJpa status = tenantStatusRepository.getReferenceById(TenantStatus.PENDING.getValue());
		TenantJpa saved = tenantRepository.save(TenantJpa.construct(id, tenant, status));
		return TenantJpa.construct(saved);
	}

	@Override
	public StaffRegistrationResponse registerAdmin(StaffRegistrationRequest request, Tenant tenant){
		StaffRegistrationResponse[] result = new StaffRegistrationResponse[1];
		UserAuthPrincipal principal = new UserAuthPrincipal(tenant.id(), request.username(), request.fullName(),
				tenant.fullNameValue(), tenant.id(), tenant.timeZone(), SessionType.STAFF, StaffRole.SUPER_STAFF);
		staffRegistrationUseCase.execute(request, principal, response -> result[0] = response);
		return result[0];
	}

}
