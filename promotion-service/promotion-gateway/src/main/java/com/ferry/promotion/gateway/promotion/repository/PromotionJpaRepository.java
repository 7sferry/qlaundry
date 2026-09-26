package com.ferry.promotion.gateway.promotion.repository;

import com.ferry.promotion.domain.promotion.PromotionFilter;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionJpaRepository extends JpaRepository<PromotionJpa, String>{

	Optional<PromotionJpa> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	Optional<PromotionJpa> findByIdAndTenantId(String id, String tenantId);

	Optional<PromotionJpa> findByCodeAndTenantId(String code, String tenantId);

	boolean existsByCodeAndTenantId(String code, String tenantId);

	boolean existsByCodeAndTenantIdAndIdNot(String code, String tenantId, String id);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update PromotionJpa p " +
			"set p.usedCount = p.usedCount + 1, p.updatedBy = :updatedBy, p.updatedAt = :updatedAt " +
			"where " +
			"p.id = :id AND " +
			"p.tenantId = :tenantId AND " +
			"p.active IS TRUE AND " +
			"(p.usageLimit is null or p.usedCount < p.usageLimit) AND " +
			"p.deleted IS FALSE")
	int claimUsage(@Param("id") String id, @Param("tenantId") String tenantId, @Param("updatedBy") String updatedBy,
	               @Param("updatedAt") Instant updatedAt);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update PromotionJpa p " +
			"set p.usedCount = p.usedCount - 1, p.updatedBy = :updatedBy, p.updatedAt = :updatedAt " +
			"where " +
			"p.id = :id AND " +
			"p.tenantId = :tenantId AND " +
			"p.usedCount > 0")
	int releaseUsage(@Param("id") String id, @Param("tenantId") String tenantId,
	                 @Param("updatedBy") String updatedBy, @Param("updatedAt") Instant updatedAt);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.id} is null or p.id > :#{#filter?.cursor?.id}) AND " +
			"p.deleted IS FALSE " +
			"order by p.id asc")
	List<PromotionJpa> findAfterById(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.id} is null or p.id < :#{#filter?.cursor?.id}) AND " +
			"p.deleted IS FALSE " +
			"order by p.id desc")
	List<PromotionJpa> findBeforeById(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or p.name > :#{#filter?.cursor?.sortValue} or " +
			"  (p.name = :#{#filter?.cursor?.sortValue} and p.id > :#{#filter?.cursor?.id})) AND " +
			"p.deleted IS FALSE " +
			"order by p.name asc, p.id asc")
	List<PromotionJpa> findAfterByName(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or p.name < :#{#filter?.cursor?.sortValue} or " +
			"  (p.name = :#{#filter?.cursor?.sortValue} and p.id < :#{#filter?.cursor?.id})) AND " +
			"p.deleted IS FALSE " +
			"order by p.name desc, p.id desc")
	List<PromotionJpa> findBeforeByName(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or p.code > :#{#filter?.cursor?.sortValue} or " +
			"  (p.code = :#{#filter?.cursor?.sortValue} and p.id > :#{#filter?.cursor?.id})) AND " +
			"p.deleted IS FALSE " +
			"order by p.code asc, p.id asc")
	List<PromotionJpa> findAfterByCode(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or p.code < :#{#filter?.cursor?.sortValue} or " +
			"  (p.code = :#{#filter?.cursor?.sortValue} and p.id < :#{#filter?.cursor?.id})) AND " +
			"p.deleted IS FALSE " +
			"order by p.code desc, p.id desc")
	List<PromotionJpa> findBeforeByCode(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or p.endAt > :#{#filter?.cursorEndAt()} or " +
			"  (p.endAt = :#{#filter?.cursorEndAt()} and p.id > :#{#filter?.cursor?.id})) AND " +
			"p.deleted IS FALSE " +
			"order by p.endAt asc, p.id asc")
	List<PromotionJpa> findAfterByEndAt(@Param("filter") PromotionFilter filter, Pageable pageable);

	@Query("select p " +
			"from PromotionJpa p " +
			"where " +
			"(:#{#filter?.codeStartsWith()} is null or p.code like :#{#filter?.codeStartsWith()}) AND " +
			"(:#{#filter?.nameStartsWith()} is null or lower(p.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or p.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or p.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or p.active = true) AND " +
			"(:#{#filter?.currentOnly} = false or (p.startAt <= CURRENT_TIMESTAMP and p.endAt > CURRENT_TIMESTAMP)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or p.endAt < :#{#filter?.cursorEndAt()} or " +
			"  (p.endAt = :#{#filter?.cursorEndAt()} and p.id < :#{#filter?.cursor?.id})) AND " +
			"p.deleted IS FALSE " +
			"order by p.endAt desc, p.id desc")
	List<PromotionJpa> findBeforeByEndAt(@Param("filter") PromotionFilter filter, Pageable pageable);

}
