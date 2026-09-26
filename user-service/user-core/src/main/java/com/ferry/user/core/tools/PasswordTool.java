package com.ferry.user.core.tools;

import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.common.RawPassword;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface PasswordTool{
	HashedPassword hash(RawPassword rawPasswordDomain);

	boolean matches(String rawPassword, String hashedPassword);
}
