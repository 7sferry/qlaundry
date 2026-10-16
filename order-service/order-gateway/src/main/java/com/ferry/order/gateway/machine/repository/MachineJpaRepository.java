package com.ferry.order.gateway.machine.repository;

import com.ferry.order.domain.machine.MachineFilter;
import com.ferry.order.gateway.machine.entity.MachineJpa;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface MachineJpaRepository extends JpaRepository<MachineJpa, String>{

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select m " +
			"from MachineJpa m " +
			"where m.id = :id and m.tenantId = :tenantId and m.deleted IS FALSE")
	Optional<MachineJpa> lockByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select m " +
			"from MachineJpa m " +
			"where m.id in :ids and m.tenantId = :tenantId and m.deleted IS FALSE " +
			"order by m.id asc")
	List<MachineJpa> lockByIdsAndTenantId(@Param("ids") Collection<String> ids, @Param("tenantId") String tenantId);

	boolean existsByNameIgnoreCaseAndTenantIdAndDeletedIsFalse(String name, String tenantId);

	boolean existsByNameIgnoreCaseAndTenantIdAndIdNotAndDeletedIsFalse(String name, String tenantId, String id);

	@Query("select m " +
			"from MachineJpa m " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(m.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or m.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or m.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or m.active = true) AND " +
			"(:#{#filter?.cursor?.id} is null or m.id > :#{#filter?.cursor?.id}) AND " +
			"m.deleted IS FALSE " +
			"order by m.id asc")
	List<MachineJpa> findAfterById(@Param("filter") MachineFilter filter, Pageable pageable);

	@Query("select m " +
			"from MachineJpa m " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(m.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or m.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or m.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or m.active = true) AND " +
			"(:#{#filter?.cursor?.id} is null or m.id < :#{#filter?.cursor?.id}) AND " +
			"m.deleted IS FALSE " +
			"order by m.id desc")
	List<MachineJpa> findBeforeById(@Param("filter") MachineFilter filter, Pageable pageable);

	@Query("select m " +
			"from MachineJpa m " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(m.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or m.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or m.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or m.active = true) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or m.name > :#{#filter?.cursor?.sortValue} or " +
			"  (m.name = :#{#filter?.cursor?.sortValue} and m.id > :#{#filter?.cursor?.id})) AND " +
			"m.deleted IS FALSE " +
			"order by m.name asc, m.id asc")
	List<MachineJpa> findAfterByName(@Param("filter") MachineFilter filter, Pageable pageable);

	@Query("select m " +
			"from MachineJpa m " +
			"where " +
			"(:#{#filter?.nameStartsWith()} is null or lower(m.name) like :#{#filter?.nameStartsWith()}) AND " +
			"(:#{#filter?.typeValue()} is null or m.typeId = :#{#filter?.typeValue()}) AND " +
			"(:#{#filter?.tenantId} is null or m.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.activeOnly} = false or m.active = true) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or m.name < :#{#filter?.cursor?.sortValue} or " +
			"  (m.name = :#{#filter?.cursor?.sortValue} and m.id < :#{#filter?.cursor?.id})) AND " +
			"m.deleted IS FALSE " +
			"order by m.name desc, m.id desc")
	List<MachineJpa> findBeforeByName(@Param("filter") MachineFilter filter, Pageable pageable);

}
