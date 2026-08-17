package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.registration.StaffRegistrationGateway;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.gateway.staff.entity.*;
import com.ferry.user.gateway.staff.repository.*;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffRegistrationJpaGateway implements StaffRegistrationGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffPasswordJpaRepository staffPasswordJpaRepository;
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final StaffAddressJpaRepository staffAddressJpaRepository;
	private final StaffPhoneJpaRepository staffPhoneJpaRepository;
	private final StaffRoleJpaRepository staffRoleJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public StaffDomain save(StaffDomain register){
		String id = idGenerator.generateId();
		TenantJpaEntity tenant = tenantJpaRepository.getReferenceById(register.tenantId());
		StaffRoleJpaEntity role = staffRoleJpaRepository.getReferenceById(register.role().getValue());
		StaffJpaEntity entity = StaffJpaEntity.construct(id, register, tenant, role);
		StaffJpaEntity saved = staffJpaRepository.save(entity);
		return StaffJpaEntity.construct(saved);
	}

	@Override
	public StaffPasswordDomain save(StaffPasswordDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffPasswordJpaEntity saved = staffPasswordJpaRepository.save(StaffPasswordJpaEntity.construct(id, register, staff));
		return StaffPasswordJpaEntity.construct(saved);
	}

	@Override
	public StaffEmailDomain save(StaffEmailDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffEmailJpaEntity saved = staffEmailJpaRepository.save(StaffEmailJpaEntity.construct(id, register, staff));
		return StaffEmailJpaEntity.construct(saved);
	}

	@Override
	public StaffAddressDomain save(StaffAddressDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffAddressJpaEntity saved = staffAddressJpaRepository.save(StaffAddressJpaEntity.construct(id, register, staff));
		return StaffAddressJpaEntity.constructUserAddressDomain(saved);
	}

	@Override
	public StaffPhoneDomain save(StaffPhoneDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffPhoneJpaEntity saved = staffPhoneJpaRepository.save(StaffPhoneJpaEntity.construct(id, register, staff));
		return StaffPhoneJpaEntity.construct(saved);
	}

	@Override
	public boolean existsByUsername(UsernameDomain username){
		return staffJpaRepository.existsByUsername(username.value());
	}

}
