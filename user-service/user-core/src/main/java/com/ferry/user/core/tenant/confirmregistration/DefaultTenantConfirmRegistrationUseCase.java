package com.ferry.user.core.tenant.confirmregistration;

import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.confirmregistration.FailedToConfirmTenantException;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantConfirmRegistrationUseCase implements TenantConfirmRegistrationUseCase{
	private final TenantConfirmRegistrationGateway gateway;
	private final UserCacheManager cacheManager;

	@Override
	public void execute(TenantConfirmRegistrationRequest request, TenantConfirmRegistrationPresenter presenter){
		try{
			request.validate();
			TenantIdDomain tenantId = new TenantIdDomain(request.tenantId());
			validateConfirmationToken(tenantId, request.token());
			TenantDomain tenant = gateway.findById(tenantId)
					.orElseThrow(() -> new FailedToConfirmTenantException("Invalid confirmation link"));
			if(tenant.status() == TenantStatus.ACTIVE){
				throw new FailedToConfirmTenantException("Tenant already confirmed");
			}
			gateway.save(tenant.activate());
			presenter.present(new TenantConfirmRegistrationResponse("Registration confirmed. You can now sign in."));
		} catch (FailedToConfirmTenantException e){
			throw e;
		} catch (Exception e){
			throw new FailedToConfirmTenantException(e);
		}
	}

	private void validateConfirmationToken(TenantIdDomain tenantId, String token){
		String storedToken = cacheManager.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenantId.value())
				.orElseThrow(() -> new FailedToConfirmTenantException("Invalid or expired confirmation link"));
		if(!MessageDigest.isEqual(storedToken.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8))){
			throw new FailedToConfirmTenantException("Invalid or expired confirmation link");
		}
		cacheManager.delete(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenantId.value());
	}

}
