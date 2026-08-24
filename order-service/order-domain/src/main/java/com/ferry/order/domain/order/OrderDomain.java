package com.ferry.order.domain.order;

import com.ferry.order.domain.common.AddressLineDomain;
import com.ferry.order.domain.common.Decimals;
import com.ferry.order.domain.common.EmailDomain;
import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.ServiceUnit;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderDomain(String id, OrderNumberDomain orderNumber, String tenantId, String customerId,
                          FullNameDomain customerName, PhoneDomain customerPhone, EmailDomain customerEmail,
                          AddressLineDomain customerAddress, String serviceId, String serviceName, ServiceUnit unit,
                          MoneyDomain unitPrice, int quantity, Double weightKg, MoneyDomain subtotal,
                          MoneyDomain discount, MoneyDomain totalPrice, OrderPriority priority,
                          PaymentMethod paymentMethod, PaymentStatus paymentStatus, OrderStatus status,
                          NoteDomain notes, NoteDomain staffNotes, Instant pickupAt, Instant estimatedDeliveryAt,
                          Instant completedAt, Integer version, boolean deleted, Instant createdAt, String createdBy,
                          Instant updatedAt, String updatedBy){
	public OrderDomain{
		if(tenantId == null || tenantId.isBlank()){
			throw new IllegalArgumentException("Tenant id must not be blank");
		}
		if(orderNumber == null || customerName == null || customerPhone == null){
			throw new IllegalArgumentException("Order number, customer name and customer phone must not be null");
		}
		if(serviceId == null || serviceId.isBlank()){
			throw new IllegalArgumentException("Service id must not be blank");
		}
		if(quantity <= 0){
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}
		if(pickupAt == null){
			throw new IllegalArgumentException("Pickup date must not be null");
		}
		weightKg = Decimals.scaled(weightKg);
	}

	public static OrderDomain create(String tenantId, String customerId, FullNameDomain customerName,
	                                 PhoneDomain customerPhone, EmailDomain customerEmail,
	                                 AddressLineDomain customerAddress, LaundryServiceDomain service, int quantity,
	                                 Double weightKg, MoneyDomain discount, OrderPriority priority,
	                                 PaymentMethod paymentMethod, Instant pickupAt, Instant estimatedDeliveryAt,
	                                 NoteDomain notes, String createdBy){
		Instant now = Instant.now();
		MoneyDomain subtotal = service.priceFor(quantity, weightKg, priority);
		MoneyDomain appliedDiscount = discount == null ? MoneyDomain.ZERO : discount;
		Instant deliveryAt = estimatedDeliveryAt == null ? service.estimatedDeliveryFrom(pickupAt) : estimatedDeliveryAt;
		return new OrderDomain(null, OrderNumberDomain.generate(now), tenantId, customerId, customerName,
				customerPhone, customerEmail, customerAddress, service.id(), service.name(), service.unit(),
				service.pricePerUnit(), quantity, weightKg, subtotal, appliedDiscount,
				subtotal.minus(appliedDiscount), priority, paymentMethod, PaymentStatus.UNPAID, OrderStatus.PENDING,
				notes, null, pickupAt, deliveryAt, null, null, false, now, createdBy, now, createdBy);
	}

	public OrderDomain changeStatus(OrderStatus next, NoteDomain staffNotes, String updatedBy){
		if(!status.canTransitionTo(next)){
			throw new InvalidOrderStatusException("Cannot change order status from " + status + " to " + next);
		}
		Instant now = Instant.now();
		return toBuilder()
				.status(next)
				.staffNotes(staffNotes == null ? this.staffNotes : staffNotes)
				.completedAt(next == OrderStatus.COMPLETED ? now : completedAt)
				.updatedBy(updatedBy)
				.updatedAt(now)
				.build();
	}

	public OrderDomain markPaid(String updatedBy){
		if(paymentStatus == PaymentStatus.PAID){
			throw new IllegalArgumentException("Order is already paid");
		}
		if(status == OrderStatus.CANCELLED){
			throw new InvalidOrderStatusException("A cancelled order cannot be paid");
		}
		return toBuilder()
				.paymentStatus(PaymentStatus.PAID)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

	public String orderNumberValue(){
		return orderNumber.value();
	}

	public String customerNameValue(){
		return customerName.value();
	}

	public String customerPhoneValue(){
		return customerPhone.value();
	}

	public String customerEmailValue(){
		return customerEmail == null ? null : customerEmail.value();
	}

	public String customerAddressValue(){
		return customerAddress == null ? null : customerAddress.value();
	}

	public String notesValue(){
		return notes == null ? null : notes.value();
	}

	public String staffNotesValue(){
		return staffNotes == null ? null : staffNotes.value();
	}

}
