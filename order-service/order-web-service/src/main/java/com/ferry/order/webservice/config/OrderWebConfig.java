package com.ferry.order.webservice.config;

import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.core.analytics.backfill.*;
import com.ferry.order.core.analytics.sweep.AnalyticsOutboxSweepGateway;
import com.ferry.order.core.analytics.sweep.AnalyticsOutboxSweepUseCase;
import com.ferry.order.core.analytics.sweep.DefaultAnalyticsOutboxSweepUseCase;
import com.ferry.order.core.customer.totals.CustomerOrderTotalsGateway;
import com.ferry.order.core.customer.totals.CustomerOrderTotalsUseCase;
import com.ferry.order.core.customer.totals.DefaultCustomerOrderTotalsUseCase;
import com.ferry.order.core.invoice.link.DefaultInvoiceLinkUseCase;
import com.ferry.order.core.invoice.link.InvoiceLinkUseCase;
import com.ferry.order.core.invoice.pdf.DefaultInvoicePdfUseCase;
import com.ferry.order.core.invoice.pdf.InvoiceHtmlComposer;
import com.ferry.order.core.invoice.pdf.InvoicePdfGateway;
import com.ferry.order.core.invoice.pdf.InvoicePdfUseCase;
import com.ferry.order.core.order.cancel.DefaultOrderCancelUseCase;
import com.ferry.order.core.order.cancel.OrderCancelGateway;
import com.ferry.order.core.order.cancel.OrderCancelUseCase;
import com.ferry.order.core.order.complete.DefaultOrderCompleteUseCase;
import com.ferry.order.core.order.complete.OrderCompleteGateway;
import com.ferry.order.core.order.complete.OrderCompleteUseCase;
import com.ferry.order.core.order.confirm.DefaultOrderConfirmUseCase;
import com.ferry.order.core.order.confirm.OrderConfirmGateway;
import com.ferry.order.core.order.confirm.OrderConfirmUseCase;
import com.ferry.order.core.order.create.*;
import com.ferry.order.core.order.deliver.DefaultOrderDeliverUseCase;
import com.ferry.order.core.order.deliver.OrderDeliverGateway;
import com.ferry.order.core.order.deliver.OrderDeliverUseCase;
import com.ferry.order.core.order.detail.DefaultOrderDetailUseCase;
import com.ferry.order.core.order.detail.OrderDetailGateway;
import com.ferry.order.core.order.detail.OrderDetailUseCase;
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
import com.ferry.order.core.order.saga.DefaultOrderPromotionSagaSweepUseCase;
import com.ferry.order.core.order.saga.OrderPromotionSagaSweepGateway;
import com.ferry.order.core.order.saga.OrderPromotionSagaSweepUseCase;
import com.ferry.order.core.order.schedule.DefaultOrderScheduleUseCase;
import com.ferry.order.core.order.schedule.OrderScheduleGateway;
import com.ferry.order.core.order.schedule.OrderScheduleUseCase;
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
import com.ferry.order.gateway.analytics.*;
import com.ferry.order.gateway.analytics.repository.AnalyticsAggregateJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventStatusJpaRepository;
import com.ferry.order.gateway.customer.CustomerOrderTotalsJpaGateway;
import com.ferry.order.gateway.customer.OrderCustomerHttpGateway;
import com.ferry.order.gateway.invoice.DefaultInvoiceHtmlComposer;
import com.ferry.order.gateway.invoice.InvoiceJpaPdfGateway;
import com.ferry.order.gateway.order.*;
import com.ferry.order.gateway.order.repository.*;
import com.ferry.order.gateway.promotion.OrderPromotionHttpGateway;
import com.ferry.order.gateway.service.LaundryServiceCreateJpaGateway;
import com.ferry.order.gateway.service.LaundryServiceDeleteJpaGateway;
import com.ferry.order.gateway.service.LaundryServiceListJpaGateway;
import com.ferry.order.gateway.service.LaundryServiceUpdateJpaGateway;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceCategoryJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import com.ferry.order.webservice.analytics.sweep.AnalyticsOutboxScheduler;
import com.ferry.order.webservice.order.saga.OrderPromotionSagaScheduler;
import com.ferry.promotion.client.DefaultPromotionServiceClient;
import com.ferry.promotion.client.PromotionServiceClient;
import com.ferry.promotion.client.PromotionServiceClientConfig;
import com.ferry.user.client.DefaultUserServiceClient;
import com.ferry.user.client.UserServiceClient;
import com.ferry.user.client.UserServiceClientConfig;
import com.ferry.utils.crypto.AesGcmCryptoTool;
import com.ferry.utils.crypto.CryptoKeyConfig;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import com.ferry.utils.linksigner.HmacLinkSigner;
import com.ferry.utils.linksigner.LinkSigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
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
	LaundryServiceCreateUseCase laundryServiceCreateUseCase(LaundryServiceCreateGateway laundryServiceCreateGateway,
	                                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultLaundryServiceCreateUseCase(laundryServiceCreateGateway, analyticsEventPublisher);
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
	LaundryServiceUpdateUseCase laundryServiceUpdateUseCase(LaundryServiceUpdateGateway laundryServiceUpdateGateway,
	                                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultLaundryServiceUpdateUseCase(laundryServiceUpdateGateway, analyticsEventPublisher);
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
	LaundryServiceDeleteUseCase laundryServiceDeleteUseCase(LaundryServiceDeleteGateway laundryServiceDeleteGateway,
	                                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultLaundryServiceDeleteUseCase(laundryServiceDeleteGateway, analyticsEventPublisher);
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
	PromotionServiceClient promotionServiceClient(@Value("${app.internal.promotion-service.base-url}") String baseUrl,
	                                              @Value("${app.internal.promotion-api-key}") String apiKey,
	                                              @Value("${app.internal.promotion-service.timeout:5s}") Duration timeout){
		return new DefaultPromotionServiceClient(new PromotionServiceClientConfig(baseUrl, apiKey, timeout));
	}

	@Bean
	OrderPromotionGateway orderPromotionGateway(PromotionServiceClient promotionServiceClient,
	                                            OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository,
	                                            OrderPromotionSagaStatusJpaRepository orderPromotionSagaStatusJpaRepository,
	                                            IdGenerator idGenerator,
	                                            PlatformTransactionManager transactionManager){
		return new OrderPromotionHttpGateway(promotionServiceClient, orderPromotionSagaJpaRepository, orderPromotionSagaStatusJpaRepository, idGenerator, transactionManager);
	}

	@Bean
	OrderCreateGateway orderCreateGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                      ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                      OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                      PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                      PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                      OrderStatusJpaRepository orderStatusJpaRepository,
	                                      ClothingTypeJpaRepository clothingTypeJpaRepository,
	                                      IdGenerator idGenerator,
	                                      CryptoTool cryptoTool,
	                                      OrderConfirmUseCase orderConfirmUseCase,
	                                      OrderPickupUseCase orderPickupUseCase){
		return new OrderCreateJpaGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				laundryServiceJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				clothingTypeJpaRepository, idGenerator, cryptoTool, orderConfirmUseCase, orderPickupUseCase);
	}

	@Bean
	OrderCreateUseCase orderCreateUseCase(OrderCreateGateway orderCreateGateway,
	                                      OrderCustomerGateway customerGateway,
	                                      OrderPromotionGateway promotionGateway,
	                                      AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderCreateUseCase(orderCreateGateway, customerGateway, promotionGateway, analyticsEventPublisher);
	}

	@Bean
	OrderPromotionSagaSweepGateway orderPromotionSagaSweepGateway(
			OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository,
			OrderPromotionSagaStatusJpaRepository orderPromotionSagaStatusJpaRepository,
			OrderJpaRepository orderJpaRepository,
			PlatformTransactionManager transactionManager){
		return new OrderPromotionSagaSweepJpaGateway(orderPromotionSagaJpaRepository,
				orderPromotionSagaStatusJpaRepository, orderJpaRepository, transactionManager);
	}

	@Bean
	OrderPromotionSagaSweepUseCase orderPromotionSagaSweepUseCase(
			OrderPromotionSagaSweepGateway orderPromotionSagaSweepGateway,
			OrderPromotionGateway promotionGateway,
			@Value("${app.promotion.saga.grace-period}") Duration gracePeriod,
			@Value("${app.promotion.saga.sweep-batch-size}") int sweepBatchSize){
		return new DefaultOrderPromotionSagaSweepUseCase(orderPromotionSagaSweepGateway, promotionGateway,
				gracePeriod, sweepBatchSize);
	}

	@Bean
	@Lazy(false)
	OrderPromotionSagaScheduler orderPromotionSagaScheduler(OrderPromotionSagaSweepUseCase orderPromotionSagaSweepUseCase){
		OrderPromotionSagaScheduler scheduler = new OrderPromotionSagaScheduler(orderPromotionSagaSweepUseCase);
		Thread.startVirtualThread(scheduler::sweep);
		return scheduler;
	}

	@Bean
	OrderListGateway orderListGateway(OrderJpaRepository orderJpaRepository,
	                                  OrderItemJpaRepository orderItemJpaRepository,
	                                  OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                  CryptoTool cryptoTool){
		return new OrderListJpaGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderListUseCase orderListUseCase(OrderListGateway orderListGateway){
		return new DefaultOrderListUseCase(orderListGateway);
	}

	@Bean
	OrderDetailGateway orderDetailGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      CryptoTool cryptoTool){
		return new OrderDetailJpaGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderDetailUseCase orderDetailUseCase(OrderDetailGateway orderDetailGateway){
		return new DefaultOrderDetailUseCase(orderDetailGateway);
	}

	@Bean
	OrderScheduleGateway orderScheduleGateway(OrderJpaRepository orderJpaRepository){
		return new OrderScheduleJpaGateway(orderJpaRepository);
	}

	@Bean
	OrderScheduleUseCase orderScheduleUseCase(OrderScheduleGateway orderScheduleGateway){
		return new DefaultOrderScheduleUseCase(orderScheduleGateway, Clock.systemUTC());
	}

	@Bean
	CustomerOrderTotalsGateway customerOrderTotalsGateway(OrderJpaRepository orderJpaRepository){
		return new CustomerOrderTotalsJpaGateway(orderJpaRepository);
	}

	@Bean
	CustomerOrderTotalsUseCase customerOrderTotalsUseCase(CustomerOrderTotalsGateway customerOrderTotalsGateway){
		return new DefaultCustomerOrderTotalsUseCase(customerOrderTotalsGateway);
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
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      CryptoTool cryptoTool){
		return new InvoiceJpaPdfGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	InvoiceHtmlComposer orderInvoiceComposer(ITemplateEngine invoiceTemplateEngine){
		return new DefaultInvoiceHtmlComposer(invoiceTemplateEngine);
	}

	@Bean
	InvoicePdfUseCase orderInvoiceUseCase(InvoicePdfGateway invoicePdfGateway,
	                                      InvoiceHtmlComposer invoiceHtmlComposer,
	                                      LinkSigner linkSigner){
		return new DefaultInvoicePdfUseCase(invoicePdfGateway, invoiceHtmlComposer, linkSigner);
	}

	@Bean
	LinkSigner linkSigner(@Value("${app.invoice.link.secret}") String base64Secret){
		SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(base64Secret), "HmacSHA256");
		return new HmacLinkSigner(secretKey);
	}

	@Bean
	InvoiceLinkUseCase orderInvoiceLinkUseCase(InvoicePdfGateway invoicePdfGateway,
	                                           LinkSigner linkSigner){
		return new DefaultInvoiceLinkUseCase(invoicePdfGateway, linkSigner);
	}

	@Bean
	OrderConfirmGateway orderConfirmGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderConfirmJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderConfirmUseCase orderConfirmUseCase(OrderConfirmGateway orderConfirmGateway,
	                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderConfirmUseCase(orderConfirmGateway, analyticsEventPublisher);
	}

	@Bean
	OrderPickupGateway orderPickupGateway(OrderJpaRepository orderJpaRepository,
	                                      ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                      OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                      PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                      PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                      OrderStatusJpaRepository orderStatusJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      CryptoTool cryptoTool){
		return new OrderPickupJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderPickupUseCase orderPickupUseCase(OrderPickupGateway orderPickupGateway,
	                                      AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderPickupUseCase(orderPickupGateway, analyticsEventPublisher);
	}

	@Bean
	OrderProcessGateway orderProcessGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderProcessJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderProcessUseCase orderProcessUseCase(OrderProcessGateway orderProcessGateway,
	                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderProcessUseCase(orderProcessGateway, analyticsEventPublisher);
	}

	@Bean
	OrderReadyGateway orderReadyGateway(OrderJpaRepository orderJpaRepository,
	                                    ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                    OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                    PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                    PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                    OrderStatusJpaRepository orderStatusJpaRepository,
	                                    OrderItemJpaRepository orderItemJpaRepository,
	                                    OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                    CryptoTool cryptoTool){
		return new OrderReadyJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderReadyUseCase orderReadyUseCase(OrderReadyGateway orderReadyGateway,
	                                    AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderReadyUseCase(orderReadyGateway, analyticsEventPublisher);
	}

	@Bean
	OrderDeliverGateway orderDeliverGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderDeliverJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderDeliverUseCase orderDeliverUseCase(OrderDeliverGateway orderDeliverGateway,
	                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderDeliverUseCase(orderDeliverGateway, analyticsEventPublisher);
	}

	@Bean
	OrderCompleteGateway orderCompleteGateway(OrderJpaRepository orderJpaRepository,
	                                          ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                          OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                          PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                          PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                          OrderStatusJpaRepository orderStatusJpaRepository,
	                                          OrderItemJpaRepository orderItemJpaRepository,
	                                          OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                          CryptoTool cryptoTool){
		return new OrderCompleteJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderCompleteUseCase orderCompleteUseCase(OrderCompleteGateway orderCompleteGateway,
	                                          AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderCompleteUseCase(orderCompleteGateway, analyticsEventPublisher);
	}

	@Bean
	OrderCancelGateway orderCancelGateway(OrderJpaRepository orderJpaRepository,
	                                      ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                      OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                      PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                      PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                      OrderStatusJpaRepository orderStatusJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      CryptoTool cryptoTool){
		return new OrderCancelJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderCancelUseCase orderCancelUseCase(OrderCancelGateway orderCancelGateway,
	                                      AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderCancelUseCase(orderCancelGateway, analyticsEventPublisher);
	}

	@Bean
	OrderPaymentGateway orderPaymentGateway(OrderJpaRepository orderJpaRepository,
	                                        ServiceUnitJpaRepository serviceUnitJpaRepository,
	                                        OrderPriorityJpaRepository orderPriorityJpaRepository,
	                                        PaymentMethodJpaRepository paymentMethodJpaRepository,
	                                        PaymentStatusJpaRepository paymentStatusJpaRepository,
	                                        OrderStatusJpaRepository orderStatusJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new OrderPaymentJpaGateway(orderJpaRepository, serviceUnitJpaRepository, orderPriorityJpaRepository,
				paymentMethodJpaRepository, paymentStatusJpaRepository, orderStatusJpaRepository,
				orderItemJpaRepository, orderPromotionJpaRepository, cryptoTool);
	}

	@Bean
	OrderPaymentUseCase orderPaymentUseCase(OrderPaymentGateway orderPaymentGateway,
	                                        AnalyticsEventPublisher analyticsEventPublisher){
		return new DefaultOrderPaymentUseCase(orderPaymentGateway, analyticsEventPublisher);
	}

	@Bean
	JsonManager jsonManager(ObjectMapper objectMapper){
		return new DefaultJsonManager(objectMapper);
	}

	@Bean
	AnalyticsStreamWriter analyticsStreamWriter(StringRedisTemplate stringRedisTemplate,
	                                            AnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                            AnalyticsEventStatusJpaRepository analyticsEventStatusJpaRepository,
	                                            PlatformTransactionManager transactionManager,
	                                            @Value("${app.analytics.stream.event.key}") String streamKeyPrefix){
		return new DefaultAnalyticsStreamWriter(stringRedisTemplate, analyticsEventJpaRepository,
				analyticsEventStatusJpaRepository, transactionManager, streamKeyPrefix);
	}

	@Bean
	AnalyticsEventPublisher analyticsEventPublisher(AnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                AnalyticsAggregateJpaRepository analyticsAggregateJpaRepository,
	                                                AnalyticsEventStatusJpaRepository analyticsEventStatusJpaRepository,
	                                                AnalyticsStreamWriter analyticsStreamWriter,
	                                                IdGenerator idGenerator,
	                                                JsonManager jsonManager,
	                                                PlatformTransactionManager transactionManager){
		return new OrderAnalyticsRedisPublisher(analyticsEventJpaRepository, analyticsAggregateJpaRepository,
				analyticsEventStatusJpaRepository, analyticsStreamWriter, idGenerator, jsonManager, transactionManager);
	}

	@Bean
	AnalyticsOutboxSweepGateway analyticsOutboxSweepGateway(AnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                        AnalyticsStreamWriter analyticsStreamWriter,
	                                                        PlatformTransactionManager transactionManager){
		return new AnalyticsOutboxSweepJpaGateway(analyticsEventJpaRepository, analyticsStreamWriter,
				transactionManager);
	}

	@Bean
	AnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase(AnalyticsOutboxSweepGateway analyticsOutboxSweepGateway,
	                                                        @Value("${app.analytics.outbox.grace-period}") Duration gracePeriod,
	                                                        @Value("${app.analytics.outbox.sweep-batch-size}") int sweepBatchSize){
		return new DefaultAnalyticsOutboxSweepUseCase(analyticsOutboxSweepGateway, gracePeriod, sweepBatchSize);
	}

	@Bean
	@Lazy(false)
	AnalyticsOutboxScheduler analyticsOutboxScheduler(AnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase){
		AnalyticsOutboxScheduler scheduler = new AnalyticsOutboxScheduler(analyticsOutboxSweepUseCase);
		Thread.startVirtualThread(scheduler::sweep);
		return scheduler;
	}

	@Bean
	AnalyticsBackfillGateway analyticsBackfillGateway(OrderJpaRepository orderJpaRepository,
	                                                  OrderItemJpaRepository orderItemJpaRepository,
	                                                  OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                                  LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                                  CryptoTool cryptoTool){
		return new AnalyticsBackfillJpaGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				laundryServiceJpaRepository, cryptoTool);
	}

	@Bean
	AnalyticsBackfillUseCase analyticsBackfillUseCase(AnalyticsBackfillGateway analyticsBackfillGateway,
	                                                  AnalyticsEventPublisher analyticsEventPublisher,
	                                                  @Value("${app.analytics.outbox.backfill-batch-size}") int backfillBatchSize){
		return new DefaultAnalyticsBackfillUseCase(analyticsBackfillGateway, analyticsEventPublisher, backfillBatchSize);
	}

//	@Bean
//	@Lazy(false)
	ApplicationRunner analyticsBackfillRunner(AnalyticsBackfillUseCase analyticsBackfillUseCase,
	                                          @Value("${app.analytics.backfill.tenant-id:}") String tenantId){
		return _ -> {
			AnalyticsBackfillResponse response = analyticsBackfillUseCase.execute(new AnalyticsBackfillRequest(tenantId));
			log.info("Analytics backfill done: {} laundry service(s), {} order(s) published", response.services(),
					response.orders());
		};
	}

}
