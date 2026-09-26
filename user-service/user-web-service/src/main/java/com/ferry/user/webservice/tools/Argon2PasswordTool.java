package com.ferry.user.webservice.tools;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.common.RawPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class Argon2PasswordTool implements PasswordTool{
	private final Argon2Password4jPasswordEncoder encoder;

	@Override
	public HashedPassword hash(RawPassword rawPasswordDomain){
		return new HashedPassword(encoder.encode(rawPasswordDomain.value()));
	}

	@Override
	public boolean matches(String rawPassword, String hashedPassword){
		return encoder.matches(rawPassword, hashedPassword);
	}
}
