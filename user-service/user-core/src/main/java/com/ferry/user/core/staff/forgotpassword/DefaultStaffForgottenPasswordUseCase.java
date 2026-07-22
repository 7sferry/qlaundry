package com.ferry.user.core.staff.forgotpassword;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.EmailDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.forgottenpassword.ForgottenPasswordOtpDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffForgottenPasswordUseCase implements StaffForgottenPasswordUseCase {
	private static final String ALPHABET = "qwertyuiopasdfghjklzxcvbnm";
	private static final BigInteger ALPHABET_LENGTH = BigInteger.valueOf(ALPHABET.length());
	private static final List<String> EMAIL_DOMAIN = List.of("gmail","yahoo","mail");
	private static final int MAX_REPEATED_ASTERISK = 7;
	private static final int MIN_REPEATED_ASTERISK = 3;
	private static final BigInteger REPEATED_ASTERISK_RANGE = BigInteger.valueOf(MAX_REPEATED_ASTERISK - MIN_REPEATED_ASTERISK + 1);
	private static final char AT_SYMBOL = '@';
	private static final String TLD = ".com";

	private final StaffForgottenPasswordGateway gateway;
	private final UserEmailPublisher emailPublisher;
	private final UserCacheManager userCacheManager;

	@Override
	public void execute(StaffForgottenPasswordRequest request, StaffForgottenPasswordPresenter presenter){
		UsernameDomain username = new UsernameDomain(request.username());
		gateway.findEmailWithUsername(username).ifPresentOrElse(email -> {
			String otp = String.format("%06d", PasswordConstant.GENERATOR.nextInt(1, 1000000));
			userCacheManager.set(PasswordConstant.OTP_KEY + username.value(), otp,
					PasswordConstant.OTP_DURATION_MINUTES);
			ForgottenPasswordOtpDomain payload = new ForgottenPasswordOtpDomain(request.username(), otp);
			EmailDomain recipient = new EmailDomain(email.email());
			EmailTriggerConfig config = new EmailTriggerConfig(payload, email.staffId(),
					EmailTriggerType.FORGOTTEN_PASSWORD, recipient);
			EmailTriggerDomain saved = emailPublisher.save(config);
			emailPublisher.publish(saved);
			String maskedEmail = maskEmail(email.email());
			presenter.present(new StaffForgottenPasswordResponse(maskedEmail));
		}, () -> {
			String maskedFakeEmail = maskFakeEmail(username.value());
			presenter.present(new StaffForgottenPasswordResponse(maskedFakeEmail));
		});
	}

	private String maskFakeEmail(String username) {
		byte[] hash = hash(username);
		BigInteger hashedInt = new BigInteger(1, hash);

		int firstIndex = hashedInt.mod(ALPHABET_LENGTH).intValue();
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);

		int lastIndex = hashedInt.mod(ALPHABET_LENGTH).intValue();
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);

		int domainIndex = hashedInt.mod(BigInteger.valueOf(EMAIL_DOMAIN.size())).intValue();
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);

		int repeatedLocalNameAsterisk = hashedInt.mod(REPEATED_ASTERISK_RANGE).intValue();
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);

		int repeatedDomainAsterisk = hashedInt.mod(REPEATED_ASTERISK_RANGE).intValue();

		return ALPHABET.charAt(firstIndex) +
				"*".repeat(MIN_REPEATED_ASTERISK + repeatedLocalNameAsterisk) +
				ALPHABET.charAt(lastIndex) +
				AT_SYMBOL +
				EMAIL_DOMAIN.get(domainIndex).charAt(0) +
				"*".repeat(MIN_REPEATED_ASTERISK + repeatedDomainAsterisk) +
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
		byte[] hash = hash(email);
		BigInteger hashedInt = new BigInteger(1, hash);
		int repeatedLocalNameAsterisk = hashedInt.mod(REPEATED_ASTERISK_RANGE).intValue();
		result.repeat("*", MIN_REPEATED_ASTERISK + repeatedLocalNameAsterisk);
		if(localName.length() > 1){
			result.append(localName.charAt(localName.length() - 1));
		}
		result.append(AT_SYMBOL);
		if(!domainName.isEmpty()){
			result.append(domainName.charAt(0));
		}
		hashedInt = hashedInt.divide(ALPHABET_LENGTH);
		int repeatedDomainAsterisk = hashedInt.mod(REPEATED_ASTERISK_RANGE).intValue();
		result.repeat("*", MIN_REPEATED_ASTERISK + repeatedDomainAsterisk);
		int dotPos = domainName.lastIndexOf(".");
		if(dotPos >= 0){
			result.append(domainName.substring(dotPos));
		}
		return result.toString();
	}

}
