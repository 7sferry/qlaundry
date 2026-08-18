package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.forgotpassword.StaffForgottenPasswordGateway;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.forgottenpassword.StaffEmailForgottenPasswordProjection;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffForgottenPasswordJpaGateway implements StaffForgottenPasswordGateway{
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<StaffEmailForgottenPasswordProjection> findEmailWithUsername(UsernameDomain username){
		return staffEmailJpaRepository.findForForgottenPassword(username.value())
				.map(entity -> new StaffEmailForgottenPasswordProjection(
						StaffEmailJpaEntity.construct(entity, cryptoTool).email().value(), entity.getStaffId()));
	}

}
