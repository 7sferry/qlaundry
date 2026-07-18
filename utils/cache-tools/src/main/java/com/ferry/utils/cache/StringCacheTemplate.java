package com.ferry.utils.cache;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StringCacheTemplate{
	void setValue(String key, String s, Duration duration);
}
