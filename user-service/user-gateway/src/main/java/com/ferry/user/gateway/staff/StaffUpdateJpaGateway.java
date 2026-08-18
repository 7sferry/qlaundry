package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.update.StaffUpdateGateway;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPasswordJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffRoleJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPasswordJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffRoleJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffUpdateJpaGateway implements StaffUpdateGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffPasswordJpaRepository staffPasswordJpaRepository;
	private final StaffRoleJpaRepository staffRoleJpaRepository;
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final StaffPhoneJpaRepository staffPhoneJpaRepository;
	private final StaffAddressJpaRepository staffAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<StaffDomain> findById(String id){
		return staffJpaRepository.findByIdAndDeletedIsFalse(id, StaffJpaEntity.class).map(StaffJpaEntity::construct);
	}

	@Override
	public StaffDomain save(StaffDomain staff){
		TenantJpaEntity tenant = tenantJpaRepository.getReferenceById(staff.tenantId());
		StaffRoleJpaEntity role = staffRoleJpaRepository.getReferenceById(staff.role().getValue());
		StaffJpaEntity saved = staffJpaRepository.save(StaffJpaEntity.construct(staff.id(), staff, tenant, role));
		return StaffJpaEntity.construct(saved);
	}

	@Override
	public Optional<StaffPasswordProjection> findCurrentPassword(String staffId){
		return staffPasswordJpaRepository.findCurrent(staffId);
	}

	@Override
	public List<StaffPasswordProjection> findRecentPasswords(String staffId, Instant since){
		return staffPasswordJpaRepository.findRecent(staffId, since);
	}

	@Override
	public void save(StaffPasswordDomain password){
		staffPasswordJpaRepository.softDeleteByStaffId(password.staffId(), password.createdBy());
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(password.staffId());
		staffPasswordJpaRepository.save(StaffPasswordJpaEntity.construct(id, password, staff));
	}

	@Override
	public List<StaffEmailDomain> findEmailsByStaffId(String staffId){
		StaffEmailFilter filter = StaffEmailFilter.builder().staffId(staffId).build();
		return staffEmailJpaRepository.findAllWithFilter(filter, StaffEmailJpaEntity.class).stream()
				.map(entity -> StaffEmailJpaEntity.construct(entity, cryptoTool)).toList();
	}

	@Override
	public List<StaffPhoneDomain> findPhonesByStaffId(String staffId){
		StaffPhoneFilter filter = StaffPhoneFilter.builder().staffId(staffId).build();
		return staffPhoneJpaRepository.findAllWithFilter(filter, StaffPhoneJpaEntity.class).stream()
				.map(entity -> StaffPhoneJpaEntity.construct(entity, cryptoTool)).toList();
	}

	@Override
	public List<StaffAddressDomain> findAddressesByStaffId(String staffId){
		StaffAddressFilter filter = StaffAddressFilter.builder().staffId(staffId).build();
		return staffAddressJpaRepository.findAllWithFilter(filter, StaffAddressJpaEntity.class).stream()
				.map(entity -> StaffAddressJpaEntity.constructUserAddressDomain(entity, cryptoTool)).toList();
	}

	@Override
	public void deleteEmails(String staffId, String updatedBy){
		staffEmailJpaRepository.softDeleteByStaffId(staffId, updatedBy);
	}

	@Override
	public void deletePhones(String staffId, String updatedBy){
		staffPhoneJpaRepository.softDeleteByStaffId(staffId, updatedBy);
	}

	@Override
	public void deleteAddresses(String staffId, String updatedBy){
		staffAddressJpaRepository.softDeleteByStaffId(staffId, updatedBy);
	}

	@Override
	public StaffEmailDomain save(StaffEmailDomain email){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(email.staffId());
		StaffEmailJpaEntity saved = staffEmailJpaRepository.save(StaffEmailJpaEntity.construct(id, email, staff, cryptoTool));
		return StaffEmailJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public StaffPhoneDomain save(StaffPhoneDomain phone){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(phone.staffId());
		StaffPhoneJpaEntity saved = staffPhoneJpaRepository.save(StaffPhoneJpaEntity.construct(id, phone, staff, cryptoTool));
		return StaffPhoneJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public StaffAddressDomain save(StaffAddressDomain address){
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(address.staffId());
		StaffAddressJpaEntity saved = staffAddressJpaRepository.save(StaffAddressJpaEntity.construct(id, address, staff, cryptoTool));
		return StaffAddressJpaEntity.constructUserAddressDomain(saved, cryptoTool);
	}

}
