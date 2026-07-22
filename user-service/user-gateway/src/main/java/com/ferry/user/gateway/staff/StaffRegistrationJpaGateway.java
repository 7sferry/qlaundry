package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.registration.StaffRegistrationGateway;
import com.ferry.user.domain.*;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
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
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final StaffAddressJpaRepository staffAddressJpaRepository;
	private final StaffPhoneJpaRepository staffPhoneJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public StaffDomain save(StaffDomain register){
		String id = idGenerator.generateId();
		TenantJpaEntity tenant = tenantJpaRepository.getReferenceById(register.tenantId());
		StaffJpaEntity entity = StaffJpaEntity.create(id, register, tenant);
		StaffJpaEntity saved = staffJpaRepository.save(entity);
		return StaffJpaEntity.construct(saved);
	}

	@Override
	public StaffEmailDomain save(StaffEmailDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffEmailJpaEntity saved = staffEmailJpaRepository.save(StaffEmailJpaEntity.create(id, register, staff));
		return StaffEmailJpaEntity.constructUserEmailDomain(saved);
	}

	@Override
	public StaffAddressDomain save(StaffAddressDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffAddressJpaEntity saved = staffAddressJpaRepository.save(StaffAddressJpaEntity.create(id, register, staff));
		return StaffAddressJpaEntity.constructUserAddressDomain(saved);
	}

	@Override
	public StaffPhoneDomain save(StaffPhoneDomain register){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffPhoneJpaEntity saved = staffPhoneJpaRepository.save(StaffPhoneJpaEntity.create(id, register, staff));
		return StaffPhoneJpaEntity.constructUserPhoneDomain(saved);
	}

	@Override
	public boolean existsByUsername(UsernameDomain username){
		return staffJpaRepository.existsByUsername(username.value());
	}

}
