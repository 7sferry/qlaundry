package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.common.AddressLine;
import com.ferry.order.domain.common.Email;
import com.ferry.order.domain.common.FullName;
import com.ferry.order.domain.common.Money;
import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.Phone;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.utils.crypto.CryptoAad;
import com.ferry.utils.crypto.CryptoTool;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = OrderJpa.TABLE, indexes = {
		@Index(name = "idx_orders_tenant_id", columnList = "tenant_id"),
		@Index(name = "idx_orders_customer_id", columnList = "customer_id"),
		@Index(name = "idx_orders_customer_phone_hash", columnList = "customer_phone_hash")
})
public class OrderJpa{
	static final String TABLE = "orders";
	private static final String COLUMN_CUSTOMER_PHONE = "customer_phone";
	private static final String COLUMN_CUSTOMER_EMAIL = "customer_email";
	private static final String COLUMN_CUSTOMER_ADDRESS = "customer_address";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, unique = true, length = 30)
	private String orderNumber;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(name = "customer_id", length = 50)
	private String customerId;
	@Column(nullable = false, length = 100)
	private String customerName;
	@Column(name = "customer_phone", nullable = false, length = 128)
	private String customerPhoneCipher;
	@Column(name = "customer_phone_hash", length = 64)
	private String customerPhoneHash;
	@Column(name = "customer_email", length = 512)
	private String customerEmailCipher;
	@Column(name = "customer_email_hash", length = 64)
	private String customerEmailHash;
	@Column(name = "customer_address", length = 1024)
	private String customerAddressCipher;
	@Column(nullable = false, name = "service_id", length = 50)
	private String serviceId;
	@Column(nullable = false, length = 100)
	private String serviceName;
	@Column(nullable = false)
	private short unitId;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal unitPrice;
	@Column(nullable = false)
	private int quantity;
	@Column
	private Double weightKg;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal subtotal;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal discount;
	@Column(nullable = false, precision = 19, scale = Money.SCALE)
	private BigDecimal totalPrice;
	@Column(nullable = false)
	private short priorityId;
	@Column(nullable = false)
	private short paymentMethodId;
	@Column(nullable = false)
	private short paymentStatusId;
	@Column(nullable = false)
	private short statusId;
	@Column
	private String notes;
	@Column
	private String staffNotes;
	@Column(nullable = false)
	private Instant pickupAt;
	@Column(nullable = false)
	private Instant estimatedDeliveryAt;
	@Column
	private Instant completedAt;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	private static CryptoAad aad(String id, String column){
		return new CryptoAad(TABLE, column, id);
	}

	public static String normalizeEmail(String email){
		return email.toLowerCase(Locale.ROOT).trim();
	}

	public static OrderJpa construct(String id, Order order, CryptoTool cryptoTool){
		OrderJpa entity = new OrderJpa();
		entity.setId(id);
		entity.setOrderNumber(order.orderNumberValue());
		entity.setTenantId(order.tenantId());
		entity.setCustomerId(order.customerId());
		entity.setCustomerName(order.customerNameValue());
		String phone = order.customerPhoneValue();
		entity.setCustomerPhoneCipher(cryptoTool.encrypt(phone, aad(id, COLUMN_CUSTOMER_PHONE)));
		entity.setCustomerPhoneHash(cryptoTool.blindIndex(phone));
		String email = order.customerEmailValue();
		if(email != null){
			entity.setCustomerEmailCipher(cryptoTool.encrypt(email, aad(id, COLUMN_CUSTOMER_EMAIL)));
			entity.setCustomerEmailHash(cryptoTool.blindIndex(normalizeEmail(email)));
		}
		String address = order.customerAddressValue();
		if(address != null){
			entity.setCustomerAddressCipher(cryptoTool.encrypt(address, aad(id, COLUMN_CUSTOMER_ADDRESS)));
		}
		entity.setServiceId(order.serviceId());
		entity.setServiceName(order.serviceName());
		entity.setUnitId(order.unit().getValue());
		entity.setUnitPrice(order.unitPrice().value());
		entity.setQuantity(order.quantity());
		entity.setWeightKg(order.weightKg());
		entity.setSubtotal(order.subtotal().value());
		entity.setDiscount(order.discount().value());
		entity.setTotalPrice(order.totalPrice().value());
		entity.setPriorityId(order.priority().getValue());
		entity.setPaymentMethodId(order.paymentMethod().getValue());
		entity.setPaymentStatusId(order.paymentStatus().getValue());
		entity.setStatusId(order.status().getValue());
		entity.setNotes(order.notesValue());
		entity.setStaffNotes(order.staffNotesValue());
		entity.setPickupAt(order.pickupAt());
		entity.setEstimatedDeliveryAt(order.estimatedDeliveryAt());
		entity.setCompletedAt(order.completedAt());
		entity.setCreatedBy(order.createdBy());
		entity.setCreatedAt(order.createdAt());
		entity.setUpdatedBy(order.updatedBy());
		entity.setUpdatedAt(order.updatedAt());
		entity.setDeleted(order.deleted());
		entity.setVersion(order.version());
		return entity;
	}

	public static Order construct(OrderJpa saved, CryptoTool cryptoTool){
		String phone = cryptoTool.decrypt(saved.getCustomerPhoneCipher(), aad(saved.getId(), COLUMN_CUSTOMER_PHONE));
		String email = saved.getCustomerEmailCipher() == null
				? null : cryptoTool.decrypt(saved.getCustomerEmailCipher(), aad(saved.getId(), COLUMN_CUSTOMER_EMAIL));
		String address = saved.getCustomerAddressCipher() == null
				? null : cryptoTool.decrypt(saved.getCustomerAddressCipher(), aad(saved.getId(), COLUMN_CUSTOMER_ADDRESS));
		return new Order(saved.getId(), new OrderNumber(saved.getOrderNumber()), saved.getTenantId(),
				saved.getCustomerId(), new FullName(saved.getCustomerName()), new Phone(phone),
				email == null ? null : new Email(email), address == null ? null : new AddressLine(address),
				saved.getServiceId(), saved.getServiceName(), ServiceUnit.fromValue(saved.getUnitId()).orElseThrow(),
				new Money(saved.getUnitPrice()), saved.getQuantity(), saved.getWeightKg(),
				new Money(saved.getSubtotal()), new Money(saved.getDiscount()), new Money(saved.getTotalPrice()),
				OrderPriority.fromValue(saved.getPriorityId()).orElseThrow(),
				PaymentMethod.fromValue(saved.getPaymentMethodId()).orElseThrow(),
				PaymentStatus.fromValue(saved.getPaymentStatusId()).orElseThrow(),
				OrderStatus.fromValue(saved.getStatusId()).orElseThrow(), new Note(saved.getNotes()),
				new Note(saved.getStaffNotes()), saved.getPickupAt(), saved.getEstimatedDeliveryAt(),
				saved.getCompletedAt(), saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(),
				saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
