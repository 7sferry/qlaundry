package com.ferry.order.core.order.create;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.AddressLineDomain;
import com.ferry.order.domain.common.EmailDomain;
import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.UnsupportedPaymentMethodException;
import com.ferry.order.domain.customer.CustomerIdDomain;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderPromotionDomain;
import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultOrderCreateUseCase implements OrderCreateUseCase{
	private final OrderCreateGateway gateway;
	private final OrderCustomerGateway customerGateway;
	private final OrderPromotionGateway promotionGateway;
	private final AnalyticsEventPublisher publisher;

	@Override
	public void execute(OrderCreateRequest request, OrderAuthPrincipal principal, OrderCreatePresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		String customerId = verifiedCustomerId(request.customerId(), tenantId);
		LaundryServiceIdDomain serviceId = new LaundryServiceIdDomain(request.serviceId());
		LaundryServiceDomain service = gateway.findServiceById(serviceId, tenantId)
				.orElseThrow(() -> new NotFoundException("Service Not Found"));
		if(!service.active()){
			throw new InvalidOrderStateException("Service is no longer available");
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
		OrderDomain order = OrderDomain.create(tenantId.value(), customerId,
				new FullNameDomain(request.customerName()), new PhoneDomain(request.customerPhone()), customerEmail,
				customerAddress, service, request.quantity(), request.weightKg(), discount, priority, paymentMethod,
				pickupAt, estimatedDeliveryAt, new NoteDomain(request.notes()), principal.userId());
		Set<String> codes = distinctCodes(request.promoCodes());
		if(codes.isEmpty()){
			presenter.present(persist(request, order, List.of(), List.of(), principal));
			return;
		}
		OrderPromotionSagaDomain saga = OrderPromotionSagaDomain.open(tenantId.value(), order.orderNumberValue(),
				principal.userId());
		promotionGateway.openSaga(saga);
		List<PromotionRedemptionHttpResponse> redemptions = redeemPromotions(order, codes, tenantId, principal, saga);
		try{
			AppliedPromotions applied = applyPromotionsToOrder(order, redemptions);
			OrderCreateResponse response = persist(request, applied.order(), redemptions, applied.granted(),
					principal);
			promotionGateway.markSagaCommittedAfterCommit(saga.commit(principal.userId()));
			presenter.present(response);
		}catch(RuntimeException e){
			releasePromotions(saga, principal, e);
			throw e;
		}
	}

	private OrderCreateResponse persist(OrderCreateRequest request, OrderDomain order,
	                                    List<PromotionRedemptionHttpResponse> redemptions,
	                                    List<MoneyDomain> grantedAmounts,
	                                    OrderAuthPrincipal principal){
		OrderDomain saved = gateway.save(order);
		List<OrderPromotionDomain> promotions = savePromotions(saved, redemptions, grantedAmounts, principal);
		List<OrderItemDomain> items = saveItems(request, saved, principal);
		OrderDomain current = Boolean.TRUE.equals(request.pickedUpImmediately())
				? gateway.markPickedUp(saved, principal) : saved;
		publisher.publish(publisher.save(AnalyticsEventConfig.order(AnalyticsEventType.ORDER_CREATED,
				OrderAnalyticsMessage.from(current, items, promotions), principal.userId())));
		return new OrderCreateResponse(current, items, promotions);
	}

	private void releasePromotions(OrderPromotionSagaDomain saga, OrderAuthPrincipal principal,
	                               RuntimeException cause){
		PromotionReleaseHttpRequest release = new PromotionReleaseHttpRequest(saga.tenantId(), saga.referenceId(),
				principal.userId());
		try{
			promotionGateway.release(release);
			promotionGateway.markSagaReleased(saga.release(principal.userId()));
		}catch(RuntimeException e){
			log.error("Failed to release promotion redemption(s) claimed for order {}; the saga sweeper will retry",
					saga.referenceId(), e);
			cause.addSuppressed(e);
		}
	}

	private void closeRejectedSaga(OrderPromotionSagaDomain saga, OrderAuthPrincipal principal,
	                               RuntimeException cause){
		try{
			promotionGateway.markSagaReleased(saga.release(principal.userId()));
		}catch(RuntimeException e){
			log.warn("Failed to close the promotion saga for rejected order {}; the saga sweeper will close it",
					saga.referenceId(), e);
			cause.addSuppressed(e);
		}
	}

	private AppliedPromotions applyPromotionsToOrder(OrderDomain order, List<PromotionRedemptionHttpResponse> redemptions){
		OrderDomain running = order;
		List<MoneyDomain> granted = new ArrayList<>(redemptions.size());
		for(PromotionRedemptionHttpResponse response : redemptions){
			MoneyDomain amount = new MoneyDomain(response.discountAmount()).min(running.discountRoom());
			granted.add(amount);
			running = running.applyPromotionDiscount(amount);
		}
		return new AppliedPromotions(running, granted);
	}

	private Set<String> distinctCodes(List<String> promoCodes){
		if(promoCodes == null){
			return Set.of();
		}
		Set<String> codes = new LinkedHashSet<>();
		for(String code : promoCodes){
			if(code == null || code.isBlank()){
				continue;
			}
			String trimmed = code.trim();
			if(!codes.add(trimmed)){
				throw new InvalidOrderStateException("Duplicate promo code: " + trimmed);
			}
		}
		return codes;
	}

	private List<PromotionRedemptionHttpResponse> redeemPromotions(OrderDomain order, Collection<String> codes,
	                                                                TenantIdDomain tenantId,
	                                                                OrderAuthPrincipal principal,
	                                                                OrderPromotionSagaDomain saga){
		PromotionRedemptionHttpRequest request = new PromotionRedemptionHttpRequest(tenantId.value(),
				codes, order.subtotal().value(), order.orderNumberValue(), order.customerId(),
				principal.userId());
		List<PromotionRedemptionHttpResponse> responses = promotionGateway.redeem(request);
		for(PromotionRedemptionHttpResponse response : responses){
			if(response == null){
				throw new InvalidOrderStateException("Promotion code is not recognised");
			}
			if(!response.applied()){
				InvalidOrderStateException rejection = new InvalidOrderStateException(response.message());
				closeRejectedSaga(saga, principal, rejection);
				throw rejection;
			}
		}
		return responses;
	}

	private List<OrderPromotionDomain> savePromotions(OrderDomain order,
	                                                  List<PromotionRedemptionHttpResponse> redemptions,
	                                                  List<MoneyDomain> grantedAmounts,
	                                                  OrderAuthPrincipal principal){
		List<OrderPromotionDomain> saved = new ArrayList<>(redemptions.size());
		for(int i = 0; i < redemptions.size(); i++){
			PromotionRedemptionHttpResponse redeemed = redemptions.get(i);
			saved.add(gateway.save(OrderPromotionDomain.register(order.id(), redeemed.promotionId(),
					redeemed.code(), grantedAmounts.get(i), principal.userId())));
		}
		return saved;
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

	private record AppliedPromotions(OrderDomain order, List<MoneyDomain> granted){
	}

}
