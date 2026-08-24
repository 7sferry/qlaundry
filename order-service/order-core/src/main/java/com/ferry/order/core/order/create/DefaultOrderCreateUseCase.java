package com.ferry.order.core.order.create;

import com.ferry.order.domain.common.AddressLineDomain;
import com.ferry.order.domain.common.EmailDomain;
import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.UnsupportedPaymentMethodException;
import com.ferry.order.domain.customer.CustomerIdDomain;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderCreateUseCase implements OrderCreateUseCase{
	private final OrderCreateGateway gateway;
	private final CustomerGateway customerGateway;

	@Override
	public void execute(OrderCreateRequest request, OrderAuthPrincipal principal, OrderCreatePresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		String customerId = verifiedCustomerId(request.customerId(), tenantId);
		LaundryServiceIdDomain serviceId = new LaundryServiceIdDomain(request.serviceId());
		LaundryServiceDomain service = gateway.findServiceById(serviceId, tenantId)
				.orElseThrow(() -> new NotFoundException("Service Not Found"));
		if(!service.active()){
			throw new IllegalArgumentException("Service is no longer available");
		}
		OrderPriority priority = request.priority() == null ? OrderPriority.NORMAL : request.priority();
		PaymentMethod paymentMethod = resolvePaymentMethod(request.paymentMethod());
		Instant pickupAt = request.pickupAt() == null ? Instant.now() : Instant.ofEpochMilli(request.pickupAt());
		Instant estimatedDeliveryAt = request.estimatedDeliveryAt() == null
				? null : Instant.ofEpochMilli(request.estimatedDeliveryAt());
		EmailDomain customerEmail = request.customerEmail() == null || request.customerEmail().isBlank()
				? null : new EmailDomain(request.customerEmail());
		AddressLineDomain customerAddress = request.customerAddress() == null || request.customerAddress().isBlank()
				? null : new AddressLineDomain(request.customerAddress());
		MoneyDomain discount = request.discount() == null ? MoneyDomain.ZERO : new MoneyDomain(request.discount());
		OrderDomain saved = gateway.save(OrderDomain.create(tenantId.value(), customerId,
				new FullNameDomain(request.customerName()), new PhoneDomain(request.customerPhone()), customerEmail,
				customerAddress, service, request.quantity(), request.weightKg(), discount, priority, paymentMethod,
				pickupAt, estimatedDeliveryAt, new NoteDomain(request.notes()), principal.userId()));
		List<OrderItemDomain> items = saveItems(request, saved, principal);
		presenter.present(new OrderCreateResponse(saved, items));
	}

	private String verifiedCustomerId(String customerId, TenantIdDomain tenantId){
		if(customerId == null || customerId.isBlank()){
			return null;
		}
		CustomerIdDomain customer = new CustomerIdDomain(customerId);
		CustomerVerificationHttpRequest verification = new CustomerVerificationHttpRequest(customer.value(),
				tenantId.value());
		if(!customerGateway.belongsToTenant(verification)){
			throw new NotFoundException("Customer Not Found");
		}
		return customer.value();
	}

	private PaymentMethod resolvePaymentMethod(PaymentMethod paymentMethod){
		if(paymentMethod == null){
			return PaymentMethod.CASH;
		}
		if(paymentMethod != PaymentMethod.CASH){
			throw new UnsupportedPaymentMethodException("Only cash payment is supported for now");
		}
		return paymentMethod;
	}

	private List<OrderItemDomain> saveItems(OrderCreateRequest request, OrderDomain order,
	                                        OrderAuthPrincipal principal){
		List<OrderCreateRequest.Item> requestItems = request.items() == null ? List.of() : request.items();
		List<OrderItemDomain> items = new ArrayList<>(requestItems.size());
		for(OrderCreateRequest.Item item : requestItems){
			items.add(gateway.save(OrderItemDomain.register(order.id(), item.type(), item.label(), item.quantity(),
					principal.userId())));
		}
		return items;
	}

}
