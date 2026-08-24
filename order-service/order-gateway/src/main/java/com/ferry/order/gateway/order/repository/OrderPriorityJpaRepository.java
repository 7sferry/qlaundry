package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderPriorityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPriorityJpaRepository extends JpaRepository<OrderPriorityJpaEntity, Short>{
}
