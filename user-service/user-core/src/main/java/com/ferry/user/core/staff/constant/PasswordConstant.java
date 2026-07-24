package com.ferry.user.core.staff.constant;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class PasswordConstant{
	public static final String OTP_KEY = "forgottenPassword:otp:";
	public static final String RESET_TOKEN_KEY = "forgottenPassword:resetToken:";
	public static final Duration OTP_DURATION = Duration.ofMinutes(4);
	public static final Duration RESET_TOKEN_DURATION = Duration.ofMinutes(5);

	public static Random getRandom() {
		return RandomHolder.GENERATOR;
	}

	static final class RandomHolder{
		public static final SecureRandom GENERATOR;
		static{
			SecureRandom random;
			try{
				random = SecureRandom.getInstanceStrong();
			} catch(Exception e){
				random = new SecureRandom();
			}
			GENERATOR = random;
		}
	}

}
