package com.ferry.user.gateway.staff.entity;

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
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "staff_roles")
public class StaffRoleJpa{
	@Id
	private short id;

	@Column(nullable = false, length = 25)
	private String name;

	@JoinColumn(name = "roleId")
	@OneToMany(fetch = FetchType.LAZY)
	private Set<StaffJpa> staffs;
}
