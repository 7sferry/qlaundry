package com.ferry.user.webservice.staff.update;

import com.ferry.user.core.staff.update.StaffUpdatePresenter;
import com.ferry.user.core.staff.update.StaffUpdateResponse;
import com.ferry.user.webservice.staff.update.StaffUpdateWebResponse.Address;
import com.ferry.user.webservice.staff.update.StaffUpdateWebResponse.Email;
import com.ferry.user.webservice.staff.update.StaffUpdateWebResponse.Phone;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class StaffUpdateWebPresenter implements StaffUpdatePresenter{
	private ResponseEntity<StaffUpdateWebResponse> responseEntity;

	@Override
	public void present(StaffUpdateResponse response){
		long createdAt = response.staff().createdAt().toEpochMilli();
		List<Email> emails = response.emails().stream().map(o -> new Email(o.email().value())).toList();
		List<Phone> phones = response.phones().stream().map(o -> new Phone(o.phone().value())).toList();
		List<Address> addresses = response.addresses().stream().map(o -> new Address(o.addressLine().value())).toList();
		responseEntity = ResponseEntity.ok(new StaffUpdateWebResponse(response.staff().descriptionValue(),
				response.staff().fullNameValue(), createdAt, response.staff().usernameValue(), emails, phones, addresses));
	}

}
