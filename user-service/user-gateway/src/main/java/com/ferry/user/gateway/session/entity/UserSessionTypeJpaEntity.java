package com.ferry.user.gateway.session.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "user_session_types")
@Entity
public class UserSessionTypeJpaEntity{
	@Id
	private short id;
	@Column(nullable = false, length = 25)
	private String name;
}
