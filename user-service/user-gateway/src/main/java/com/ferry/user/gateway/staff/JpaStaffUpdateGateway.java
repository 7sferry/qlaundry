package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.update.StaffUpdateGateway;
import com.ferry.user.domain.staff.StaffAddress;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffEmail;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPassword;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.StaffPhone;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.gateway.staff.entity.StaffAddressJpa;
import com.ferry.user.gateway.staff.entity.StaffEmailJpa;
import com.ferry.user.gateway.staff.entity.StaffJpa;
import com.ferry.user.gateway.staff.entity.StaffPasswordJpa;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpa;
import com.ferry.user.gateway.staff.entity.StaffRoleJpa;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPasswordJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffRoleJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
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
public class JpaStaffUpdateGateway implements StaffUpdateGateway{
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
	public Optional<Staff> findById(String id){
		return staffJpaRepository.findByIdAndDeletedIsFalse(id, StaffJpa.class).map(StaffJpa::construct);
	}

	@Override
	public Staff save(Staff staff){
		TenantJpa tenant = tenantJpaRepository.getReferenceById(staff.tenantId());
		StaffRoleJpa role = staffRoleJpaRepository.getReferenceById(staff.role().getValue());
		StaffJpa saved = staffJpaRepository.save(StaffJpa.construct(staff.id(), staff, tenant, role));
		return StaffJpa.construct(saved);
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
	public void save(StaffPassword password){
		staffPasswordJpaRepository.softDeleteByStaffId(password.staffId(), password.createdBy());
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(password.staffId());
		staffPasswordJpaRepository.save(StaffPasswordJpa.construct(id, password, staff));
	}

	@Override
	public List<StaffEmail> findEmailsByStaffId(String staffId){
		StaffEmailFilter filter = StaffEmailFilter.builder().staffId(staffId).build();
		return staffEmailJpaRepository.findAllWithFilter(filter, StaffEmailJpa.class).stream()
				.map(entity -> StaffEmailJpa.construct(entity, cryptoTool)).toList();
	}

	@Override
	public List<StaffPhone> findPhonesByStaffId(String staffId){
		StaffPhoneFilter filter = StaffPhoneFilter.builder().staffId(staffId).build();
		return staffPhoneJpaRepository.findAllWithFilter(filter, StaffPhoneJpa.class).stream()
				.map(entity -> StaffPhoneJpa.construct(entity, cryptoTool)).toList();
	}

	@Override
	public List<StaffAddress> findAddressesByStaffId(String staffId){
		StaffAddressFilter filter = StaffAddressFilter.builder().staffId(staffId).build();
		return staffAddressJpaRepository.findAllWithFilter(filter, StaffAddressJpa.class).stream()
				.map(entity -> StaffAddressJpa.constructUserAddressDomain(entity, cryptoTool)).toList();
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
	public StaffEmail save(StaffEmail email){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(email.staffId());
		StaffEmailJpa saved = staffEmailJpaRepository.save(StaffEmailJpa.construct(id, email, staff, cryptoTool));
		return StaffEmailJpa.construct(saved, cryptoTool);
	}

	@Override
	public StaffPhone save(StaffPhone phone){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(phone.staffId());
		StaffPhoneJpa saved = staffPhoneJpaRepository.save(StaffPhoneJpa.construct(id, phone, staff, cryptoTool));
		return StaffPhoneJpa.construct(saved, cryptoTool);
	}

	@Override
	public StaffAddress save(StaffAddress address){
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(address.staffId());
		StaffAddressJpa saved = staffAddressJpaRepository.save(StaffAddressJpa.construct(id, address, staff, cryptoTool));
		return StaffAddressJpa.constructUserAddressDomain(saved, cryptoTool);
	}

}
