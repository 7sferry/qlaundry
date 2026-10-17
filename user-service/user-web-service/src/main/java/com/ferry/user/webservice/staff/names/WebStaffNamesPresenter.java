package com.ferry.user.webservice.staff.names;

import com.ferry.user.core.staff.names.StaffNamesPresenter;
import com.ferry.user.core.staff.names.StaffNamesResponse;
import com.ferry.user.webservice.staff.names.StaffNamesRestResponse.StaffName;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebStaffNamesPresenter implements StaffNamesPresenter{
	private ResponseEntity<StaffNamesRestResponse> responseEntity;

	@Override
	public void present(StaffNamesResponse response){
		responseEntity = ResponseEntity.ok(new StaffNamesRestResponse(response.staffs().stream()
				.map(staff -> new StaffName(staff.staffId(), staff.fullName()))
				.toList()));
	}

}
