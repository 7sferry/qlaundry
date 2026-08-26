package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderPromotionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPromotionJpaRepository extends JpaRepository<OrderPromotionJpaEntity, String>{

	List<OrderPromotionJpaEntity> findByOrderIdAndDeletedIsFalseOrderById(String orderId);

	List<OrderPromotionJpaEntity> findByOrderIdInAndDeletedIsFalseOrderById(Collection<String> orderIds);

}
