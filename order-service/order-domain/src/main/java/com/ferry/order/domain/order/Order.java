package com.ferry.order.domain.order;

import com.ferry.order.domain.common.AddressLine;
import com.ferry.order.domain.common.Decimals;
import com.ferry.order.domain.common.Email;
import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.ServiceUnit;
import lombok.Builder;

import java.time.Instant;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record Order(
	String id,
	OrderNumber orderNumber,
	String tenantId,
	String customerId,
	FullName customerName,
	Phone customerPhone,
	Email customerEmail,
	AddressLine customerAddress,
	String serviceId,
	String serviceName,
	ServiceUnit unit,
	Money unitPrice,
	int quantity,
	Double weightKg,
	Money subtotal,
	Money discount,
	Money totalPrice,
	OrderPriority priority,
	PaymentMethod paymentMethod,
	PaymentStatus paymentStatus,
	OrderStatus status,
	Note notes,
	Note staffNotes,
	Instant pickupAt,
	Instant estimatedDeliveryAt,
	Instant completedAt,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public Order{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidOrderStateException("Tenant id must not be blank");
		}
		if(orderNumber == null || customerName == null || customerPhone == null){
			throw new InvalidOrderStateException("Order number, customer name and customer phone must not be null");
		}
		if(serviceId == null || serviceId.isBlank()){
			throw new InvalidOrderStateException("Service id must not be blank");
		}
		if(quantity <= 0){
			throw new InvalidOrderStateException("Quantity must be greater than zero");
		}
		if(pickupAt == null){
			throw new InvalidOrderStateException("Pickup date must not be null");
		}
		weightKg = Decimals.scaled(weightKg);
	}

	public static Order create(String tenantId, ZoneId tenantZone, String customerId, FullName customerName,
	                                 Phone customerPhone, Email customerEmail,
	                                 AddressLine customerAddress, LaundryService service, int quantity,
	                                 Double weightKg, Money discount, OrderPriority priority,
	                                 PaymentMethod paymentMethod, Instant pickupAt, Instant estimatedDeliveryAt,
	                                 Note notes, String createdBy){
		Instant now = Instant.now();
		Money subtotal = service.priceFor(quantity, weightKg, priority);
		Money appliedDiscount = discount == null ? Money.ZERO : discount;
		Instant deliveryAt = estimatedDeliveryAt == null ? service.estimatedDeliveryFrom(pickupAt) : estimatedDeliveryAt;
		return new Order(null, OrderNumber.generate(tenantZone), tenantId, customerId, customerName,
				customerPhone, customerEmail, customerAddress, service.id(), service.name(), service.unit(),
				service.pricePerUnit(), quantity, weightKg, subtotal, appliedDiscount,
				subtotal.minus(appliedDiscount), priority, paymentMethod, PaymentStatus.UNPAID, OrderStatus.PENDING,
				notes, null, pickupAt, deliveryAt, null, null, false, now, createdBy, now, createdBy);
	}

	public Money discountRoom(){
		return subtotal.minus(discount);
	}

	public Order applyPromotionDiscount(Money granted){
		Money combined = discount.plus(granted);
		return toBuilder()
				.discount(combined)
				.totalPrice(subtotal.minus(combined))
				.build();
	}

	public Order changeStatus(OrderStatus next, Note staffNotes, String updatedBy){
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

	public Order markPaid(String updatedBy){
		if(paymentStatus == PaymentStatus.PAID){
			throw new InvalidOrderStatusException("Order is already paid");
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
