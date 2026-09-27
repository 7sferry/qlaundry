package com.ferry.user.gateway.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tenant_statuses")
public class TenantStatusJpa{
	@Id
	private short id;

	@Column(nullable = false, length = 25)
	private String name;

	@JoinColumn(name = "statusId")
	@OneToMany(fetch = FetchType.LAZY)
	private Set<TenantJpa> tenants;
}
