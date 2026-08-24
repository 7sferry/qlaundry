package com.ferry.order.core.order.create;

import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultOrderCreateUseCaseTest{

	private static final String TENANT_ID = "01TENANTMELATI000000000000";
	private static final String STAFF_ID = "01STAFFSARIWIJAYA000000000";
	private static final String SERVICE_ID = "01SERVICEKILOAN00000000000";
	private static final String ORDER_ID = "01ORDERKILOAN0000000000000";
	private static final String CUSTOMER_NAME = "intan permata";
	private static final String CUSTOMER_PHONE = "+6281355501234";

	@Mock
	OrderCreateGateway gateway;
	@Mock
	CustomerGateway customerGateway;
	@InjectMocks
	DefaultOrderCreateUseCase useCase;
	@Mock
	OrderCreatePresenter presenter;
	@Captor
	ArgumentCaptor<OrderDomain> orderCaptor;
	@Captor
	ArgumentCaptor<OrderItemDomain> itemCaptor;
	@Captor
	ArgumentCaptor<OrderCreateResponse> responseCaptor;

	@Test
	void givenBlankCustomerName_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, "  ", CUSTOMER_PHONE, null, null, SERVICE_ID,
				List.of(), 1, null, null, null, null, null, null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(OrderCreateResponse.class));
	}

	@Test
	void givenUnknownService_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, CUSTOMER_NAME, CUSTOMER_PHONE, null, null,
				SERVICE_ID, List.of(), 2, null, null, null, null, null, null, null);
		willReturn(Optional.empty()).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Service Not Found"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
		then(presenter).should(never())
				.present(any(OrderCreateResponse.class));
	}

	@Test
	void givenInactiveService_thenThrowsIllegalArgumentException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Setrika Satuan")
				.description(new NoteDomain("iron only"))
				.pricePerUnit(MoneyDomain.of(5000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.IRON)
				.estimatedHours(24)
				.expressMultiplier(1.0d)
				.popular(false)
				.active(false)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, CUSTOMER_NAME, CUSTOMER_PHONE, null, null,
				SERVICE_ID, List.of(), 4, null, null, null, null, null, null, null);
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Service is no longer available"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenPerKgServiceWithoutWeight_thenThrowsIllegalArgumentException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Kiloan Reguler")
				.description(new NoteDomain("regular wash"))
				.pricePerUnit(MoneyDomain.of(7000L))
				.unit(ServiceUnit.KG)
				.category(ServiceCategory.WASH)
				.estimatedHours(48)
				.expressMultiplier(1.5d)
				.popular(true)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, CUSTOMER_NAME, CUSTOMER_PHONE, null, null,
				SERVICE_ID, List.of(), 1, null, null, null, null, null, null, null);
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Weight in kg is required for a per-kg service"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenDiscountLargerThanSubtotal_thenThrowsIllegalArgumentException(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Kiloan Reguler")
				.description(new NoteDomain("regular wash"))
				.pricePerUnit(MoneyDomain.of(7000L))
				.unit(ServiceUnit.KG)
				.category(ServiceCategory.WASH)
				.estimatedHours(48)
				.expressMultiplier(1.5d)
				.popular(true)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, CUSTOMER_NAME, CUSTOMER_PHONE, null, null,
				SERVICE_ID, List.of(), 1, 2.0d, new BigDecimal("99000"), null, null, null, null, null);
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Discount must not exceed the subtotal"));

		then(gateway).should(never())
				.save(any(OrderDomain.class));
	}

	@Test
	void givenCustomerOfAnotherTenant_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderCreateRequest request = new OrderCreateRequest("01CUSTOMEROUTSIDER00000000", CUSTOMER_NAME,
				CUSTOMER_PHONE, null, null, SERVICE_ID, List.of(), 2, null, null, null, null, null, null, null);
		willReturn(false).given(customerGateway)
				.belongsToTenant(any(CustomerVerificationHttpRequest.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Customer Not Found"));

		then(customerGateway).should()
				.belongsToTenant(eq(new CustomerVerificationHttpRequest("01CUSTOMEROUTSIDER00000000", TENANT_ID)));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(OrderCreateResponse.class));
	}

	@Test
	void givenWalkInOrderWithoutCustomerId_thenSkipsCustomerVerification(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Sepatu")
				.description(new NoteDomain("shoe wash"))
				.pricePerUnit(MoneyDomain.of(25000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.WASH)
				.estimatedHours(36)
				.expressMultiplier(1.75d)
				.popular(false)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest("   ", CUSTOMER_NAME, CUSTOMER_PHONE, null, null,
				SERVICE_ID, List.of(), 2, null, null, null, null, null, null, null);
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<OrderDomain>getArgument(0).toBuilder().id(ORDER_ID).build())
				.given(gateway)
				.save(any(OrderDomain.class));

		useCase.execute(request, principal, presenter);

		then(customerGateway).shouldHaveNoInteractions();
		then(gateway).should()
				.save(orderCaptor.capture());

		thenSoftly(softly -> softly.then(orderCaptor.getValue().customerId()).isNull());
	}

	@Test
	void givenExpressOrderWithDiscount_thenPricesWithMultiplierAndSubtractsDiscount(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Cuci Kiloan Reguler")
				.description(new NoteDomain("regular wash"))
				.pricePerUnit(MoneyDomain.of(7000L))
				.unit(ServiceUnit.KG)
				.category(ServiceCategory.WASH)
				.estimatedHours(48)
				.expressMultiplier(1.5d)
				.popular(true)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, CUSTOMER_NAME, CUSTOMER_PHONE,
				"intan@laundry.test", "Jl. Melati No. 9", SERVICE_ID, List.of(), 1, 3.5d, new BigDecimal("750"),
				OrderPriority.EXPRESS, PaymentMethod.CASH, null, null, "please separate the whites");
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<OrderDomain>getArgument(0).toBuilder().id(ORDER_ID).build())
				.given(gateway)
				.save(any(OrderDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findServiceById(eq(new LaundryServiceIdDomain(SERVICE_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(orderCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		OrderDomain saved = orderCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.subtotal().value()).isEqualByComparingTo(new BigDecimal("36750.00"));
			softly.then(saved.discount().value()).isEqualByComparingTo(new BigDecimal("750.00"));
			softly.then(saved.totalPrice().value()).isEqualByComparingTo(new BigDecimal("36000.00"));
			softly.then(saved.weightKg()).isEqualTo(3.5d);
			softly.then(saved.priority()).isEqualTo(OrderPriority.EXPRESS);
			softly.then(saved.paymentMethod()).isEqualTo(PaymentMethod.CASH);
			softly.then(saved.paymentStatus()).isEqualTo(PaymentStatus.UNPAID);
			softly.then(saved.status()).isEqualTo(OrderStatus.PENDING);
			softly.then(saved.serviceName()).isEqualTo("Cuci Kiloan Reguler");
			softly.then(saved.unitPrice().value()).isEqualByComparingTo(new BigDecimal("7000.00"));
			softly.then(saved.customerEmailValue()).isEqualTo("intan@laundry.test");
			softly.then(saved.orderNumberValue()).startsWith("INV-");
			softly.then(saved.estimatedDeliveryAt()).isEqualTo(saved.pickupAt().plusSeconds(48 * 3600L));
			softly.then(responseCaptor.getValue().order().id()).isEqualTo(ORDER_ID);
		});
	}

	@Test
	void givenLocalFormatCustomerPhone_thenTheSnapshotIsStoredInE164(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Setrika Satuan")
				.description(new NoteDomain("iron only"))
				.pricePerUnit(MoneyDomain.of(5000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.IRON)
				.estimatedHours(24)
				.expressMultiplier(1.0d)
				.popular(false)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest(null, CUSTOMER_NAME, "0813 5550 1234", null, null,
				SERVICE_ID, List.of(), 3, null, null, null, null, null, null, null);
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<OrderDomain>getArgument(0).toBuilder().id(ORDER_ID).build())
				.given(gateway)
				.save(any(OrderDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(orderCaptor.capture());

		thenSoftly(softly -> softly.then(orderCaptor.getValue().customerPhoneValue()).isEqualTo(CUSTOMER_PHONE));
	}

	@Test
	void givenClothingItems_thenSavesEachItemAgainstTheCreatedOrder(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service = LaundryServiceDomain.builder()
				.id(SERVICE_ID)
				.tenantId(TENANT_ID)
				.name("Dry Clean Jas")
				.description(new NoteDomain("dry clean"))
				.pricePerUnit(MoneyDomain.of(35000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.DRY_CLEAN)
				.estimatedHours(72)
				.expressMultiplier(2.0d)
				.popular(false)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		OrderCreateRequest request = new OrderCreateRequest("01CUSTOMERINTAN00000000000", CUSTOMER_NAME,
				CUSTOMER_PHONE, null, null, SERVICE_ID,
				List.of(new OrderCreateRequest.Item(ClothingType.JACKET, "Jas Hitam", 2),
						new OrderCreateRequest.Item(ClothingType.SHIRT, "Kemeja Putih", 3)),
				5, null, null, null, null, null, null, null);
		willReturn(true).given(customerGateway)
				.belongsToTenant(any(CustomerVerificationHttpRequest.class));
		willReturn(Optional.of(service)).given(gateway)
				.findServiceById(any(LaundryServiceIdDomain.class), any(TenantIdDomain.class));
		willAnswer(invocation -> invocation.<OrderDomain>getArgument(0).toBuilder().id(ORDER_ID).build())
				.given(gateway)
				.save(any(OrderDomain.class));
		willAnswer(invocation -> invocation.<OrderItemDomain>getArgument(0)).given(gateway)
				.save(any(OrderItemDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should(times(2))
				.save(itemCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		List<OrderItemDomain> items = itemCaptor.getAllValues();
		OrderCreateResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(items.getFirst().type()).isEqualTo(ClothingType.JACKET);
			softly.then(items.getFirst().orderId()).isEqualTo(ORDER_ID);
			softly.then(items.getLast().type()).isEqualTo(ClothingType.SHIRT);
			softly.then(items.getLast().quantity()).isEqualTo(3);
			softly.then(response.items()).hasSize(2);
			softly.then(response.order().subtotal().value()).isEqualByComparingTo(new BigDecimal("175000.00"));
			softly.then(response.order().customerId()).isEqualTo("01CUSTOMERINTAN00000000000");
		});
	}

}
