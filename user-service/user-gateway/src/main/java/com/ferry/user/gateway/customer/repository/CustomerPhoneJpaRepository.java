package com.ferry.user.gateway.customer.repository;

import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerPhoneJpaRepository extends JpaRepository<CustomerPhoneJpaEntity, String>{

	@Query("select p " +
			"from CustomerPhoneJpaEntity p " +
			"where " +
			"(:#{#filter?.customerId} is null or p.customerId = :#{#filter?.customerId}) AND " +
			"(coalesce(:#{#filter?.customerIds}, null) is null or p.customerId IN :#{#filter?.customerIds}) " +
			"and p.deleted IS FALSE " +
			"order by p.id")
	List<CustomerPhoneJpaEntity> findAllWithFilter(@Param("filter") CustomerPhoneFilter filter);

	@Modifying
	@Query("update CustomerPhoneJpaEntity p set p.deleted = true, p.updatedBy = :updatedBy, " +
			"p.updatedAt = CURRENT_TIMESTAMP " +
			"where p.customerId = :customerId and p.deleted is false")
	void softDeleteByCustomerId(@Param("customerId") String customerId, @Param("updatedBy") String updatedBy);

}
