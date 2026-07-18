package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.login.StaffLoginGateway;
import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.gateway.session.entity.UserSessionJpaEntity;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpaEntity;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionTypeJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.cache.StringCacheTemplate;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffLoginJpaGateway implements StaffLoginGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final UserSessionJpaRepository userSessionJpaRepository;
	private final UserSessionTypeJpaRepository userSessionTypeJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final StringCacheTemplate cacheTemplate;
	private final JsonManager jsonManager;

	@Override
	public Optional<StaffLoginProjection> findByUsername(UsernameDomain username){
		return staffJpaRepository.findByUsername(username.value(), StaffLoginProjection.class);
	}

	@Override
	public UserSessionDomain save(UserSessionDomain userSession){
		UserSessionJpaEntity entity = new UserSessionJpaEntity();
		entity.setId(userSession.id());
		entity.setCreatedAt(userSession.createdAt());
		entity.setExpirationTime(userSession.expirationTime());
		entity.setUserId(userSession.userId());
		UserSessionTypeJpaEntity sessionType = userSessionTypeJpaRepository.findById(userSession.sessionType().getValue()).orElse(null);
		entity.setSessionType(sessionType);
		entity.setUpdatedAt(userSession.updatedAt());
		UserSessionJpaEntity saved = userSessionJpaRepository.save(entity);
		return new UserSessionDomain(saved.getId(), saved.getExpirationTime(), saved.getUserId(),
				userSession.sessionType(), saved.getVersion(), saved.getCreatedAt(),
				saved.getUpdatedAt());
	}

	@Override
	public Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId){
		return tenantJpaRepository.findById(tenantId.value(), TenantLoginProjection.class);
	}

	@Override
	public void cache(String key, Object value, Duration duration){
		cacheTemplate.setValue(key, jsonManager.writeValueAsString(value), duration);
	}
}
