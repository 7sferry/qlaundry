package com.ferry.user.core.tools;

import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.RawPasswordDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface PasswordTool{
	HashedPasswordDomain hash(RawPasswordDomain rawPasswordDomain);

	boolean matches(String rawPassword, String hashedPassword);
}
