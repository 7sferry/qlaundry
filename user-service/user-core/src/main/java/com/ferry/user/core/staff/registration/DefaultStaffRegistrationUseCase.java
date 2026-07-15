package com.ferry.user.core.staff.registration;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.*;
import com.ferry.user.domain.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
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
	public void execute(StaffRegistrationRequest request, StaffRegistrationPresenter presenter){
		StaffDomain registeredUser = registerStaff(request);
		saveEmail(request, registeredUser);
		saveAddress(request, registeredUser);
		savePhone(request, registeredUser);
		presenter.present(new StaffRegistrationResponse(registeredUser));
	}

	private StaffDomain registerStaff(StaffRegistrationRequest request){
		UsernameDomain username = new UsernameDomain(request.username());
		HashedPasswordDomain hashedPassword = passwordTool.hash(new RawPasswordDomain(request.password()));
		FullNameDomain fullName = new FullNameDomain(request.fullName());
		DescriptionDomain note = new DescriptionDomain(request.description());
		StaffDomain registered = StaffDomain.register(username, hashedPassword, fullName, note, request.tenantId(), request.createdBy());
		if(gateway.existsByUsername(registered.username())){
			throw new InvalidUsernameException("Username already exists");
		}
		return gateway.save(registered);
	}

	private void savePhone(StaffRegistrationRequest request, StaffDomain registeredUser){
		List<String> phones = request.phones() == null ? List.of() : request.phones();
		for(String phone : phones){
			gateway.save(StaffPhoneDomain.register(registeredUser.id(), new PhoneDomain(phone), request.createdBy()));
		}
	}

	private void saveAddress(StaffRegistrationRequest request, StaffDomain registeredUser){
		List<String> addresses = request.addresses() == null ? List.of() : request.addresses();
		for(String address : addresses){
			gateway.save(StaffAddressDomain.register(registeredUser.id(), new AddressLineDomain(address), request.createdBy()));
		}
	}

	private void saveEmail(StaffRegistrationRequest request, StaffDomain registeredUser){
		List<String> emails = request.emails() == null ? List.of() : request.emails();
		for(String email : emails){
			gateway.save(StaffEmailDomain.register(registeredUser.id(), new EmailDomain(email), request.createdBy()));
		}
	}

}
