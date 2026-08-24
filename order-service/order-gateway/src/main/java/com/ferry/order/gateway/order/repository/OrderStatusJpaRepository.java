package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderStatusJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderStatusJpaRepository extends JpaRepository<OrderStatusJpaEntity, Short>{
}
