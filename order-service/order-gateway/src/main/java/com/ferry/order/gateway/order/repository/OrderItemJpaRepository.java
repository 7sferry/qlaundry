package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, String>{

	List<OrderItemJpaEntity> findByOrderIdAndDeletedIsFalseOrderById(String orderId);

	List<OrderItemJpaEntity> findByOrderIdInAndDeletedIsFalseOrderById(Collection<String> orderIds);

}
