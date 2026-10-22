package com.ferry.user.webservice.tools;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.common.RawPassword;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;

import java.util.UUID;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class Argon2PasswordTool implements PasswordTool{
	private final Argon2Password4jPasswordEncoder encoder;
	private final String dummyHash;

	public Argon2PasswordTool(Argon2Password4jPasswordEncoder encoder){
		this.encoder = encoder;
		this.dummyHash = encoder.encode(UUID.randomUUID().toString());
	}

	@Override
	public HashedPassword hash(RawPassword rawPasswordDomain){
		return new HashedPassword(encoder.encode(rawPasswordDomain.value()));
	}

	@Override
	public boolean matches(String rawPassword, String hashedPassword){
		return encoder.matches(rawPassword, hashedPassword);
	}

	@Override
	public void simulateMatch(String rawPassword){
		encoder.matches(rawPassword == null ? "" : rawPassword, dummyHash);
	}
}
