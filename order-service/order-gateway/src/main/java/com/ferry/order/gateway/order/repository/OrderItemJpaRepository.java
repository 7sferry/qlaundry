package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderItemJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpa, String>{

	List<OrderItemJpa> findByOrderIdAndDeletedIsFalseOrderById(String orderId);

	List<OrderItemJpa> findByOrderIdInAndDeletedIsFalseOrderById(Collection<String> orderIds);

}
