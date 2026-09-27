package com.ferry.user.webservice.staff.list;

import com.ferry.user.core.staff.list.StaffListPresenter;
import com.ferry.user.core.staff.list.StaffListResponse;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.webservice.staff.list.StaffListRestResponse.Address;
import com.ferry.user.webservice.staff.list.StaffListRestResponse.Email;
import com.ferry.user.webservice.staff.list.StaffListRestResponse.Phone;
import com.ferry.user.webservice.staff.list.StaffListRestResponse.Staff;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class WebStaffListPresenter implements StaffListPresenter{
	private ResponseEntity<StaffListRestResponse> responseEntity;

	@Override
	public void present(StaffListResponse response){
		List<Staff> staffs = response.staffs().stream()
				.map(o -> {
					List<Email> emails = response.emailsByStaffId().getOrDefault(o.id(), List.of()).stream()
							.map(e -> new Email(e.email()))
							.toList();
					List<Phone> phones = response.phonesByStaffId().getOrDefault(o.id(), List.of()).stream()
							.map(e -> new Phone(e.phone()))
							.toList();
					List<Address> addresses = response.addressesByStaffId().getOrDefault(o.id(), List.of()).stream()
							.map(e -> new Address(e.addressLine()))
							.toList();
					return new Staff(o.description(), o.fullName(), o.createdAt().toEpochMilli(), o.username(),
							o.role().name(), emails, phones, addresses);
				})
				.toList();
		responseEntity = ResponseEntity.ok(new StaffListRestResponse(staffs, response.nextCursor(), response.prevCursor()));
	}

}
