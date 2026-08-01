package com.ferry.user.webservice.tenant.expiration;

import com.ferry.user.core.tenant.expiration.TenantExpirationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class TenantExpirationScheduler{
	private final TenantExpirationUseCase tenantExpirationUseCase;

	@Scheduled(cron = "0 0 3 * * *")
	public void expirePendingTenants(){
		int expired = tenantExpirationUseCase.execute();
		if(expired > 0){
			log.info("Expired {} tenant(s) still pending confirmation after 30 days", expired);
		}
	}

}
