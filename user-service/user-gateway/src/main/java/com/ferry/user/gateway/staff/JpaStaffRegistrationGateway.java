package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.registration.StaffRegistrationGateway;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.StaffAddress;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffEmail;
import com.ferry.user.domain.staff.StaffPassword;
import com.ferry.user.domain.staff.StaffPhone;
import com.ferry.user.gateway.staff.entity.*;
import com.ferry.user.gateway.staff.repository.*;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffRegistrationGateway implements StaffRegistrationGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffPasswordJpaRepository staffPasswordJpaRepository;
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final StaffAddressJpaRepository staffAddressJpaRepository;
	private final StaffPhoneJpaRepository staffPhoneJpaRepository;
	private final StaffRoleJpaRepository staffRoleJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public Staff save(Staff register){
		String id = idGenerator.generateId();
		TenantJpa tenant = tenantJpaRepository.getReferenceById(register.tenantId());
		StaffRoleJpa role = staffRoleJpaRepository.getReferenceById(register.role().getValue());
		StaffJpa entity = StaffJpa.construct(id, register, tenant, role);
		StaffJpa saved = staffJpaRepository.save(entity);
		return StaffJpa.construct(saved);
	}

	@Override
	public StaffPassword save(StaffPassword register){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffPasswordJpa saved = staffPasswordJpaRepository.save(StaffPasswordJpa.construct(id, register, staff));
		return StaffPasswordJpa.construct(saved);
	}

	@Override
	public StaffEmail save(StaffEmail register){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffEmailJpa saved = staffEmailJpaRepository.save(StaffEmailJpa.construct(id, register, staff, cryptoTool));
		return StaffEmailJpa.construct(saved, cryptoTool);
	}

	@Override
	public StaffAddress save(StaffAddress register){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffAddressJpa saved = staffAddressJpaRepository.save(StaffAddressJpa.construct(id, register, staff, cryptoTool));
		return StaffAddressJpa.constructUserAddressDomain(saved, cryptoTool);
	}

	@Override
	public StaffPhone save(StaffPhone register){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(register.staffId());
		StaffPhoneJpa saved = staffPhoneJpaRepository.save(StaffPhoneJpa.construct(id, register, staff, cryptoTool));
		return StaffPhoneJpa.construct(saved, cryptoTool);
	}

	@Override
	public boolean existsByUsername(Username username){
		return staffJpaRepository.existsByUsername(username.value());
	}

}
