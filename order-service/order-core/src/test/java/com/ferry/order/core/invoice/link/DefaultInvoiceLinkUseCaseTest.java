package com.ferry.order.core.invoice.link;

import com.ferry.order.core.invoice.pdf.InvoicePdfGateway;
import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderNumberDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.linksigner.LinkSigner;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultInvoiceLinkUseCaseTest{

	private static final String TENANT_ID = "01TENANTKENANGA000000000000";
	private static final String STAFF_ID = "01STAFFBAYUFIRMANSYAH000000";
	private static final String ORDER_ID = "01ORDERGORDEN0000000000000";
	private static final String ORDER_NUMBER = "INV-20260810-4LM8QW";
	private static final String CUSTOMER_NAME = "siti rahayu";
	private static final String CUSTOMER_PHONE = "+6281388812345";
	private static final String TOKEN = "signed-token-gorden-invoice";

	@Mock
	InvoicePdfGateway gateway;
	@Mock
	LinkSigner signer;
	@InjectMocks
	DefaultInvoiceLinkUseCase useCase;
	@Mock
	InvoiceLinkPresenter presenter;
	@Captor
	ArgumentCaptor<Long> expiresAtCaptor;
	@Captor
	ArgumentCaptor<InvoiceLinkResponse> responseCaptor;

	@Test
	void givenBlankOrderId_thenThrowsConstraintViolationException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		InvoiceLinkRequest request = new InvoiceLinkRequest("  ");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(signer).shouldHaveNoInteractions();
	}

	@Test
	void givenOrderNotFoundOrNotOwnedByTenant_thenThrowsNotFoundException(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		willReturn(Optional.empty()).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new InvoiceLinkRequest(ORDER_ID), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(signer).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(InvoiceLinkResponse.class));
	}

	@Test
	void givenOrderBelongsToTenant_thenMintsSignedLinkAndPresentsTokenAndExpiry(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		OrderDomain order = OrderDomain.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumberDomain(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullNameDomain(CUSTOMER_NAME))
				.customerPhone(new PhoneDomain(CUSTOMER_PHONE))
				.serviceId("01SERVICEGORDEN00000000000")
				.serviceName("Cuci Gorden")
				.unit(ServiceUnit.SET)
				.unitPrice(MoneyDomain.of(45000L))
				.quantity(2)
				.subtotal(MoneyDomain.of(90000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(90000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.UNPAID)
				.status(OrderStatus.CONFIRMED)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(432000))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));
		willReturn(TOKEN).given(signer)
				.sign(anyLong(), anyMap());

		Instant before = Instant.now();
		useCase.execute(new InvoiceLinkRequest(ORDER_ID), principal, presenter);
		Instant after = Instant.now();

		then(gateway).should()
				.findById(eq(new OrderIdDomain(ORDER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(signer).should()
				.sign(expiresAtCaptor.capture(),
						eq(Map.of(InvoiceLinkConstant.ORDER_ID_FIELD, ORDER_ID, InvoiceLinkConstant.TENANT_ID_FIELD, TENANT_ID)));
		then(presenter).should()
				.present(responseCaptor.capture());

		long expiresAt = expiresAtCaptor.getValue();
		InvoiceLinkResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.token()).isEqualTo(TOKEN);
			softly.then(response.expiresAt()).isEqualTo(expiresAt);
			softly.then(expiresAt).isGreaterThanOrEqualTo(before.plus(InvoiceLinkConstant.LINK_TTL).toEpochMilli());
			softly.then(expiresAt).isLessThanOrEqualTo(after.plus(InvoiceLinkConstant.LINK_TTL).toEpochMilli());
		});
	}

}
