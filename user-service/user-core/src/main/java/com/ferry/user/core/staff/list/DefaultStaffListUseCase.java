package com.ferry.user.core.staff.list;

import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffListUseCase implements StaffListUseCase{
	private final StaffListGateway gateway;

	@Override
	public void execute(StaffListRequest request, UserPrincipal principal, StaffListPresenter presenter){
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		StaffFilter filter = StaffFilter.builder()
				.fullName(request.fullName())
				.tenantId(tenantId.value())
				.build();
		List<StaffListProjection> staffs = gateway.findByFilter(filter);
		Set<String> staffIds = staffs.stream().map(StaffListProjection::id).collect(Collectors.toSet());
		Map<String, List<StaffPhoneListProjection>> staffIdToPhones  = getStaffIdToPhones(staffIds);
		Map<String, List<StaffEmailListProjection>> staffIdToEmails = getStaffIdToEmails(staffIds);
		Map<String, List<StaffAddressListProjection>> staffIdToAddresses = getStaffIdToAddresses(staffIds);
		presenter.present(new StaffListResponse(staffs, staffIdToPhones, staffIdToEmails, staffIdToAddresses));
	}

	private Map<String, List<StaffAddressListProjection>> getStaffIdToAddresses(Set<String> staffIds){
		StaffAddressFilter filter = StaffAddressFilter.builder()
				.staffIds(staffIds)
				.build();
		return staffIds.isEmpty() ? Map.of() : gateway.findAddressesByFilter(filter).stream().collect(Collectors.groupingBy(StaffAddressListProjection::staffId));
	}

	private Map<String, List<StaffEmailListProjection>> getStaffIdToEmails(Set<String> staffIds){
		StaffEmailFilter filter = StaffEmailFilter.builder()
				.staffIds(staffIds)
				.build();
		return staffIds.isEmpty() ? Map.of() : gateway.findEmailsByFilter(filter).stream().collect(Collectors.groupingBy(StaffEmailListProjection::staffId));
	}

	private Map<String, List<StaffPhoneListProjection>> getStaffIdToPhones(Set<String> staffIds){
		StaffPhoneFilter filter = StaffPhoneFilter.builder()
				.staffIds(staffIds)
				.build();
		return staffIds.isEmpty() ? Map.of() : gateway.findPhonesByFilter(filter).stream().collect(Collectors.groupingBy(StaffPhoneListProjection::staffId));
	}

}
