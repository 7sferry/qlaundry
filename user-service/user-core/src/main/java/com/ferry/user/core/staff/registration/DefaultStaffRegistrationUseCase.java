package com.ferry.user.core.staff.registration;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.*;
import com.ferry.user.domain.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.domain.token.UserPrincipal;
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
	public void execute(StaffRegistrationRequest request, UserPrincipal principal, StaffRegistrationPresenter presenter){
		StaffDomain registeredUser = registerStaff(request, principal);
		saveEmail(request, registeredUser, principal);
		saveAddress(request, registeredUser, principal);
		savePhone(request, registeredUser, principal);
		presenter.present(new StaffRegistrationResponse(registeredUser));
	}

	private StaffDomain registerStaff(StaffRegistrationRequest request, UserPrincipal principal){
		UsernameDomain username = new UsernameDomain(request.username());
		if(gateway.existsByUsername(username)){
			throw new InvalidUsernameException("Username already exists");
		}
		HashedPasswordDomain hashedPassword = passwordTool.hash(new RawPasswordDomain(request.password()));
		FullNameDomain fullName = new FullNameDomain(request.fullName());
		DescriptionDomain note = new DescriptionDomain(request.description());
		StaffDomain registered = StaffDomain.register(username, hashedPassword, fullName, note, principal.tenantId(),
				principal.userId());
		return gateway.save(registered);
	}

	private void savePhone(StaffRegistrationRequest request, StaffDomain registeredUser, UserPrincipal principal){
		List<String> phones = request.phones() == null ? List.of() : request.phones();
		for(String phone : phones){
			gateway.save(StaffPhoneDomain.register(registeredUser.id(), new PhoneDomain(phone), principal.userId()));
		}
	}

	private void saveAddress(StaffRegistrationRequest request, StaffDomain registeredUser, UserPrincipal principal){
		List<String> addresses = request.addresses() == null ? List.of() : request.addresses();
		for(String address : addresses){
			gateway.save(StaffAddressDomain.register(registeredUser.id(), new AddressLineDomain(address),
					principal.userId()));
		}
	}

	private void saveEmail(StaffRegistrationRequest request, StaffDomain registeredUser, UserPrincipal principal){
		List<String> emails = request.emails() == null ? List.of() : request.emails();
		if(emails.isEmpty()){
			throw new IllegalArgumentException("Emails cannot be empty");
		}
		for(String email : emails){
			gateway.save(StaffEmailDomain.register(registeredUser.id(), new EmailDomain(email), principal.userId()));
		}
	}

}
