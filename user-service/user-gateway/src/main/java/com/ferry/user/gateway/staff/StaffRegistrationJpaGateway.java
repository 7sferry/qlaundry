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
		StaffJpaEntity entity = new StaffJpaEntity();
		entity.setId(idGenerator.generateId());
		entity.setUsername(register.usernameValue());
		entity.setDescription(register.descriptionValue());
		entity.setPassword(register.passwordValue());
		entity.setFullName(register.fullNameValue());
		entity.setTenant(tenantJpaRepository.findById(register.tenantId()).orElse(null));
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		StaffJpaEntity saved = staffJpaRepository.save(entity);
		return StaffJpaEntity.constructUserDomain(saved);
	}

	@Override
	public StaffEmailDomain save(StaffEmailDomain register){
		StaffEmailJpaEntity entity = new StaffEmailJpaEntity();
		entity.setId(idGenerator.generateId());
		entity.setStaff(staffJpaRepository.findById(register.staffId()).orElse(null));
		entity.setEmail(register.email().value());
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		StaffEmailJpaEntity saved = staffEmailJpaRepository.save(entity);
		return StaffEmailJpaEntity.constructUserEmailDomain(saved);
	}

	@Override
	public StaffAddressDomain save(StaffAddressDomain register){
		StaffAddressJpaEntity entity = new StaffAddressJpaEntity();
		entity.setId(idGenerator.generateId());
		entity.setStaff(staffJpaRepository.findById(register.staffId()).orElse(null));
		entity.setAddressLine(register.addressLine().value());
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		StaffAddressJpaEntity saved = staffAddressJpaRepository.save(entity);
		return StaffAddressJpaEntity.constructUserAddressDomain(saved);
	}

	@Override
	public StaffPhoneDomain save(StaffPhoneDomain register){
		StaffPhoneJpaEntity entity = new StaffPhoneJpaEntity();
		entity.setId(idGenerator.generateId());
		entity.setStaff(staffJpaRepository.findById(register.staffId()).orElse(null));
		entity.setPhone(register.phone().value());
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		StaffPhoneJpaEntity saved = staffPhoneJpaRepository.save(entity);
		return StaffPhoneJpaEntity.constructUserPhoneDomain(saved);
	}

	@Override
	public boolean existsByUsername(UsernameDomain username){
		return staffJpaRepository.existsByUsername(username.value());
	}

}
