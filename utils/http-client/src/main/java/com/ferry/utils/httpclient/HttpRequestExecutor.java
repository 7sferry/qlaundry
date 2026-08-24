package com.ferry.utils.httpclient;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface HttpRequestExecutor{

	<T> T send(Class<T> responseType);

	String traceId();

}
