package com.ferry.user.gateway.customer.repository;

import com.ferry.user.domain.customer.CustomerFilter;
import com.ferry.user.gateway.customer.entity.CustomerJpa;
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

public interface CustomerJpaRepository extends JpaRepository<CustomerJpa, String>{

	Optional<CustomerJpa> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	boolean existsByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	@Query("select c " +
			"from CustomerJpa c " +
			"where " +
			"(:#{#filter?.fullNameStartsWith()} is null or lower(c.fullName) like :#{#filter?.fullNameStartsWith()}) AND " +
			"(:#{#filter?.tenantId} is null or c.tenantId = :#{#filter?.tenantId}) AND " +
			"(:phoneHash is null or exists(select 1 from CustomerPhoneJpa p " +
			"   where p.customerId = c.id and p.phoneHash = :phoneHash and p.deleted is false)) AND " +
			"(:#{#filter?.cursor?.id} is null or c.id > :#{#filter?.cursor?.id}) AND " +
			"c.deleted IS FALSE " +
			"order by c.id asc")
	List<CustomerJpa> findAfterById(@Param("filter") CustomerFilter filter, @Param("phoneHash") String phoneHash,
	                                      Pageable pageable);

	@Query("select c " +
			"from CustomerJpa c " +
			"where " +
			"(:#{#filter?.fullNameStartsWith()} is null or lower(c.fullName) like :#{#filter?.fullNameStartsWith()}) AND " +
			"(:#{#filter?.tenantId} is null or c.tenantId = :#{#filter?.tenantId}) AND " +
			"(:phoneHash is null or exists(select 1 from CustomerPhoneJpa p " +
			"   where p.customerId = c.id and p.phoneHash = :phoneHash and p.deleted is false)) AND " +
			"(:#{#filter?.cursor?.id} is null or c.id < :#{#filter?.cursor?.id}) AND " +
			"c.deleted IS FALSE " +
			"order by c.id desc")
	List<CustomerJpa> findBeforeById(@Param("filter") CustomerFilter filter, @Param("phoneHash") String phoneHash,
	                                       Pageable pageable);

	@Query("select c " +
			"from CustomerJpa c " +
			"where " +
			"(:#{#filter?.fullNameStartsWith()} is null or lower(c.fullName) like :#{#filter?.fullNameStartsWith()}) AND " +
			"(:#{#filter?.tenantId} is null or c.tenantId = :#{#filter?.tenantId}) AND " +
			"(:phoneHash is null or exists(select 1 from CustomerPhoneJpa p " +
			"   where p.customerId = c.id and p.phoneHash = :phoneHash and p.deleted is false)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or c.fullName > :#{#filter?.cursor?.sortValue} or " +
			"  (c.fullName = :#{#filter?.cursor?.sortValue} and c.id > :#{#filter?.cursor?.id})) AND " +
			"c.deleted IS FALSE " +
			"order by c.fullName asc, c.id asc")
	List<CustomerJpa> findAfterByFullName(@Param("filter") CustomerFilter filter, @Param("phoneHash") String phoneHash,
	                                            Pageable pageable);

	@Query("select c " +
			"from CustomerJpa c " +
			"where " +
			"(:#{#filter?.fullNameStartsWith()} is null or lower(c.fullName) like :#{#filter?.fullNameStartsWith()}) AND " +
			"(:#{#filter?.tenantId} is null or c.tenantId = :#{#filter?.tenantId}) AND " +
			"(:phoneHash is null or exists(select 1 from CustomerPhoneJpa p " +
			"   where p.customerId = c.id and p.phoneHash = :phoneHash and p.deleted is false)) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or c.fullName < :#{#filter?.cursor?.sortValue} or " +
			"  (c.fullName = :#{#filter?.cursor?.sortValue} and c.id < :#{#filter?.cursor?.id})) AND " +
			"c.deleted IS FALSE " +
			"order by c.fullName desc, c.id desc")
	List<CustomerJpa> findBeforeByFullName(@Param("filter") CustomerFilter filter, @Param("phoneHash") String phoneHash,
	                                             Pageable pageable);

}
