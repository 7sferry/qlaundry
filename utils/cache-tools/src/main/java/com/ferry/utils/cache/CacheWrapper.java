package com.ferry.utils.cache;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record CacheWrapper<T>(T data, long expiresInSeconds) {
}
