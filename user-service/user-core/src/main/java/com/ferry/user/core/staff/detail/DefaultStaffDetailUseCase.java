package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffDetailUseCase implements StaffDetailUseCase{
	private final StaffDetailGateway gateway;

	@Override
	public void execute(StaffDetailRequest request, UserPrincipal principal, StaffDetailPresenter presenter){
		UsernameDomain username = new UsernameDomain(request.username());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		StaffDetailProjection staff = gateway.findDetail(username, tenantId)
				.orElseThrow(() -> new NotFoundException("Staff Not Found"));
		StaffIdDomain staffId = new StaffIdDomain(staff.id());
		StaffPhoneFilter phoneFilter = StaffPhoneFilter.builder()
				.staffId(staffId.value())
				.build();
		List<StaffPhoneDetailProjection> phones = gateway.findByFilter(phoneFilter);
		StaffEmailFilter emailFilter = StaffEmailFilter.builder()
				.staffId(staffId.value())
				.build();
		List<StaffEmailDetailProjection> emails = gateway.findByFilter(emailFilter);
		StaffAddressFilter addressFilter = StaffAddressFilter.builder()
				.staffId(staffId.value())
				.build();
		List<StaffAddressDetailProjection> addresses = gateway.findByFilter(addressFilter);
		presenter.present(new StaffDetailResponse(staff, phones, emails, addresses));
	}

}
