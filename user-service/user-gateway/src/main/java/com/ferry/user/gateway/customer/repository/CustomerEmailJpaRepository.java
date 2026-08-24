package com.ferry.user.gateway.customer.repository;

import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerEmailJpaRepository extends JpaRepository<CustomerEmailJpaEntity, String>{

	@Query("select e " +
			"from CustomerEmailJpaEntity e " +
			"where " +
			"(:#{#filter?.customerId} is null or e.customerId = :#{#filter?.customerId}) AND " +
			"(coalesce(:#{#filter?.customerIds}, null) is null or e.customerId IN :#{#filter?.customerIds}) " +
			"and e.deleted IS FALSE " +
			"order by e.id")
	List<CustomerEmailJpaEntity> findAllWithFilter(@Param("filter") CustomerEmailFilter filter);

	@Modifying
	@Query("update CustomerEmailJpaEntity e set e.deleted = true, e.updatedBy = :updatedBy, " +
			"e.updatedAt = CURRENT_TIMESTAMP " +
			"where e.customerId = :customerId and e.deleted is false")
	void softDeleteByCustomerId(@Param("customerId") String customerId, @Param("updatedBy") String updatedBy);

}
