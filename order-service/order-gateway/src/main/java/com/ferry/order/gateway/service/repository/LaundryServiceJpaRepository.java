package com.ferry.order.gateway.service.repository;

import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import org.springframework.data.domain.Pageable;
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
			"(:#{#filter?.cursor?.id} is null or s.id > :#{#filter?.cursor?.id}) AND " +
			"s.deleted IS FALSE " +
			"order by s.id asc")
	List<LaundryServiceJpaEntity> findAfterById(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

	@Query("select s " +
			"from LaundryServiceJpaEntity s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.id} is null or s.id < :#{#filter?.cursor?.id}) AND " +
			"s.deleted IS FALSE " +
			"order by s.id desc")
	List<LaundryServiceJpaEntity> findBeforeById(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

	@Query("select s " +
			"from LaundryServiceJpaEntity s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or s.name > :#{#filter?.cursor?.sortValue} or " +
			"  (s.name = :#{#filter?.cursor?.sortValue} and s.id > :#{#filter?.cursor?.id})) AND " +
			"s.deleted IS FALSE " +
			"order by s.name asc, s.id asc")
	List<LaundryServiceJpaEntity> findAfterByName(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

	@Query("select s " +
			"from LaundryServiceJpaEntity s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or s.name < :#{#filter?.cursor?.sortValue} or " +
			"  (s.name = :#{#filter?.cursor?.sortValue} and s.id < :#{#filter?.cursor?.id})) AND " +
			"s.deleted IS FALSE " +
			"order by s.name desc, s.id desc")
	List<LaundryServiceJpaEntity> findBeforeByName(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

}
