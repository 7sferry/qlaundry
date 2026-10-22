package com.ferry.user.core.tools;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface ResponseTimeFloor{
	void enforce(long startedAtNanos, Duration minimum);
}
