package com.ferry.order.core.order.create;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.common.AddressLine;
import com.ferry.order.domain.common.Email;
import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.UnsupportedPaymentMethodException;
import com.ferry.order.domain.customer.CustomerId;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.order.OrderPromotionSaga;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;
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
	private final OrderAnalyticsPublisher publisher;

	@Override
	public void execute(OrderCreateRequest request, OrderAuthPrincipal principal, OrderCreatePresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		String customerId = verifiedCustomerId(request.customerId(), tenantId);
		LaundryServiceId serviceId = new LaundryServiceId(request.serviceId());
		LaundryService service = gateway.findServiceById(serviceId, tenantId)
				.orElseThrow(() -> new NotFoundException("Service Not Found"));
		if(!service.active()){
			throw new InvalidOrderStateException("Service is no longer available");
		}
		OrderPriority priority = request.priority() == null ? OrderPriority.NORMAL : request.priority();
		PaymentMethod paymentMethod = resolvePaymentMethod(request.paymentMethod());
		Instant pickupAt = request.pickupAt() == null ? Instant.now() : Instant.ofEpochMilli(request.pickupAt());
		Instant estimatedDeliveryAt = request.estimatedDeliveryAt() == null
				? null : Instant.ofEpochMilli(request.estimatedDeliveryAt());
		Email customerEmail = request.customerEmail() == null || request.customerEmail().isBlank()
				? null : new Email(request.customerEmail());
		AddressLine customerAddress = request.customerAddress() == null || request.customerAddress().isBlank()
				? null : new AddressLine(request.customerAddress());
		Money discount = request.discount() == null ? Money.ZERO : new Money(request.discount());
		Order order = Order.create(tenantId.value(), customerId,
				new FullName(request.customerName()), new Phone(request.customerPhone()), customerEmail,
				customerAddress, service, request.quantity(), request.weightKg(), discount, priority, paymentMethod,
				pickupAt, estimatedDeliveryAt, new Note(request.notes()), principal.userId());
		Set<String> codes = distinctCodes(request.promoCodes());
		if(codes.isEmpty()){
			presenter.present(persist(request, order, List.of(), List.of(), principal));
			return;
		}
		OrderPromotionSaga saga = OrderPromotionSaga.open(tenantId.value(), order.orderNumberValue(),
				principal.userId());
		promotionGateway.openSaga(saga);
		List<PromotionRedemptionHttpResponse> redemptions = redeemPromotions(order, codes, tenantId, principal, saga);
		try{
			AppliedPromotionList applied = applyPromotionsToOrder(order, redemptions);
			OrderCreateResponse response = persist(request, applied.order(), redemptions, applied.granted(),
					principal);
			promotionGateway.markSagaCommittedAfterCommit(saga.commit(principal.userId()));
			presenter.present(response);
		}catch(RuntimeException e){
			releasePromotions(saga, principal, e);
			throw e;
		}
	}

	private OrderCreateResponse persist(OrderCreateRequest request, Order order,
	                                    List<PromotionRedemptionHttpResponse> redemptions,
	                                    List<Money> grantedAmounts,
	                                    OrderAuthPrincipal principal){
		Order saved = gateway.save(order);
		List<OrderPromotion> promotions = savePromotions(saved, redemptions, grantedAmounts, principal);
		List<OrderItem> items = saveItems(request, saved, principal);
		Order current = Boolean.TRUE.equals(request.pickedUpImmediately())
				? gateway.markPickedUp(saved, principal) : saved;
		publisher.publish(publisher.save(AnalyticsEventConfig.order(AnalyticsEventType.ORDER_CREATED,
				OrderAnalyticsMessage.from(current, items, promotions), principal.userId())));
		return new OrderCreateResponse(current, items, promotions);
	}

	private void releasePromotions(OrderPromotionSaga saga, OrderAuthPrincipal principal,
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

	private void closeRejectedSaga(OrderPromotionSaga saga, OrderAuthPrincipal principal,
	                               RuntimeException cause){
		try{
			promotionGateway.markSagaReleased(saga.release(principal.userId()));
		}catch(RuntimeException e){
			log.warn("Failed to close the promotion saga for rejected order {}; the saga sweeper will close it",
					saga.referenceId(), e);
			cause.addSuppressed(e);
		}
	}

	private AppliedPromotionList applyPromotionsToOrder(Order order, List<PromotionRedemptionHttpResponse> redemptions){
		Order running = order;
		List<Money> granted = new ArrayList<>(redemptions.size());
		for(PromotionRedemptionHttpResponse response : redemptions){
			Money amount = new Money(response.discountAmount()).min(running.discountRoom());
			granted.add(amount);
			running = running.applyPromotionDiscount(amount);
		}
		return new AppliedPromotionList(running, granted);
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

	private List<PromotionRedemptionHttpResponse> redeemPromotions(Order order, Collection<String> codes,
	                                                                TenantId tenantId,
	                                                                OrderAuthPrincipal principal,
	                                                                OrderPromotionSaga saga){
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

	private List<OrderPromotion> savePromotions(Order order,
	                                                  List<PromotionRedemptionHttpResponse> redemptions,
	                                                  List<Money> grantedAmounts,
	                                                  OrderAuthPrincipal principal){
		List<OrderPromotion> saved = new ArrayList<>(redemptions.size());
		for(int i = 0; i < redemptions.size(); i++){
			PromotionRedemptionHttpResponse redeemed = redemptions.get(i);
			saved.add(gateway.save(OrderPromotion.register(order.id(), redeemed.promotionId(),
					redeemed.code(), grantedAmounts.get(i), principal.userId())));
		}
		return saved;
	}

	private String verifiedCustomerId(String customerId, TenantId tenantId){
		if(customerId == null || customerId.isBlank()){
			return null;
		}
		CustomerId customer = new CustomerId(customerId);
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

	private List<OrderItem> saveItems(OrderCreateRequest request, Order order,
	                                        OrderAuthPrincipal principal){
		List<OrderCreateRequest.Item> requestItems = request.items() == null ? List.of() : request.items();
		List<OrderItem> items = new ArrayList<>(requestItems.size());
		for(OrderCreateRequest.Item item : requestItems){
			items.add(gateway.save(OrderItem.register(order.id(), item.type(), item.label(), item.quantity(),
					principal.userId())));
		}
		return items;
	}

	private record AppliedPromotionList(Order order, List<Money> granted){
	}

}
