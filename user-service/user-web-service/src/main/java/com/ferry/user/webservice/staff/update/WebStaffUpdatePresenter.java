package com.ferry.user.webservice.staff.update;

import com.ferry.user.core.staff.update.StaffUpdatePresenter;
import com.ferry.user.core.staff.update.StaffUpdateResponse;
import com.ferry.user.webservice.staff.update.StaffUpdateRestResponse.Address;
import com.ferry.user.webservice.staff.update.StaffUpdateRestResponse.Email;
import com.ferry.user.webservice.staff.update.StaffUpdateRestResponse.Phone;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class WebStaffUpdatePresenter implements StaffUpdatePresenter{
	private ResponseEntity<StaffUpdateRestResponse> responseEntity;

	@Override
	public void present(StaffUpdateResponse response){
		long createdAt = response.staff().createdAt().toEpochMilli();
		List<Email> emails = response.emails().stream().map(o -> new Email(o.email().value())).toList();
		List<Phone> phones = response.phones().stream().map(o -> new Phone(o.phone().value())).toList();
		List<Address> addresses = response.addresses().stream().map(o -> new Address(o.addressLine().value())).toList();
		responseEntity = ResponseEntity.ok(new StaffUpdateRestResponse(response.staff().descriptionValue(),
				response.staff().fullNameValue(), createdAt, response.staff().usernameValue(), emails, phones, addresses));
	}

}
