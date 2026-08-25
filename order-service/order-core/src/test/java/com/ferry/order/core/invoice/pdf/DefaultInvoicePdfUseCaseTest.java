package com.ferry.order.core.invoice.pdf;

import com.ferry.order.core.invoice.link.InvoiceLinkConstant;
import com.ferry.order.domain.common.FullNameDomain;
import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.PhoneDomain;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderNumberDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.utils.linksigner.LinkSigner;
import com.ferry.utils.linksigner.SignedLinkPayload;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultInvoicePdfUseCaseTest{

	private static final String TENANT_ID = "01TENANTTERATAI00000000000";
	private static final String STAFF_ID = "01STAFFNOVITASARI0000000000";
	private static final String ORDER_ID = "01ORDERSELIMUT000000000000";
	private static final String ORDER_NUMBER = "INV-20260815-2VD9RN";
	private static final String CUSTOMER_NAME = "agus wijaya";
	private static final String CUSTOMER_PHONE = "+6285211145678";
	private static final String TOKEN = "signed-token-selimut-invoice";

	@Mock
	InvoicePdfGateway gateway;
	@Mock
	InvoiceHtmlComposer composer;
	@Mock
	LinkSigner linkSigner;
	@InjectMocks
	DefaultInvoicePdfUseCase useCase;
	@Mock
	InvoicePdfPresenter presenter;
	@Captor
	ArgumentCaptor<InvoicePdfResponse> responseCaptor;

	@Test
	void givenBlankToken_thenThrowsConstraintViolationException(){
		InvoicePdfRequest request = new InvoicePdfRequest("  ");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(linkSigner).shouldHaveNoInteractions();
		then(gateway).shouldHaveNoInteractions();
		then(composer).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidOrExpiredToken_thenThrowsNotFoundException(){
		willReturn(Optional.empty()).given(linkSigner)
				.verify(anyString());

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new InvoicePdfRequest(TOKEN), presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Invoice link is invalid or expired"));

		then(gateway).shouldHaveNoInteractions();
		then(composer).shouldHaveNoInteractions();
	}

	@Test
	void givenTokenPayloadMissingTenantIdField_thenThrowsNotFoundException(){
		SignedLinkPayload payload = new SignedLinkPayload(Map.of(InvoiceLinkConstant.ORDER_ID_FIELD, ORDER_ID),
				Instant.now().plusSeconds(600).toEpochMilli());
		willReturn(Optional.of(payload)).given(linkSigner)
				.verify(anyString());

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new InvoicePdfRequest(TOKEN), presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Invoice link is invalid or expired"));

		then(gateway).shouldHaveNoInteractions();
		then(composer).shouldHaveNoInteractions();
	}

	@Test
	void givenValidTokenButOrderNotFound_thenThrowsNotFoundException(){
		SignedLinkPayload payload = new SignedLinkPayload(Map.of(InvoiceLinkConstant.ORDER_ID_FIELD, ORDER_ID,
				InvoiceLinkConstant.TENANT_ID_FIELD, TENANT_ID), Instant.now().plusSeconds(600).toEpochMilli());
		willReturn(Optional.of(payload)).given(linkSigner)
				.verify(anyString());
		willReturn(Optional.empty()).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new InvoicePdfRequest(TOKEN), presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Order Not Found"));

		then(gateway).should(never())
				.findItemsByOrderId(any(OrderIdDomain.class));
		then(composer).shouldHaveNoInteractions();
	}

	@Test
	void givenValidTokenAndOrderFound_thenComposesPdfAndPresentsResponse(){
		Instant now = Instant.now();
		SignedLinkPayload payload = new SignedLinkPayload(Map.of(InvoiceLinkConstant.ORDER_ID_FIELD, ORDER_ID,
				InvoiceLinkConstant.TENANT_ID_FIELD, TENANT_ID), now.plusSeconds(600).toEpochMilli());
		OrderDomain order = OrderDomain.builder()
				.id(ORDER_ID)
				.orderNumber(new OrderNumberDomain(ORDER_NUMBER))
				.tenantId(TENANT_ID)
				.customerName(new FullNameDomain(CUSTOMER_NAME))
				.customerPhone(new PhoneDomain(CUSTOMER_PHONE))
				.serviceId("01SERVICESELIMUT00000000000")
				.serviceName("Cuci Selimut")
				.unit(ServiceUnit.ITEM)
				.unitPrice(MoneyDomain.of(20000L))
				.quantity(2)
				.subtotal(MoneyDomain.of(40000L))
				.discount(MoneyDomain.ZERO)
				.totalPrice(MoneyDomain.of(40000L))
				.priority(OrderPriority.NORMAL)
				.paymentMethod(PaymentMethod.CASH)
				.paymentStatus(PaymentStatus.PAID)
				.status(OrderStatus.READY)
				.pickupAt(now)
				.estimatedDeliveryAt(now.plusSeconds(259200))
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		List<OrderItemDomain> items = List.of(OrderItemDomain.builder()
				.id("01ITEMSELIMUT00000000000000")
				.orderId(ORDER_ID)
				.type(ClothingType.OTHER)
				.label("Selimut Tebal")
				.quantity(2)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build());
		byte[] pdfBytes = "%PDF-1.4 fake selimut invoice".getBytes(StandardCharsets.UTF_8);
		willReturn(Optional.of(payload)).given(linkSigner)
				.verify(anyString());
		willReturn(Optional.of(order)).given(gateway)
				.findById(any(OrderIdDomain.class), any(TenantIdDomain.class));
		willReturn(items).given(gateway)
				.findItemsByOrderId(any(OrderIdDomain.class));
		willReturn(pdfBytes).given(composer)
				.compose(any(OrderDomain.class), any(List.class));

		useCase.execute(new InvoicePdfRequest(TOKEN), presenter);

		then(linkSigner).should()
				.verify(eq(TOKEN));
		then(gateway).should()
				.findById(eq(new OrderIdDomain(ORDER_ID)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.findItemsByOrderId(eq(new OrderIdDomain(ORDER_ID)));
		then(composer).should()
				.compose(eq(order), eq(items));
		then(presenter).should()
				.present(responseCaptor.capture());

		InvoicePdfResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.order()).isEqualTo(order);
			softly.then(response.pdf()).isEqualTo(pdfBytes);
		});
	}

}
