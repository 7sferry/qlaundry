package com.ferry.user.webservice.audit;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class Slf4jAuthAuditLog implements AuthAuditLog{
	public static final String LOGGER_NAME = "AUTH_AUDIT";
	private static final String UNKNOWN = "-";
	private static final int MAX_FIELD_LENGTH = 100;
	private static final Logger LOG = LoggerFactory.getLogger(LOGGER_NAME);

	@Override
	public void record(AuthAction action, AuthResult result, String userId, String subject){
		LOG.info("action={} result={} userId={} subject={} ip={}", action, result, clean(userId), clean(subject),
				clean(clientIp()));
	}

	private String clientIp(){
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if(attributes instanceof ServletRequestAttributes servletAttributes){
			return servletAttributes.getRequest().getRemoteAddr();
		}
		return null;
	}

	private String clean(String value){
		if(value == null || value.isBlank()){
			return UNKNOWN;
		}
		String cleaned = value.replaceAll("[\\p{Cntrl}\\s]+", "_");
		return cleaned.length() > MAX_FIELD_LENGTH ? cleaned.substring(0, MAX_FIELD_LENGTH) : cleaned;
	}
}
