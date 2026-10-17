package com.ferry.user.core.tenant.confirmregistration;

import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.confirmregistration.FailedToConfirmTenantException;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantConfirmRegistrationUseCase implements TenantConfirmRegistrationUseCase{
	private final TenantConfirmRegistrationGateway gateway;
	private final UserCacheManager cacheManager;
	private final AuthAuditLog auditLog;

	@Override
	public void execute(TenantConfirmRegistrationRequest request, TenantConfirmRegistrationPresenter presenter){
		String subject = request.tenantId();
		try{
			request.validate();
			TenantId tenantId = new TenantId(request.tenantId());
			validateConfirmationToken(tenantId, request.token());
			Tenant tenant = gateway.findById(tenantId)
					.orElseThrow(() -> new FailedToConfirmTenantException("Invalid confirmation link"));
			if(tenant.status() == TenantStatus.ACTIVE){
				throw new FailedToConfirmTenantException("Tenant already confirmed");
			}
			gateway.save(tenant.activate());
			auditLog.record(AuthAction.TENANT_CONFIRMATION, AuthResult.SUCCESS, null, subject);
			presenter.present(new TenantConfirmRegistrationResponse("Registration confirmed. You can now sign in."));
		} catch (FailedToConfirmTenantException e){
			auditLog.record(AuthAction.TENANT_CONFIRMATION, AuthResult.FAILURE, null, subject);
			throw e;
		} catch (Exception e){
			auditLog.record(AuthAction.TENANT_CONFIRMATION, AuthResult.FAILURE, null, subject);
			throw new FailedToConfirmTenantException(e);
		}
	}

	private void validateConfirmationToken(TenantId tenantId, String token){
		String storedToken = cacheManager.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenantId.value())
				.orElseThrow(() -> new FailedToConfirmTenantException("Invalid or expired confirmation link"));
		if(!MessageDigest.isEqual(storedToken.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8))){
			throw new FailedToConfirmTenantException("Invalid or expired confirmation link");
		}
		cacheManager.delete(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + tenantId.value());
	}

}
