package com.ferry.order.gateway.service.repository;

import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
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

public interface LaundryServiceJpaRepository extends JpaRepository<LaundryServiceJpa, String>{

	Optional<LaundryServiceJpa> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	@Query("select s " +
			"from LaundryServiceJpa s " +
			"where " +
			"(:tenantId is null or s.tenantId = :tenantId) AND " +
			"(:afterId is null or s.id > :afterId) " +
			"order by s.id asc")
	List<LaundryServiceJpa> findBackfillPage(@Param("tenantId") String tenantId, @Param("afterId") String afterId,
	                                               Pageable pageable);

	boolean existsByNameIgnoreCaseAndTenantIdAndDeletedIsFalse(String name, String tenantId);

	@Query("select s " +
			"from LaundryServiceJpa s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.id} is null or s.id > :#{#filter?.cursor?.id}) AND " +
			"s.deleted IS FALSE " +
			"order by s.id asc")
	List<LaundryServiceJpa> findAfterById(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

	@Query("select s " +
			"from LaundryServiceJpa s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.id} is null or s.id < :#{#filter?.cursor?.id}) AND " +
			"s.deleted IS FALSE " +
			"order by s.id desc")
	List<LaundryServiceJpa> findBeforeById(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

	@Query("select s " +
			"from LaundryServiceJpa s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or s.name > :#{#filter?.cursor?.sortValue} or " +
			"  (s.name = :#{#filter?.cursor?.sortValue} and s.id > :#{#filter?.cursor?.id})) AND " +
			"s.deleted IS FALSE " +
			"order by s.name asc, s.id asc")
	List<LaundryServiceJpa> findAfterByName(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

	@Query("select s " +
			"from LaundryServiceJpa s " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(s.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.categoryValue()} is null or s.categoryId = :#{#filter?.categoryValue()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or s.active = true) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or s.name < :#{#filter?.cursor?.sortValue} or " +
			"  (s.name = :#{#filter?.cursor?.sortValue} and s.id < :#{#filter?.cursor?.id})) AND " +
			"s.deleted IS FALSE " +
			"order by s.name desc, s.id desc")
	List<LaundryServiceJpa> findBeforeByName(@Param("filter") LaundryServiceFilter filter, Pageable pageable);

}
