package com.ferry.user.core.staff.update;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.*;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.token.UserAuthPrincipal;
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
	public void execute(StaffUpdateRequest request, UserAuthPrincipal principal, StaffUpdatePresenter presenter){
		request.validate();
		Staff staff = gateway.findById(principal.userId())
				.orElseThrow(() -> new NotFoundException("Staff Not Found"));
		StaffPassword newPassword = validatePasswordChange(staff, request);
		if(newPassword != null){
			gateway.save(newPassword);
		}
		Staff saved = updateProfile(staff, request, principal);
		replaceEmails(request, saved, principal);
		replacePhones(request, saved, principal);
		replaceAddresses(request, saved, principal);
		List<StaffEmail> emails = gateway.findEmailsByStaffId(saved.id());
		List<StaffPhone> phones = gateway.findPhonesByStaffId(saved.id());
		List<StaffAddress> addresses = gateway.findAddressesByStaffId(saved.id());
		presenter.present(new StaffUpdateResponse(saved, emails, phones, addresses));
	}

	private Staff updateProfile(Staff staff, StaffUpdateRequest request, UserAuthPrincipal principal){
		FullName fullName = new FullName(request.fullName());
		Description description = new Description(request.description());
		return gateway.save(staff.update(fullName, description, principal.userId()));
	}

	private StaffPassword validatePasswordChange(Staff staff, StaffUpdateRequest request){
		if(request.newPassword() == null || request.newPassword().isBlank()){
			return null;
		}
		if(request.currentPassword() == null){
			throw new InvalidPasswordException("Current password is incorrect");
		}
		StaffPasswordProjection currentPassword = gateway.findCurrentPassword(staff.id())
				.orElseThrow(() -> new InvalidPasswordException("Current password is incorrect"));
		validateCurrentPassword(request, currentPassword);
		RawPassword newRawPassword = new RawPassword(request.newPassword());
		validateRecentPassword(newRawPassword, currentPassword);
		validateLastUsedPasswords(staff, newRawPassword);
		return StaffPassword.register(staff.id(), passwordTool.hash(newRawPassword), staff.id());
	}

	private void validateLastUsedPasswords(Staff staff, RawPassword newRawPassword){
		Instant passwordReuseCutoff = Instant.now().minus(PasswordConstant.PASSWORD_REUSE_WINDOW);
		List<StaffPasswordProjection> recentPasswords = gateway.findRecentPasswords(staff.id(), passwordReuseCutoff);
		boolean reused = recentPasswords.stream()
				.anyMatch(recent -> passwordTool.matches(newRawPassword.value(), recent.password()));
		if(reused){
			throw new InvalidPasswordException("Password was used within the last 3 months, please choose a different password");
		}
	}

	private void validateRecentPassword(RawPassword newRawPassword, StaffPasswordProjection currentPassword){
		if(passwordTool.matches(newRawPassword.value(), currentPassword.password())){
			throw new InvalidPasswordException("New password must be different from your current password");
		}
	}

	private void validateCurrentPassword(StaffUpdateRequest request, StaffPasswordProjection currentPassword){
		if(!passwordTool.matches(request.currentPassword(), currentPassword.password())){
			throw new InvalidPasswordException("Current password is incorrect");
		}
	}

	private void replaceEmails(StaffUpdateRequest request, Staff staff, UserAuthPrincipal principal){
		List<String> emails = request.emails() == null ? List.of() : request.emails();
		if(emails.isEmpty()){
			throw new InvalidUserStateException("Emails cannot be empty");
		}
		gateway.deleteEmails(staff.id(), principal.userId());
		for(String email : emails){
			gateway.save(StaffEmail.register(staff.id(), new Email(email), principal.userId()));
		}
	}

	private void replacePhones(StaffUpdateRequest request, Staff staff, UserAuthPrincipal principal){
		List<String> phones = request.phones() == null ? List.of() : request.phones();
		gateway.deletePhones(staff.id(), principal.userId());
		for(String phone : phones){
			gateway.save(StaffPhone.register(staff.id(), new Phone(phone), principal.userId()));
		}
	}

	private void replaceAddresses(StaffUpdateRequest request, Staff staff, UserAuthPrincipal principal){
		List<String> addresses = request.addresses() == null ? List.of() : request.addresses();
		gateway.deleteAddresses(staff.id(), principal.userId());
		for(String address : addresses){
			gateway.save(StaffAddress.register(staff.id(), new AddressLine(address), principal.userId()));
		}
	}

}
