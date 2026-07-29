package com.ferry.user.webservice.tools;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.RawPasswordDomain;
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
	public HashedPasswordDomain hash(RawPasswordDomain rawPasswordDomain){
		return new HashedPasswordDomain(encoder.encode(rawPasswordDomain.value()));
	}

	@Override
	public boolean matches(String rawPassword, String hashedPassword){
		return encoder.matches(rawPassword, hashedPassword);
	}
}
