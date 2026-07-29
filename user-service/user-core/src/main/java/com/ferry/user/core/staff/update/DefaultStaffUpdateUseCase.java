package com.ferry.user.core.staff.update;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.AddressLineDomain;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.common.RawPasswordDomain;
import com.ferry.user.domain.staff.update.InvalidPasswordException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.domain.token.UserPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffUpdateUseCase implements StaffUpdateUseCase{
	private final StaffUpdateGateway gateway;
	private final PasswordTool passwordTool;

	@Override
	public void execute(StaffUpdateRequest request, UserPrincipal principal, StaffUpdatePresenter presenter){
		StaffDomain staff = gateway.findById(principal.userId())
				.orElseThrow(() -> new NotFoundException("Staff Not Found"));
		StaffDomain saved = gateway.save(updateProfile(staff, request, principal));
		replaceEmails(request, saved, principal);
		replacePhones(request, saved, principal);
		replaceAddresses(request, saved, principal);
		List<StaffEmailDomain> emails = gateway.findEmailsByStaffId(saved.id());
		List<StaffPhoneDomain> phones = gateway.findPhonesByStaffId(saved.id());
		List<StaffAddressDomain> addresses = gateway.findAddressesByStaffId(saved.id());
		presenter.present(new StaffUpdateResponse(saved, emails, phones, addresses));
	}

	private StaffDomain updateProfile(StaffDomain staff, StaffUpdateRequest request, UserPrincipal principal){
		FullNameDomain fullName = new FullNameDomain(request.fullName());
		DescriptionDomain description = new DescriptionDomain(request.description());
		HashedPasswordDomain password = getPassword(request, staff);
		return staff.toBuilder()
				.fullName(fullName)
				.description(description)
				.updatedBy(principal.userId())
				.updatedAt(Instant.now())
				.password(password)
				.build();
	}

	private HashedPasswordDomain getPassword(StaffUpdateRequest request, StaffDomain staff){
		if(request.newPassword() == null || request.newPassword().isBlank()){
			return staff.password();
		}
		return changePassword(staff, request);
	}

	private HashedPasswordDomain changePassword(StaffDomain staff, StaffUpdateRequest request){
		if(request.currentPassword() == null || !passwordTool.matches(request.currentPassword(), staff.passwordValue())){
			throw new InvalidPasswordException("Current password is incorrect");
		}
		return passwordTool.hash(new RawPasswordDomain(request.newPassword()));
	}

	private void replaceEmails(StaffUpdateRequest request, StaffDomain staff, UserPrincipal principal){
		List<String> emails = request.emails() == null ? List.of() : request.emails();
		if(emails.isEmpty()){
			throw new IllegalArgumentException("Emails cannot be empty");
		}
		gateway.deleteEmails(staff.id(), principal.userId());
		for(String email : emails){
			gateway.save(StaffEmailDomain.register(staff.id(), new EmailDomain(email), principal.userId()));
		}
	}

	private void replacePhones(StaffUpdateRequest request, StaffDomain staff, UserPrincipal principal){
		List<String> phones = request.phones() == null ? List.of() : request.phones();
		gateway.deletePhones(staff.id(), principal.userId());
		for(String phone : phones){
			gateway.save(StaffPhoneDomain.register(staff.id(), new PhoneDomain(phone), principal.userId()));
		}
	}

	private void replaceAddresses(StaffUpdateRequest request, StaffDomain staff, UserPrincipal principal){
		List<String> addresses = request.addresses() == null ? List.of() : request.addresses();
		gateway.deleteAddresses(staff.id(), principal.userId());
		for(String address : addresses){
			gateway.save(StaffAddressDomain.register(staff.id(), new AddressLineDomain(address), principal.userId()));
		}
	}

}
