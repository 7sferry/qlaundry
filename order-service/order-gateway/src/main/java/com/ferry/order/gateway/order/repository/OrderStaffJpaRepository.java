package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderStaffJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderStaffJpaRepository extends JpaRepository<OrderStaffJpa, String>{

	List<OrderStaffJpa> findByOrderIdAndDeletedIsFalseOrderById(String orderId);

	List<OrderStaffJpa> findByOrderIdOrderById(String orderId);

}
