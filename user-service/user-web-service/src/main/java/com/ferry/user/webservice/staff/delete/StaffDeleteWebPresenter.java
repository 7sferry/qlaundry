package com.ferry.user.webservice.staff.delete;

import com.ferry.user.core.staff.delete.StaffDeletePresenter;
import com.ferry.user.core.staff.delete.StaffDeleteResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class StaffDeleteWebPresenter implements StaffDeletePresenter{
	private ResponseEntity<StaffDeleteWebResponse> responseEntity;

	@Override
	public void present(StaffDeleteResponse response){
		responseEntity = ResponseEntity.ok(new StaffDeleteWebResponse(response.username()));
	}

}
