package com.ferry.user.gateway.customer.repository;

import com.ferry.user.domain.customer.CustomerFilter;
import com.ferry.user.gateway.customer.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, String>{

	Optional<CustomerJpaEntity> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	boolean existsByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	@Query("select c " +
			"from CustomerJpaEntity c " +
			"where " +
			"(:#{#filter?.fullNameStartsWith()} is null or lower(c.fullName) like :#{#filter?.fullNameStartsWith()}) AND " +
			"(:#{#filter?.tenantId} is null or c.tenantId = :#{#filter?.tenantId}) AND " +
			"(:phoneHash is null or exists(select 1 from CustomerPhoneJpaEntity p " +
			"   where p.customerId = c.id and p.phoneHash = :phoneHash and p.deleted is false)) AND " +
			"c.deleted IS FALSE " +
			"order by c.fullName")
	List<CustomerJpaEntity> findAllWithFilter(@Param("filter") CustomerFilter filter,
	                                          @Param("phoneHash") String phoneHash);

}
