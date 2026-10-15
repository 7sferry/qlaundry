package com.ferry.analytics.webservice.staff;

import com.ferry.analytics.core.staff.StaffSummaryPresenter;
import com.ferry.analytics.core.staff.StaffSummaryResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebStaffSummaryPresenter implements StaffSummaryPresenter{
	private ResponseEntity<StaffSummaryRestResponse> responseEntity;

	@Override
	public void present(StaffSummaryResponse response){
		responseEntity = ResponseEntity.ok(new StaffSummaryRestResponse(response.totalStaff(), response.newStaff()));
	}

}
