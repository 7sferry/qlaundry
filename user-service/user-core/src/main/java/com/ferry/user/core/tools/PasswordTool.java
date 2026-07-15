package com.ferry.user.core.tools;

import com.ferry.user.domain.HashedPasswordDomain;
import com.ferry.user.domain.RawPasswordDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface PasswordTool{
	HashedPasswordDomain hash(RawPasswordDomain rawPasswordDomain);

	boolean matches(RawPasswordDomain input, RawPasswordDomain stored);
}
