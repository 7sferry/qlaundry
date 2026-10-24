package com.ferry.user.core.staff.forgotpassword;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.core.tools.ResponseTimeFloor;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.forgottenpassword.ForgottenPasswordOtp;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultStaffForgottenPasswordUseCase implements StaffForgottenPasswordUseCase {
	private static final String ALPHABET = "qwertyuiopasdfghjklzxcvbnm";
	private static final BigInteger ALPHABET_LENGTH = BigInteger.valueOf(ALPHABET.length());
	private static final List<String> EMAIL_DOMAIN_PREFIXES = List.of("g","y","m");
	private static final BigInteger EMAIL_DOMAIN_LENGTH = BigInteger.valueOf(EMAIL_DOMAIN_PREFIXES.size());
	private static final int REPEATED_ASTERISK = 5;
	private static final char AT_SYMBOL = '@';
	private static final String TLD = ".com";
	private static final Duration MIN_RESPONSE_TIME = Duration.ofMillis(300);

	private final StaffForgottenPasswordGateway gateway;
	private final UserEmailPublisher emailPublisher;
	private final UserCacheManager userCacheManager;
	private final AuthAuditLog auditLog;
	private final ResponseTimeFloor responseTimeFloor;

	@Override
	public void execute(StaffForgottenPasswordRequest request, StaffForgottenPasswordPresenter presenter){
		long startedAt = System.nanoTime();
		try{
			request.validate();
			Username username = new Username(request.username());
			gateway.findEmailWithUsername(username).ifPresentOrElse(email -> {
				String otp = String.format("%06d", PasswordConstant.getRandom().nextInt(0, 1_000_000));
				userCacheManager.set(PasswordConstant.OTP_KEY + username.value(), otp,
						PasswordConstant.OTP_DURATION);
				ForgottenPasswordOtp payload = new ForgottenPasswordOtp(request.username(), otp);
				Email recipient = new Email(email.email());
				EmailTriggerConfig config = new EmailTriggerConfig(payload, email.staffId(),
						EmailTriggerType.FORGOTTEN_PASSWORD, recipient);
				EmailTrigger saved = emailPublisher.save(config);
				emailPublisher.publish(saved);
				auditLog.record(AuthAction.STAFF_FORGOTTEN_PASSWORD, AuthResult.SUCCESS, username.value());
				String maskedEmail = maskEmail(email.email());
				presenter.present(new StaffForgottenPasswordResponse(maskedEmail));
				responseTimeFloor.enforce(startedAt, MIN_RESPONSE_TIME);
			}, () -> {
				auditLog.record(AuthAction.STAFF_FORGOTTEN_PASSWORD, AuthResult.FAILURE, username.value());
				String maskedFakeEmail = maskFakeEmail(username.value());
				presenter.present(new StaffForgottenPasswordResponse(maskedFakeEmail));
				responseTimeFloor.enforce(startedAt, MIN_RESPONSE_TIME);
			});
		} catch (Exception e){
			log.error("Failed to process forgotten password request", e);
			auditLog.record(AuthAction.STAFF_FORGOTTEN_PASSWORD, AuthResult.FAILURE, request.username());
			String maskedFakeEmail = maskFakeEmail(request.username());
			presenter.present(new StaffForgottenPasswordResponse(maskedFakeEmail));
			responseTimeFloor.enforce(startedAt, MIN_RESPONSE_TIME);
		}
	}

	private String maskFakeEmail(String username) {
		byte[] hash = hash(username);
		BigInteger hashedInt = new BigInteger(1, hash);

		int firstIndex = hashedInt.mod(ALPHABET_LENGTH).intValue();
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);

		int lastIndex = hashedInt.mod(ALPHABET_LENGTH).intValue();
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);

		int domainIndex = hashedInt.mod(EMAIL_DOMAIN_LENGTH).intValue();

		return ALPHABET.charAt(firstIndex) +
				"*".repeat(REPEATED_ASTERISK) +
				ALPHABET.charAt(lastIndex) +
				AT_SYMBOL +
				EMAIL_DOMAIN_PREFIXES.get(domainIndex) +
				"*".repeat(REPEATED_ASTERISK) +
				TLD;
	}

	@SneakyThrows
	private byte[] hash(String input) {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	private String maskEmail(String email){
		int atIndex = email.indexOf(AT_SYMBOL);
		if(atIndex < 0){
			return maskFakeEmail(email);
		}
		String localName = email.substring(0, atIndex);
		String domainName = email.substring(atIndex + 1);
		StringBuilder result = new StringBuilder();
		if(!localName.isEmpty()){
			result.append(localName.charAt(0));
		}
		result.repeat("*", REPEATED_ASTERISK);
		if(localName.length() > 1){
			result.append(localName.charAt(localName.length() - 1));
		}
		result.append(AT_SYMBOL);
		if(!domainName.isEmpty()){
			result.append(domainName.charAt(0));
		}
		result.repeat("*", REPEATED_ASTERISK);
		int dotPos = domainName.lastIndexOf(".");
		if(dotPos >= 0){
			result.append(domainName.substring(dotPos));
		}
		return result.toString();
	}

}
