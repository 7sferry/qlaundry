package com.ferry.order.gateway.service.repository;

import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceJpaRepository extends JpaRepository<LaundryServiceJpaEntity, String>{

	Optional<LaundryServiceJpaEntity> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	boolean existsByNameIgnoreCaseAndTenantIdAndDeletedIsFalse(String name, String tenantId);

	@Query("select s " +
			"from LaundryServiceJpaEntity s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"s.deleted IS FALSE " +
			"order by s.name")
	List<LaundryServiceJpaEntity> findAllWithFilter(@Param("filter") LaundryServiceFilter filter);

}
