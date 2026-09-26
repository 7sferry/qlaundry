package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.token.UserAuthPrincipal;
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
	public void execute(StaffDetailRequest request, UserAuthPrincipal principal, StaffDetailPresenter presenter){
		request.validate();
		Username username = new Username(request.username());
		TenantId tenantId = new TenantId(principal.tenantId());
		StaffDetailProjection staff = gateway.findDetail(username, tenantId)
				.orElseThrow(() -> new NotFoundException("Staff Not Found"));
		StaffId staffId = new StaffId(staff.id());
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
