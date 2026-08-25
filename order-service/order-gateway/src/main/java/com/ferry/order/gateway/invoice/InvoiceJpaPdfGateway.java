package com.ferry.order.gateway.invoice;

import com.ferry.order.core.invoice.pdf.InvoicePdfGateway;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.entity.OrderItemJpaEntity;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class InvoiceJpaPdfGateway implements InvoicePdfGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<OrderDomain> findById(OrderIdDomain orderId, TenantIdDomain tenantId){
		return orderJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(orderId.value(), tenantId.value())
				.map(entity -> OrderJpaEntity.construct(entity, cryptoTool));
	}

	@Override
	public List<OrderItemDomain> findItemsByOrderId(OrderIdDomain orderId){
		return orderItemJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderItemJpaEntity::construct)
				.toList();
	}

}
