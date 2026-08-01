package com.ferry.user.gateway.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tenant_statuses")
public class TenantStatusJpaEntity{
	@Id
	private short id;

	@Column(nullable = false, length = 25)
	private String name;
}
