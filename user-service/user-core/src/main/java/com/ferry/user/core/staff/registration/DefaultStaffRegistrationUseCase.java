package com.ferry.user.core.staff.registration;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.*;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffRegistrationUseCase implements StaffRegistrationUseCase{
	private final StaffRegistrationGateway gateway;
	private final PasswordTool passwordTool;

	@Override
	public void execute(StaffRegistrationRequest request, UserAuthPrincipal principal, StaffRegistrationPresenter presenter){
		request.validate();
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new ForbiddenActionException("Only super staff can register staff");
		}
		Staff registeredUser = registerStaff(request, principal);
		saveEmail(request, registeredUser, principal);
		saveAddress(request, registeredUser, principal);
		savePhone(request, registeredUser, principal);
		presenter.present(new StaffRegistrationResponse(registeredUser));
	}

	private Staff registerStaff(StaffRegistrationRequest request, UserAuthPrincipal principal){
		Username username = new Username(request.username());
		if(gateway.existsByUsername(username)){
			throw new InvalidUsernameException("Username already exists");
		}
		HashedPassword hashedPassword = passwordTool.hash(new RawPassword(request.password()));
		FullName fullName = new FullName(request.fullName());
		Description note = new Description(request.description());
		Staff registered = Staff.register(username, fullName, note, principal.tenantId(),
				request.role(), principal.userId());
		Staff saved = gateway.save(registered);
		gateway.save(StaffPassword.register(saved.id(), hashedPassword, principal.userId()));
		return saved;
	}

	private void savePhone(StaffRegistrationRequest request, Staff registeredUser, UserAuthPrincipal principal){
		List<String> phones = request.phones() == null ? List.of() : request.phones();
		for(String phone : phones){
			gateway.save(StaffPhone.register(registeredUser.id(), new Phone(phone), principal.userId()));
		}
	}

	private void saveAddress(StaffRegistrationRequest request, Staff registeredUser, UserAuthPrincipal principal){
		List<String> addresses = request.addresses() == null ? List.of() : request.addresses();
		for(String address : addresses){
			gateway.save(StaffAddress.register(registeredUser.id(), new AddressLine(address),
					principal.userId()));
		}
	}

	private void saveEmail(StaffRegistrationRequest request, Staff registeredUser, UserAuthPrincipal principal){
		List<String> emails = request.emails() == null ? List.of() : request.emails();
		if(emails.isEmpty()){
			throw new InvalidUserStateException("Emails cannot be empty");
		}
		for(String email : emails){
			gateway.save(StaffEmail.register(registeredUser.id(), new Email(email), principal.userId()));
		}
	}

}
