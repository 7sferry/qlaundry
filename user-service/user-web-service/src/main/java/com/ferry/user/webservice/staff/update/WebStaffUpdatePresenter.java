package com.ferry.user.webservice.staff.update;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.staff.update.StaffUpdatePresenter;
import com.ferry.user.core.staff.update.StaffUpdateResponse;
import com.ferry.user.webservice.staff.update.StaffUpdateRestResponse.Address;
import com.ferry.user.webservice.staff.update.StaffUpdateRestResponse.Email;
import com.ferry.user.webservice.staff.update.StaffUpdateRestResponse.Phone;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@RequiredArgsConstructor
public class WebStaffUpdatePresenter implements StaffUpdatePresenter{
	private final HttpServletResponse servletResponse;
	private ResponseEntity<StaffUpdateRestResponse> responseEntity;

	@Override
	public void present(StaffUpdateResponse response){
		long createdAt = response.staff().createdAt().toEpochMilli();
		List<Email> emails = response.emails().stream().map(o -> new Email(o.email().value())).toList();
		List<Phone> phones = response.phones().stream().map(o -> new Phone(o.phone().value())).toList();
		List<Address> addresses = response.addresses().stream().map(o -> new Address(o.addressLine().value())).toList();
		responseEntity = ResponseEntity.ok(new StaffUpdateRestResponse(response.staff().descriptionValue(),
				response.staff().fullNameValue(), createdAt, response.staff().usernameValue(),
				response.staff().role().name(), emails, phones, addresses));
		if(response.passwordChanged()){
			servletResponse.addHeader(HttpHeaders.SET_COOKIE, getExpiredRefreshCookie().toString());
		}
	}

	private ResponseCookie getExpiredRefreshCookie(){
		return ResponseCookie.from(TokenConstant.REFRESH_TOKEN_COOKIE, "")
				.httpOnly(true)
				.secure(false)
				.path(TokenConstant.AUTH_PATH)
				.maxAge(0)
				.sameSite("Lax")
				.build();
	}

}
