package com.ferry.user.domain.token;

import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.HashedPasswordDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.session.SessionType;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record UserTokenDomain(UsernameDomain username, FullNameDomain fullName, FullNameDomain tenantName,
                              SessionType sessionType){
}
