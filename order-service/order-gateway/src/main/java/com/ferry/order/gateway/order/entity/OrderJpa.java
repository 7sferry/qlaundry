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
import com.ferry.order.gateway.service.entity.ServiceUnitJpa;
import com.ferry.utils.crypto.CryptoAad;
import com.ferry.utils.crypto.CryptoTool;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private ServiceUnitJpa unit;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "unit_id", insertable = false, updatable = false)
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private OrderPriorityJpa priority;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "priority_id", insertable = false, updatable = false)
	private short priorityId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PaymentMethodJpa paymentMethod;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "payment_method_id", insertable = false, updatable = false)
	private short paymentMethodId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PaymentStatusJpa paymentStatus;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "payment_status_id", insertable = false, updatable = false)
	private short paymentStatusId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private OrderStatusJpa status;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "status_id", insertable = false, updatable = false)
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

	public void setUnit(ServiceUnitJpa unit){
		this.unit = unit;
		this.unitId = unit.getId();
	}

	public void setPriority(OrderPriorityJpa priority){
		this.priority = priority;
		this.priorityId = priority.getId();
	}

	public void setPaymentMethod(PaymentMethodJpa paymentMethod){
		this.paymentMethod = paymentMethod;
		this.paymentMethodId = paymentMethod.getId();
	}

	public void setPaymentStatus(PaymentStatusJpa paymentStatus){
		this.paymentStatus = paymentStatus;
		this.paymentStatusId = paymentStatus.getId();
	}

	public void setStatus(OrderStatusJpa status){
		this.status = status;
		this.statusId = status.getId();
	}

	private static CryptoAad aad(String id, String column){
		return new CryptoAad(TABLE, column, id);
	}

	public static String normalizeEmail(String email){
		return email.toLowerCase(Locale.ROOT).trim();
	}

	public static OrderJpa construct(String id, Order order, ServiceUnitJpa unit,
	                                       OrderPriorityJpa priority, PaymentMethodJpa paymentMethod,
	                                       PaymentStatusJpa paymentStatus, OrderStatusJpa status,
	                                       CryptoTool cryptoTool){
		OrderJpa entity = new OrderJpa();
		entity.id = id;
		entity.orderNumber = order.orderNumberValue();
		entity.tenantId = order.tenantId();
		entity.customerId = order.customerId();
		entity.customerName = order.customerNameValue();
		String phone = order.customerPhoneValue();
		entity.customerPhoneCipher = cryptoTool.encrypt(phone, aad(id, COLUMN_CUSTOMER_PHONE));
		entity.customerPhoneHash = cryptoTool.blindIndex(phone);
		String email = order.customerEmailValue();
		if(email != null){
			entity.customerEmailCipher = cryptoTool.encrypt(email, aad(id, COLUMN_CUSTOMER_EMAIL));
			entity.customerEmailHash = cryptoTool.blindIndex(normalizeEmail(email));
		}
		String address = order.customerAddressValue();
		if(address != null){
			entity.customerAddressCipher = cryptoTool.encrypt(address, aad(id, COLUMN_CUSTOMER_ADDRESS));
		}
		entity.serviceId = order.serviceId();
		entity.serviceName = order.serviceName();
		entity.unit = unit;
		entity.unitId = unit.getId();
		entity.unitPrice = order.unitPrice().value();
		entity.quantity = order.quantity();
		entity.weightKg = order.weightKg();
		entity.subtotal = order.subtotal().value();
		entity.discount = order.discount().value();
		entity.totalPrice = order.totalPrice().value();
		entity.priority = priority;
		entity.priorityId = priority.getId();
		entity.paymentMethod = paymentMethod;
		entity.paymentMethodId = paymentMethod.getId();
		entity.paymentStatus = paymentStatus;
		entity.paymentStatusId = paymentStatus.getId();
		entity.status = status;
		entity.statusId = status.getId();
		entity.notes = order.notesValue();
		entity.staffNotes = order.staffNotesValue();
		entity.pickupAt = order.pickupAt();
		entity.estimatedDeliveryAt = order.estimatedDeliveryAt();
		entity.completedAt = order.completedAt();
		entity.createdBy = order.createdBy();
		entity.createdAt = order.createdAt();
		entity.updatedBy = order.updatedBy();
		entity.updatedAt = order.updatedAt();
		entity.deleted = order.deleted();
		entity.version = order.version();
		return entity;
	}

	public static Order construct(OrderJpa saved, CryptoTool cryptoTool){
		String phone = cryptoTool.decrypt(saved.customerPhoneCipher, aad(saved.id, COLUMN_CUSTOMER_PHONE));
		String email = saved.customerEmailCipher == null
				? null : cryptoTool.decrypt(saved.customerEmailCipher, aad(saved.id, COLUMN_CUSTOMER_EMAIL));
		String address = saved.customerAddressCipher == null
				? null : cryptoTool.decrypt(saved.customerAddressCipher, aad(saved.id, COLUMN_CUSTOMER_ADDRESS));
		return new Order(saved.id, new OrderNumber(saved.orderNumber), saved.tenantId, saved.customerId,
				new FullName(saved.customerName), new Phone(phone),
				email == null ? null : new Email(email),
				address == null ? null : new AddressLine(address), saved.serviceId, saved.serviceName,
				ServiceUnit.fromValue(saved.unitId).orElseThrow(), new Money(saved.unitPrice), saved.quantity,
				saved.weightKg, new Money(saved.subtotal), new Money(saved.discount),
				new Money(saved.totalPrice), OrderPriority.fromValue(saved.priorityId).orElseThrow(),
				PaymentMethod.fromValue(saved.paymentMethodId).orElseThrow(),
				PaymentStatus.fromValue(saved.paymentStatusId).orElseThrow(),
				OrderStatus.fromValue(saved.statusId).orElseThrow(), new Note(saved.notes),
				new Note(saved.staffNotes), saved.pickupAt, saved.estimatedDeliveryAt, saved.completedAt,
				saved.version, saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
