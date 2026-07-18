package com.ferry.user.core.staff.list;

import com.ferry.user.domain.staff.StaffListFilter;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
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
		StaffListFilter filter = new StaffListFilter(request.fullName(), tenantId);
		List<StaffListProjection> staffs = gateway.findByFilter(filter);
		List<String> staffIds = staffs.stream().map(StaffListProjection::id).toList();
		Map<String, List<StaffPhoneListProjection>> staffIdToPhones  = getStaffIdToPhones(staffIds);
		Map<String, List<StaffEmailListProjection>> staffIdToEmails = getStaffIdToEmails(staffIds);
		Map<String, List<StaffAddressListProjection>> staffIdToAddresses = getStaffIdToAddresses(staffIds);
		presenter.present(new StaffListResponse(staffs, staffIdToPhones, staffIdToEmails, staffIdToAddresses));
	}

	private Map<String, List<StaffAddressListProjection>> getStaffIdToAddresses(List<String> staffIds){
		return staffIds.isEmpty() ? Map.of() : gateway.findAddressesByStaffIds(staffIds).stream().collect(Collectors.groupingBy(StaffAddressListProjection::staffId));
	}

	private Map<String, List<StaffEmailListProjection>> getStaffIdToEmails(List<String> staffIds){
		return staffIds.isEmpty() ? Map.of() : gateway.findEmailsByStaffIds(staffIds).stream().collect(Collectors.groupingBy(StaffEmailListProjection::staffId));
	}

	private Map<String, List<StaffPhoneListProjection>> getStaffIdToPhones(List<String> staffIds){
		return staffIds.isEmpty() ? Map.of() : gateway.findPhonesByStaffIds(staffIds).stream().collect(Collectors.groupingBy(StaffPhoneListProjection::staffId));
	}

}
