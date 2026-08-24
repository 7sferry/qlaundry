package com.ferry.user.gateway.customer.repository;

import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.gateway.customer.entity.CustomerAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerAddressJpaRepository extends JpaRepository<CustomerAddressJpaEntity, String>{

	@Query("select a " +
			"from CustomerAddressJpaEntity a " +
			"where " +
			"(:#{#filter?.customerId} is null or a.customerId = :#{#filter?.customerId}) AND " +
			"(coalesce(:#{#filter?.customerIds}, null) is null or a.customerId IN :#{#filter?.customerIds}) " +
			"and a.deleted IS FALSE " +
			"order by a.id")
	List<CustomerAddressJpaEntity> findAllWithFilter(@Param("filter") CustomerAddressFilter filter);

	@Modifying
	@Query("update CustomerAddressJpaEntity a set a.deleted = true, a.updatedBy = :updatedBy, " +
			"a.updatedAt = CURRENT_TIMESTAMP " +
			"where a.customerId = :customerId and a.deleted is false")
	void softDeleteByCustomerId(@Param("customerId") String customerId, @Param("updatedBy") String updatedBy);

}
