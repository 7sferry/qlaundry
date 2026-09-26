package com.ferry.promotion.gateway.promotion.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "promotion_types")
public class PromotionTypeJpa{
	@Id
	private short id;

	@Column(nullable = false, length = 25)
	private String name;

	@JoinColumn(name = "typeId")
	@OneToMany(fetch = FetchType.LAZY)
	private Set<PromotionJpa> promotions;
}
