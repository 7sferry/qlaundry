package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordGateway;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.forgottenpassword.StaffEmailForgottenPasswordProjection;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffForgottenPasswordJpaGateway implements StaffForgottenPasswordGateway{
	private final StaffEmailJpaRepository staffEmailJpaRepository;

	@Override
	public Optional<StaffEmailForgottenPasswordProjection> findEmailWithUsername(UsernameDomain username){
		return staffEmailJpaRepository.findForForgottenPassword(username.value());
	}

}
