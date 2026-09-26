package com.ferry.user.webservice.staff.detail;

import com.ferry.user.core.staff.detail.StaffDetailPresenter;
import com.ferry.user.core.staff.detail.StaffDetailResponse;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.webservice.staff.detail.StaffDetailRestResponse.Address;
import com.ferry.user.webservice.staff.detail.StaffDetailRestResponse.Email;
import com.ferry.user.webservice.staff.detail.StaffDetailRestResponse.Phone;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class WebStaffDetailPresenter implements StaffDetailPresenter{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneOffset.UTC);
	private ResponseEntity<StaffDetailRestResponse> responseEntity;

	@Override
	public void present(StaffDetailResponse response){
		StaffDetailProjection staff = response.staff();
		long createdAt = staff.createdAt().toEpochMilli();
		List<Email> emails = response.emails().stream().map(o -> new Email(o.email())).toList();
		List<Phone> phones = response.phones().stream().map(o -> new Phone(o.phone())).toList();
		List<Address> addresses = response.addresses().stream().map(o -> new Address(o.addressLine())).toList();
		responseEntity = ResponseEntity.ok(new StaffDetailRestResponse(staff.description(), staff.fullName(),
				createdAt, staff.username(), emails, phones, addresses));
	}

}
