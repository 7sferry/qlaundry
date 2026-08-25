package com.ferry.order.webservice.config;

import com.ferry.order.core.order.cancel.DefaultOrderCancelUseCase;
import com.ferry.order.core.order.cancel.OrderCancelGateway;
import com.ferry.order.core.order.cancel.OrderCancelUseCase;
import com.ferry.order.core.order.complete.DefaultOrderCompleteUseCase;
import com.ferry.order.core.order.complete.OrderCompleteGateway;
import com.ferry.order.core.order.complete.OrderCompleteUseCase;
import com.ferry.order.core.order.confirm.DefaultOrderConfirmUseCase;
import com.ferry.order.core.order.confirm.OrderConfirmGateway;
import com.ferry.order.core.order.confirm.OrderConfirmUseCase;
import com.ferry.order.core.order.create.OrderCustomerGateway;
import com.ferry.order.core.order.create.DefaultOrderCreateUseCase;
import com.ferry.order.core.order.create.OrderCreateGateway;
import com.ferry.order.core.order.create.OrderCreateUseCase;
import com.ferry.order.core.order.deliver.DefaultOrderDeliverUseCase;
import com.ferry.order.core.order.deliver.OrderDeliverGateway;
import com.ferry.order.core.order.deliver.OrderDeliverUseCase;
import com.ferry.order.core.order.detail.DefaultOrderDetailUseCase;
import com.ferry.order.core.order.detail.OrderDetailGateway;
import com.ferry.order.core.order.detail.OrderDetailUseCase;
import com.ferry.order.core.invoice.link.DefaultInvoiceLinkUseCase;
import com.ferry.order.core.invoice.pdf.DefaultInvoicePdfUseCase;
import com.ferry.order.core.invoice.pdf.InvoiceHtmlComposer;
import com.ferry.order.core.invoice.pdf.InvoicePdfGateway;
import com.ferry.order.core.invoice.link.InvoiceLinkUseCase;
import com.ferry.order.core.invoice.pdf.InvoicePdfUseCase;
import com.ferry.order.core.order.list.DefaultOrderListUseCase;
import com.ferry.order.core.order.list.OrderListGateway;
import com.ferry.order.core.order.list.OrderListUseCase;
import com.ferry.order.core.order.payment.DefaultOrderPaymentUseCase;
import com.ferry.order.core.order.payment.OrderPaymentGateway;
import com.ferry.order.core.order.payment.OrderPaymentUseCase;
import com.ferry.order.core.order.pickup.DefaultOrderPickupUseCase;
import com.ferry.order.core.order.pickup.OrderPickupGateway;
import com.ferry.order.core.order.pickup.OrderPickupUseCase;
import com.ferry.order.core.order.process.DefaultOrderProcessUseCase;
import com.ferry.order.core.order.process.OrderProcessGateway;
import com.ferry.order.core.order.process.OrderProcessUseCase;
import com.ferry.order.core.order.ready.DefaultOrderReadyUseCase;
import com.ferry.order.core.order.ready.OrderReadyGateway;
import com.ferry.order.core.order.ready.OrderReadyUseCase;
import com.ferry.order.core.service.create.DefaultLaundryServiceCreateUseCase;
import com.ferry.order.core.service.create.LaundryServiceCreateGateway;
import com.ferry.order.core.service.create.LaundryServiceCreateUseCase;
import com.ferry.order.core.service.delete.DefaultLaundryServiceDeleteUseCase;
import com.ferry.order.core.service.delete.LaundryServiceDeleteGateway;
import com.ferry.order.core.service.delete.LaundryServiceDeleteUseCase;
import com.ferry.order.core.service.list.DefaultLaundryServiceListUseCase;
import com.ferry.order.core.service.list.LaundryServiceListGateway;
import com.ferry.order.core.service.list.LaundryServiceListUseCase;
import com.ferry.order.core.service.update.DefaultLaundryServiceUpdateUseCase;
import com.ferry.order.core.service.update.LaundryServiceUpdateGateway;
import com.ferry.order.core.service.update.LaundryServiceUpdateUseCase;
import com.ferry.order.core.tools.InvoiceLinkSigner;
import com.ferry.order.gateway.customer.OrderCustomerHttpGateway;
import com.ferry.order.gateway.invoice.InvoicePdfHtmlComposer;
import com.ferry.order.gateway.order.OrderCancelJpaGateway;
import com.ferry.order.gateway.order.OrderCompleteJpaGateway;
import com.ferry.order.gateway.order.OrderConfirmJpaGateway;
import com.ferry.order.gateway.order.OrderCreateJpaGateway;
import com.ferry.order.gateway.order.OrderDeliverJpaGateway;
import com.ferry.order.gateway.order.OrderDetailJpaGateway;
import com.ferry.order.gateway.order.InvoiceJpaPdfGateway;
import com.ferry.order.gateway.order.OrderListJpaGateway;
import com.ferry.order.gateway.order.OrderPaymentJpaGateway;
import com.ferry.order.gateway.order.OrderPickupJpaGateway;
import com.ferry.order.gateway.order.OrderProcessJpaGateway;
import com.ferry.order.gateway.order.OrderReadyJpaGateway;
import com.ferry.order.gateway.order.repository.ClothingTypeJpaRepository;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPriorityJpaRepository;
import com.ferry.order.gateway.order.repository.OrderStatusJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentMethodJpaRepository;
import com.ferry.order.gateway.order.repository.PaymentStatusJpaRepository;
import com.ferry.order.gateway.service.LaundryServiceCreateJpaGateway;
import com.ferry.order.gateway.service.LaundryServiceDeleteJpaGateway;
import com.ferry.order.gateway.service.LaundryServiceListJpaGateway;
import com.ferry.order.gateway.service.LaundryServiceUpdateJpaGateway;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceCategoryJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import com.ferry.user.client.DefaultUserServiceClient;
import com.ferry.user.client.UserServiceClient;
import com.ferry.user.client.UserServiceClientConfig;
import com.ferry.utils.crypto.AesGcmCryptoTool;
import com.ferry.utils.crypto.CryptoKeyConfig;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Slf4j
@Configuration
@Lazy
@EnableConfigurationProperties(CryptoKeysProperties.class)
public class OrderWebConfig{

	@Bean
	IdGenerator idGenerator(){
		return new UlidGenerator();
	}

	@Bean
	CryptoTool cryptoTool(CryptoKeysProperties cryptoKeysProperties){
		return new AesGcmCryptoTool(CryptoKeyConfig.of(cryptoKeysProperties.activeKeyId(),
				cryptoKeysProperties.keys(), cryptoKeysProperties.blindIndexKey(),
				cryptoKeysProperties.allowPlaintextRead()));
	}

	@Bean
	LaundryServiceCreateGateway laundryServiceCreateGateway(LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                                        ServiceCategoryJpaRepository serviceCategoryJpaRepository,
	                                                        IdGenerator idGenerator){
		return new LaundryServiceCreateJpaGateway(laundryServiceJpaRepository, serviceUnitJpaRepository,
				serviceCategoryJpaRepository, idGenerator);
	}

	@Bean
	LaundryServiceCreateUseCase laundryServiceCreateUseCase(LaundryServiceCreateGateway laundryServiceCreateGateway){
		return new DefaultLaundryServiceCreateUseCase(laundryServiceCreateGateway);
	}

	@Bean
	LaundryServiceListGateway laundryServiceListGateway(LaundryServiceJpaRepository laundryServiceJpaRepository){
		return new LaundryServiceListJpaGateway(laundryServiceJpaRepository);
	}

	@Bean
	LaundryServiceListUseCase laundryServiceListUseCase(LaundryServiceListGateway laundryServiceListGateway){
		return new DefaultLaundryServiceListUseCase(laundryServiceListGateway);
	}

	@Bean
	LaundryServiceUpdateGateway laundryServiceUpdateGateway(LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                                        ServiceCategoryJpaRepository serviceCategoryJpaRepository){
		return new LaundryServiceUpdateJpaGateway(laundryServiceJpaRepository, serviceUnitJpaRepository,
				serviceCategoryJpaRepository);
	}

	@Bean
	LaundryServiceUpdateUseCase laundryServiceUpdateUseCase(LaundryServiceUpdateGateway laundryServiceUpdateGateway){
		return new DefaultLaundryServiceUpdateUseCase(laundryServiceUpdateGateway);
	}

	@Bean
	LaundryServiceDeleteGateway laundryServiceDeleteGateway(LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                                        ServiceCategoryJpaRepository serviceCategoryJpaRepository,
	                                                        OrderJpaRepository orderJpaRepository){
		return new LaundryServiceDeleteJpaGateway(laundryServiceJpaRepository, serviceUnitJpaRepository,
				serviceCategoryJpaRepository, orderJpaRepository);
	}

	@Bean
	LaundryServiceDeleteUseCase laundryServiceDeleteUseCase(LaundryServiceDeleteGateway laundryServiceDeleteGateway){
		return new DefaultLaundryServiceDeleteUseCase(laundryServiceDeleteGateway);
	}

	@Bean
	UserServiceClient userServiceClient(@Value("${app.internal.user-service.base-url}") String baseUrl,
	                                    @Value("${app.internal.api-key}") String apiKey,
	                                    @Value("${app.internal.user-service.timeout:5s}") Duration timeout){
		return new DefaultUserServiceClient(new UserServiceClientConfig(baseUrl, apiKey, timeout));
	}

	@Bean
	OrderCustomerGateway customerVerificationGateway(UserServiceClient userServiceClient){
		return new OrderCustomerHttpGateway(userServiceClient);
	}

	@Bean
	OrderCreateGateway orderCreateGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                      ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                      OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                      PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                      PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                      OrderStatusJpaRepository orderStatusJpaRepository,
	                                      ClothingTypeJpaRepository clothingTypeJpaRepository,
	                                      IdGenerator idGenerator,
	                                      CryptoTool cryptoTool){
		return new OrderCreateJpaGateway(orderJpaRepository, orderItemJpaRepository, laundryServiceJpaRepository,
				serviceUnitJpaRepository, orderPriorityJpaRepository, paymentMethodJpaRepository,
				paymentStatusJpaRepository, orderStatusJpaRepository, clothingTypeJpaRepository, idGenerator,
				cryptoTool);
	}

	@Bean
	OrderCreateUseCase orderCreateUseCase(OrderCreateGateway orderCreateGateway,
	                                      OrderCustomerGateway customerGateway){
		return new DefaultOrderCreateUseCase(orderCreateGateway, customerGateway);
	}

	@Bean
	OrderListGateway orderListGateway(OrderJpaRepository orderJpaRepository,
	                                  OrderItemJpaRepository orderItemJpaRepository,
	                                  CryptoTool cryptoTool){
		return new OrderListJpaGateway(orderJpaRepository, orderItemJpaRepository, cryptoTool);
	}

	@Bean
	OrderListUseCase orderListUseCase(OrderListGateway orderListGateway){
		return new DefaultOrderListUseCase(orderListGateway);
	}

	@Bean
	OrderDetailGateway orderDetailGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      CryptoTool cryptoTool){
		return new OrderDetailJpaGateway(orderJpaRepository, orderItemJpaRepository, cryptoTool);
	}

	@Bean
	OrderDetailUseCase orderDetailUseCase(OrderDetailGateway orderDetailGateway){
		return new DefaultOrderDetailUseCase(orderDetailGateway);
	}

	@Bean
	ITemplateEngine invoiceTemplateEngine(){
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/invoice/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		templateResolver.setCacheable(true);
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	@Bean
	InvoicePdfGateway orderInvoiceGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      CryptoTool cryptoTool){
		return new InvoiceJpaPdfGateway(orderJpaRepository, orderItemJpaRepository, cryptoTool);
	}

	@Bean
	InvoiceHtmlComposer orderInvoiceComposer(ITemplateEngine invoiceTemplateEngine){
		return new InvoicePdfHtmlComposer(invoiceTemplateEngine);
	}

	@Bean
	InvoicePdfUseCase orderInvoiceUseCase(InvoicePdfGateway invoicePdfGateway,
	                                      InvoiceHtmlComposer invoiceHtmlComposer){
		return new DefaultInvoicePdfUseCase(invoicePdfGateway, invoiceHtmlComposer);
	}

	@Bean
	InvoiceLinkSigner invoiceLinkSigner(@Value("${app.invoice.link.secret}") String base64Secret){
		return new InvoiceLinkSigner(Base64.getDecoder().decode(base64Secret));
	}

	@Bean
	InvoiceLinkUseCase orderInvoiceLinkUseCase(InvoicePdfGateway invoicePdfGateway,
	                                           InvoiceLinkSigner invoiceLinkSigner){
		return new DefaultInvoiceLinkUseCase(invoicePdfGateway, invoiceLinkSigner);
	}

	@Bean
	OrderConfirmGateway orderConfirmGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderConfirmJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderConfirmUseCase orderConfirmUseCase(OrderConfirmGateway orderConfirmGateway){
		return new DefaultOrderConfirmUseCase(orderConfirmGateway);
	}

	@Bean
	OrderPickupGateway orderPickupGateway(OrderJpaRepository orderJpaRepository,
	                                      ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                      OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                      PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                      PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                      OrderStatusJpaRepository orderStatusJpaRepository,
	                                      CryptoTool cryptoTool){
		return new OrderPickupJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderPickupUseCase orderPickupUseCase(OrderPickupGateway orderPickupGateway){
		return new DefaultOrderPickupUseCase(orderPickupGateway);
	}

	@Bean
	OrderProcessGateway orderProcessGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderProcessJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderProcessUseCase orderProcessUseCase(OrderProcessGateway orderProcessGateway){
		return new DefaultOrderProcessUseCase(orderProcessGateway);
	}

	@Bean
	OrderReadyGateway orderReadyGateway(OrderJpaRepository orderJpaRepository,
	                                    ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                    OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                    PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                    PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                    OrderStatusJpaRepository orderStatusJpaRepository,
	                                    CryptoTool cryptoTool){
		return new OrderReadyJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderReadyUseCase orderReadyUseCase(OrderReadyGateway orderReadyGateway){
		return new DefaultOrderReadyUseCase(orderReadyGateway);
	}

	@Bean
	OrderDeliverGateway orderDeliverGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderDeliverJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderDeliverUseCase orderDeliverUseCase(OrderDeliverGateway orderDeliverGateway){
		return new DefaultOrderDeliverUseCase(orderDeliverGateway);
	}

	@Bean
	OrderCompleteGateway orderCompleteGateway(OrderJpaRepository orderJpaRepository,
	                                          ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                          OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                          PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                          PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                          OrderStatusJpaRepository orderStatusJpaRepository,
	                                          CryptoTool cryptoTool){
		return new OrderCompleteJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderCompleteUseCase orderCompleteUseCase(OrderCompleteGateway orderCompleteGateway){
		return new DefaultOrderCompleteUseCase(orderCompleteGateway);
	}

	@Bean
	OrderCancelGateway orderCancelGateway(OrderJpaRepository orderJpaRepository,
	                                      ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                      OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                      PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                      PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                      OrderStatusJpaRepository orderStatusJpaRepository,
	                                      CryptoTool cryptoTool){
		return new OrderCancelJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderCancelUseCase orderCancelUseCase(OrderCancelGateway orderCancelGateway){
		return new DefaultOrderCancelUseCase(orderCancelGateway);
	}

	@Bean
	OrderPaymentGateway orderPaymentGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderPaymentJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository, cryptoTool);
	}

	@Bean
	OrderPaymentUseCase orderPaymentUseCase(OrderPaymentGateway orderPaymentGateway){
		return new DefaultOrderPaymentUseCase(orderPaymentGateway);
	}

}
