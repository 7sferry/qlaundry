package com.ferry.user.core.notification;

import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.notification.EmailTriggerType;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailTriggerConfig(Object payload, String userId, EmailTriggerType triggerType,
                                 EmailDomain recipient){
}
