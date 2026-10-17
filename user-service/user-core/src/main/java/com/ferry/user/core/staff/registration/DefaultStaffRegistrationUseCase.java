package com.ferry.user.core.staff.registration;

import com.ferry.user.core.analytics.UserAnalyticsEvent;
import com.ferry.user.core.analytics.StaffAnalyticsMessage;
import com.ferry.user.core.analytics.UserAnalyticsPublisher;
import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.tools.PasswordContentPolicy;
import com.ferry.user.core.tools.PasswordSubject;
import com.ferry.user.core.tools.PasswordSubjectGateway;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
import com.ferry.user.domain.common.*;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
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
	private final UserAnalyticsPublisher analyticsPublisher;
	private final PasswordSubjectGateway subjectGateway;
	private final AuthAuditLog auditLog;

	@Override
	public void execute(StaffRegistrationRequest request, UserAuthPrincipal principal, StaffRegistrationPresenter presenter){
		try{
			request.validate();
			if(principal.role() != StaffRole.SUPER_STAFF){
				throw new ForbiddenActionException("Only super staff can register staff");
			}
			Staff registeredUser = registerStaff(request, principal);
			saveEmail(request, registeredUser, principal);
			saveAddress(request, registeredUser, principal);
			savePhone(request, registeredUser, principal);
			analyticsPublisher.publish(analyticsPublisher.save(UserAnalyticsEvent.staff(
					UserAnalyticsEventType.STAFF_REGISTERED, StaffAnalyticsMessage.from(registeredUser), principal.userId())));
			auditLog.record(AuthAction.STAFF_REGISTRATION, AuthResult.SUCCESS, principal.userId(),
					registeredUser.usernameValue());
			presenter.present(new StaffRegistrationResponse(registeredUser));
		} catch (RuntimeException e){
			auditLog.record(AuthAction.STAFF_REGISTRATION, AuthResult.FAILURE, principal.userId(), request.username());
			throw e;
		}
	}

	private Staff registerStaff(StaffRegistrationRequest request, UserAuthPrincipal principal){
		Username username = new Username(request.username());
		if(gateway.existsByUsername(username)){
			throw new InvalidUsernameException("Username already exists");
		}
		RawPassword rawPassword = new RawPassword(request.password());
		validatePasswordContent(request, principal, rawPassword);
		HashedPassword hashedPassword = passwordTool.hash(rawPassword);
		FullName fullName = new FullName(request.fullName());
		Description note = new Description(request.description());
		Staff registered = Staff.register(username, fullName, note, principal.tenantId(),
				request.role(), principal.userId());
		Staff saved = gateway.save(registered);
		gateway.save(StaffPassword.register(saved.id(), hashedPassword, principal.userId()));
		return saved;
	}

	private void validatePasswordContent(StaffRegistrationRequest request, UserAuthPrincipal principal, RawPassword rawPassword){
		PasswordSubject tenant = subjectGateway.findByTenantId(principal.tenantId())
				.orElseThrow(() -> new NotFoundException("Tenant Not Found"));
		PasswordSubject registered = new PasswordSubject(request.username(), request.fullName(), tenant.tenantName(),
				tenant.tenantUsername(), request.emails(), request.phones());
		PasswordContentPolicy.check(rawPassword.value(), registered);
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
