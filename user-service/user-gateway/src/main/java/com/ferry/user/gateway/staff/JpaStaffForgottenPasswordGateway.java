package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordGateway;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.forgottenpassword.StaffEmailForgottenPasswordProjection;
import com.ferry.user.gateway.staff.entity.StaffEmailJpa;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffForgottenPasswordGateway implements StaffForgottenPasswordGateway{
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<StaffEmailForgottenPasswordProjection> findEmailWithUsername(Username username){
		return staffEmailJpaRepository.findForForgottenPassword(username.value())
				.map(entity -> new StaffEmailForgottenPasswordProjection(
						StaffEmailJpa.construct(entity, cryptoTool).email().value(), entity.getStaffId()));
	}

}
