package com.ferry.user.gateway.tenant.entity;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.ZoneIdConverter;

import java.time.Instant;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tenants")
public class TenantJpa{
	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(unique = true, length = 50)
	private String username;
	@Column(nullable = false, length = 100)
	private String fullName;
	@Column
	private String description;
	@Column(nullable = false, length = 50)
	private ZoneId timeZone;
	@Column(nullable = false, columnDefinition = "SMALLINT DEFAULT 1")
	private short statusId;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	public static Tenant construct(TenantJpa saved){
		return new Tenant(saved.id, new Username(saved.username), new FullName(saved.fullName),
				new Description(saved.description), saved.timeZone, TenantStatus.findByValue(saved.statusId).orElseThrow(),
				saved.version, saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

	public static TenantJpa construct(String id, Tenant tenant, TenantStatus status){
		TenantJpa entity = new TenantJpa();
		entity.id = id;
		entity.createdAt = tenant.createdAt();
		entity.updatedAt = tenant.updatedAt();
		entity.createdBy = entity.id;
		entity.updatedBy = entity.id;
		entity.username = tenant.usernameValue();
		entity.description = tenant.descriptionValue();
		entity.fullName = tenant.fullNameValue();
		entity.timeZone = tenant.timeZone();
		entity.statusId = status.getValue();
		entity.version = tenant.version();
		entity.deleted = tenant.deleted();
		return entity;
	}

}
