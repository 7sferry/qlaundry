package com.ferry.user.webservice.staff.detail;

import com.ferry.user.core.staff.detail.StaffDetailPresenter;
import com.ferry.user.core.staff.detail.StaffDetailResponse;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.webservice.staff.detail.StaffDetailWebResponse.Address;
import com.ferry.user.webservice.staff.detail.StaffDetailWebResponse.Email;
import com.ferry.user.webservice.staff.detail.StaffDetailWebResponse.Phone;
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
public class StaffDetailWebPresenter implements StaffDetailPresenter{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneOffset.UTC);
	private ResponseEntity<StaffDetailWebResponse> responseEntity;

	@Override
	public void present(StaffDetailResponse response){
		StaffDetailProjection staff = response.staff();
		long createdAt = staff.createdAt().toEpochMilli();
		List<Email> emails = response.emails().stream().map(o -> new Email(o.email())).toList();
		List<Phone> phones = response.phones().stream().map(o -> new Phone(o.phone())).toList();
		List<Address> addresses = response.addresses().stream().map(o -> new Address(o.addressLine())).toList();
		responseEntity = ResponseEntity.ok(new StaffDetailWebResponse(staff.description(), staff.fullName(),
				createdAt, staff.username(), emails, phones, addresses));
	}

}
