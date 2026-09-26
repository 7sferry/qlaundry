package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderPromotionJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPromotionJpaRepository extends JpaRepository<OrderPromotionJpa, String>{

	List<OrderPromotionJpa> findByOrderIdAndDeletedIsFalseOrderById(String orderId);

	List<OrderPromotionJpa> findByOrderIdInAndDeletedIsFalseOrderById(Collection<String> orderIds);

}
