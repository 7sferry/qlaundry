package com.ferry.user.webservice.tools;

import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tools.ResponseTimeFloor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class RequestResponseTimeFloor implements ResponseTimeFloor{
	public static final String DEADLINE_ATTRIBUTE = RequestResponseTimeFloor.class.getName() + ".deadline";
	private static final long MAX_JITTER_NANOS = Duration.ofMillis(100).toNanos();

	@Override
	public void enforce(long startedAtNanos, Duration minimum){
		long deadline = startedAtNanos + minimum.toNanos() + PasswordConstant.getRandom().nextLong(MAX_JITTER_NANOS);
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if(attributes == null){
			awaitUntil(deadline);
			return;
		}
		if(attributes.getAttribute(DEADLINE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) instanceof Long existing
				&& existing - deadline > 0){
			return;
		}
		attributes.setAttribute(DEADLINE_ATTRIBUTE, deadline, RequestAttributes.SCOPE_REQUEST);
	}

	public static void awaitUntil(long deadlineNanos){
		long remaining = deadlineNanos - System.nanoTime();
		if(remaining <= 0){
			return;
		}
		try{
			TimeUnit.NANOSECONDS.sleep(remaining);
		} catch (InterruptedException e){
			Thread.currentThread().interrupt();
		}
	}
}
