package com.ferry.order.webservice.config;

import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.backfill.*;
import com.ferry.order.core.analytics.sweep.OrderAnalyticsOutboxSweepGateway;
import com.ferry.order.core.analytics.sweep.OrderAnalyticsOutboxSweepUseCase;
import com.ferry.order.core.analytics.sweep.DefaultOrderAnalyticsOutboxSweepUseCase;
import com.ferry.order.core.invoice.link.DefaultInvoiceLinkUseCase;
import com.ferry.order.core.invoice.link.InvoiceLinkUseCase;
import com.ferry.order.core.invoice.pdf.DefaultInvoicePdfUseCase;
import com.ferry.order.core.invoice.pdf.InvoiceComposer;
import com.ferry.order.core.invoice.pdf.InvoicePdfGateway;
import com.ferry.order.core.invoice.pdf.InvoicePdfUseCase;
import com.ferry.order.core.machine.create.DefaultMachineCreateUseCase;
import com.ferry.order.core.machine.create.MachineCreateGateway;
import com.ferry.order.core.machine.create.MachineCreateUseCase;
import com.ferry.order.core.machine.list.DefaultMachineListUseCase;
import com.ferry.order.core.machine.list.MachineListGateway;
import com.ferry.order.core.machine.list.MachineListUseCase;
import com.ferry.order.core.machine.update.DefaultMachineUpdateUseCase;
import com.ferry.order.core.machine.update.MachineUpdateGateway;
import com.ferry.order.core.machine.update.MachineUpdateUseCase;
import com.ferry.order.core.order.assignmachines.DefaultOrderAssignMachinesUseCase;
import com.ferry.order.core.order.assignmachines.OrderAssignMachinesGateway;
import com.ferry.order.core.order.assignmachines.OrderAssignMachinesUseCase;
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
import com.ferry.order.gateway.analytics.repository.OrderAnalyticsEventJpaRepository;
import com.ferry.order.gateway.customer.HttpOrderCustomerGateway;
import com.ferry.order.gateway.invoice.HtmlInvoiceComposer;
import com.ferry.order.gateway.invoice.JpaInvoicePdfGateway;
import com.ferry.order.gateway.machine.JpaMachineCreateGateway;
import com.ferry.order.gateway.machine.JpaMachineListGateway;
import com.ferry.order.gateway.machine.JpaMachineUpdateGateway;
import com.ferry.order.gateway.machine.repository.MachineJpaRepository;
import com.ferry.order.gateway.order.*;
import com.ferry.order.gateway.order.repository.*;
import com.ferry.order.gateway.promotion.HttpOrderPromotionGateway;
import com.ferry.order.gateway.service.JpaLaundryServiceCreateGateway;
import com.ferry.order.gateway.service.JpaLaundryServiceDeleteGateway;
import com.ferry.order.gateway.service.JpaLaundryServiceListGateway;
import com.ferry.order.gateway.service.JpaLaundryServiceUpdateGateway;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.webservice.analytics.sweep.OrderAnalyticsOutboxScheduler;
import com.ferry.order.webservice.order.saga.OrderPromotionSagaScheduler;
import com.ferry.promotion.client.PromotionServiceClient;
import com.ferry.user.client.UserServiceClient;
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
	                                                        IdGenerator idGenerator){
		return new JpaLaundryServiceCreateGateway(laundryServiceJpaRepository, idGenerator);
	}

	@Bean
	LaundryServiceCreateUseCase laundryServiceCreateUseCase(LaundryServiceCreateGateway laundryServiceCreateGateway,
	                                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultLaundryServiceCreateUseCase(laundryServiceCreateGateway, analyticsEventPublisher);
	}

	@Bean
	LaundryServiceListGateway laundryServiceListGateway(LaundryServiceJpaRepository laundryServiceJpaRepository){
		return new JpaLaundryServiceListGateway(laundryServiceJpaRepository);
	}

	@Bean
	LaundryServiceListUseCase laundryServiceListUseCase(LaundryServiceListGateway laundryServiceListGateway){
		return new DefaultLaundryServiceListUseCase(laundryServiceListGateway);
	}

	@Bean
	LaundryServiceUpdateGateway laundryServiceUpdateGateway(LaundryServiceJpaRepository laundryServiceJpaRepository){
		return new JpaLaundryServiceUpdateGateway(laundryServiceJpaRepository);
	}

	@Bean
	LaundryServiceUpdateUseCase laundryServiceUpdateUseCase(LaundryServiceUpdateGateway laundryServiceUpdateGateway,
	                                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultLaundryServiceUpdateUseCase(laundryServiceUpdateGateway, analyticsEventPublisher);
	}

	@Bean
	LaundryServiceDeleteGateway laundryServiceDeleteGateway(LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                                        OrderJpaRepository orderJpaRepository){
		return new JpaLaundryServiceDeleteGateway(laundryServiceJpaRepository, orderJpaRepository);
	}

	@Bean
	LaundryServiceDeleteUseCase laundryServiceDeleteUseCase(LaundryServiceDeleteGateway laundryServiceDeleteGateway,
	                                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultLaundryServiceDeleteUseCase(laundryServiceDeleteGateway, analyticsEventPublisher);
	}

	@Bean
	MachineCreateGateway machineCreateGateway(MachineJpaRepository machineJpaRepository, IdGenerator idGenerator){
		return new JpaMachineCreateGateway(machineJpaRepository, idGenerator);
	}

	@Bean
	MachineCreateUseCase machineCreateUseCase(MachineCreateGateway machineCreateGateway){
		return new DefaultMachineCreateUseCase(machineCreateGateway);
	}

	@Bean
	MachineListGateway machineListGateway(MachineJpaRepository machineJpaRepository,
	                                      OrderMachineJpaRepository orderMachineJpaRepository){
		return new JpaMachineListGateway(machineJpaRepository, orderMachineJpaRepository);
	}

	@Bean
	MachineListUseCase machineListUseCase(MachineListGateway machineListGateway){
		return new DefaultMachineListUseCase(machineListGateway);
	}

	@Bean
	MachineUpdateGateway machineUpdateGateway(MachineJpaRepository machineJpaRepository,
	                                          OrderMachineJpaRepository orderMachineJpaRepository){
		return new JpaMachineUpdateGateway(machineJpaRepository, orderMachineJpaRepository);
	}

	@Bean
	MachineUpdateUseCase machineUpdateUseCase(MachineUpdateGateway machineUpdateGateway){
		return new DefaultMachineUpdateUseCase(machineUpdateGateway);
	}

	@Bean
	OrderCustomerGateway customerVerificationGateway(UserServiceClient userServiceClient){
		return new HttpOrderCustomerGateway(userServiceClient);
	}

	@Bean
	OrderPromotionGateway orderPromotionGateway(PromotionServiceClient promotionServiceClient,
	                                            OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository,
	                                            IdGenerator idGenerator,
	                                            PlatformTransactionManager transactionManager){
		return new HttpOrderPromotionGateway(promotionServiceClient, orderPromotionSagaJpaRepository, idGenerator, transactionManager);
	}

	@Bean
	OrderCreateGateway orderCreateGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                      IdGenerator idGenerator,
	                                      CryptoTool cryptoTool,
	                                      OrderConfirmUseCase orderConfirmUseCase,
	                                      OrderPickupUseCase orderPickupUseCase){
		return new JpaOrderCreateGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				laundryServiceJpaRepository, idGenerator, cryptoTool, orderConfirmUseCase, orderPickupUseCase);
	}

	@Bean
	OrderCreateUseCase orderCreateUseCase(OrderCreateGateway orderCreateGateway,
	                                      OrderCustomerGateway customerGateway,
	                                      OrderPromotionGateway promotionGateway,
	                                      OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderCreateUseCase(orderCreateGateway, customerGateway, promotionGateway, analyticsEventPublisher);
	}

	@Bean
	OrderPromotionSagaSweepGateway orderPromotionSagaSweepGateway(
			OrderPromotionSagaJpaRepository orderPromotionSagaJpaRepository,
			OrderJpaRepository orderJpaRepository,
			PlatformTransactionManager transactionManager){
		return new JpaOrderPromotionSagaSweepGateway(orderPromotionSagaJpaRepository,
				orderJpaRepository, transactionManager);
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
		return new JpaOrderListGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
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
	                                      OrderMachineJpaRepository orderMachineJpaRepository,
	                                      CryptoTool cryptoTool){
		return new JpaOrderDetailGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				orderMachineJpaRepository, cryptoTool);
	}

	@Bean
	OrderDetailUseCase orderDetailUseCase(OrderDetailGateway orderDetailGateway){
		return new DefaultOrderDetailUseCase(orderDetailGateway);
	}

	@Bean
	OrderScheduleGateway orderScheduleGateway(OrderJpaRepository orderJpaRepository){
		return new JpaOrderScheduleGateway(orderJpaRepository);
	}

	@Bean
	OrderScheduleUseCase orderScheduleUseCase(OrderScheduleGateway orderScheduleGateway, Clock clock){
		return new DefaultOrderScheduleUseCase(orderScheduleGateway, clock);
	}

	@Bean
	Clock clock(){
		return Clock.systemUTC();
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
		return new JpaInvoicePdfGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	InvoiceComposer orderInvoiceComposer(ITemplateEngine invoiceTemplateEngine){
		return new HtmlInvoiceComposer(invoiceTemplateEngine);
	}

	@Bean
	InvoicePdfUseCase orderInvoiceUseCase(InvoicePdfGateway invoicePdfGateway,
	                                      InvoiceComposer invoiceHtmlComposer,
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
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new JpaOrderConfirmGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderConfirmUseCase orderConfirmUseCase(OrderConfirmGateway orderConfirmGateway,
	                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderConfirmUseCase(orderConfirmGateway, analyticsEventPublisher);
	}

	@Bean
	OrderPickupGateway orderPickupGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      CryptoTool cryptoTool){
		return new JpaOrderPickupGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderPickupUseCase orderPickupUseCase(OrderPickupGateway orderPickupGateway,
	                                      OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderPickupUseCase(orderPickupGateway, analyticsEventPublisher);
	}

	@Bean
	OrderProcessGateway orderProcessGateway(OrderJpaRepository orderJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        OrderMachineJpaRepository orderMachineJpaRepository,
	                                        MachineJpaRepository machineJpaRepository,
	                                        IdGenerator idGenerator,
	                                        CryptoTool cryptoTool){
		return new JpaOrderProcessGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				orderMachineJpaRepository, machineJpaRepository, idGenerator, cryptoTool);
	}

	@Bean
	OrderProcessUseCase orderProcessUseCase(OrderProcessGateway orderProcessGateway,
	                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderProcessUseCase(orderProcessGateway, analyticsEventPublisher);
	}

	@Bean
	OrderAssignMachinesGateway orderAssignMachinesGateway(OrderJpaRepository orderJpaRepository,
	                                                      OrderMachineJpaRepository orderMachineJpaRepository,
	                                                      MachineJpaRepository machineJpaRepository,
	                                                      IdGenerator idGenerator,
	                                                      CryptoTool cryptoTool){
		return new JpaOrderAssignMachinesGateway(orderJpaRepository, orderMachineJpaRepository, machineJpaRepository,
				idGenerator, cryptoTool);
	}

	@Bean
	OrderAssignMachinesUseCase orderAssignMachinesUseCase(OrderAssignMachinesGateway orderAssignMachinesGateway){
		return new DefaultOrderAssignMachinesUseCase(orderAssignMachinesGateway);
	}

	@Bean
	OrderReadyGateway orderReadyGateway(OrderJpaRepository orderJpaRepository,
	                                    OrderItemJpaRepository orderItemJpaRepository,
	                                    OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                    CryptoTool cryptoTool){
		return new JpaOrderReadyGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderReadyUseCase orderReadyUseCase(OrderReadyGateway orderReadyGateway,
	                                    OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderReadyUseCase(orderReadyGateway, analyticsEventPublisher);
	}

	@Bean
	OrderDeliverGateway orderDeliverGateway(OrderJpaRepository orderJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new JpaOrderDeliverGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderDeliverUseCase orderDeliverUseCase(OrderDeliverGateway orderDeliverGateway,
	                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderDeliverUseCase(orderDeliverGateway, analyticsEventPublisher);
	}

	@Bean
	OrderCompleteGateway orderCompleteGateway(OrderJpaRepository orderJpaRepository,
	                                          OrderItemJpaRepository orderItemJpaRepository,
	                                          OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                          CryptoTool cryptoTool){
		return new JpaOrderCompleteGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderCompleteUseCase orderCompleteUseCase(OrderCompleteGateway orderCompleteGateway,
	                                          OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderCompleteUseCase(orderCompleteGateway, analyticsEventPublisher);
	}

	@Bean
	OrderCancelGateway orderCancelGateway(OrderJpaRepository orderJpaRepository,
	                                      OrderItemJpaRepository orderItemJpaRepository,
	                                      OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                      CryptoTool cryptoTool){
		return new JpaOrderCancelGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderCancelUseCase orderCancelUseCase(OrderCancelGateway orderCancelGateway,
	                                      OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderCancelUseCase(orderCancelGateway, analyticsEventPublisher);
	}

	@Bean
	OrderPaymentGateway orderPaymentGateway(OrderJpaRepository orderJpaRepository,
	                                        OrderItemJpaRepository orderItemJpaRepository,
	                                        OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                        CryptoTool cryptoTool){
		return new JpaOrderPaymentGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				cryptoTool);
	}

	@Bean
	OrderPaymentUseCase orderPaymentUseCase(OrderPaymentGateway orderPaymentGateway,
	                                        OrderAnalyticsPublisher analyticsEventPublisher){
		return new DefaultOrderPaymentUseCase(orderPaymentGateway, analyticsEventPublisher);
	}

	@Bean
	JsonManager jsonManager(ObjectMapper objectMapper){
		return new DefaultJsonManager(objectMapper);
	}

	@Bean
	OrderAnalyticsStreamWriter analyticsStreamWriter(StringRedisTemplate stringRedisTemplate,
	                                                 OrderAnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                 PlatformTransactionManager transactionManager,
	                                                 @Value("${app.analytics.stream.event.key}") String streamKeyPrefix){
		return new DefaultOrderAnalyticsStreamWriter(stringRedisTemplate, analyticsEventJpaRepository,
				transactionManager, streamKeyPrefix);
	}

	@Bean
	OrderAnalyticsPublisher analyticsEventPublisher(OrderAnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                OrderAnalyticsStreamWriter analyticsStreamWriter,
	                                                IdGenerator idGenerator,
	                                                JsonManager jsonManager,
	                                                PlatformTransactionManager transactionManager){
		return new RedisOrderAnalyticsPublisher(analyticsEventJpaRepository, analyticsStreamWriter, idGenerator,
				jsonManager, transactionManager);
	}

	@Bean
	OrderAnalyticsOutboxSweepGateway analyticsOutboxSweepGateway(OrderAnalyticsEventJpaRepository analyticsEventJpaRepository,
	                                                             OrderAnalyticsStreamWriter analyticsStreamWriter,
	                                                             PlatformTransactionManager transactionManager){
		return new JpaOrderAnalyticsOutboxSweepGateway(analyticsEventJpaRepository, analyticsStreamWriter,
				transactionManager);
	}

	@Bean
	OrderAnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase(OrderAnalyticsOutboxSweepGateway analyticsOutboxSweepGateway,
	                                                             @Value("${app.analytics.outbox.grace-period}") Duration gracePeriod,
	                                                             @Value("${app.analytics.outbox.sweep-batch-size}") int sweepBatchSize){
		return new DefaultOrderAnalyticsOutboxSweepUseCase(analyticsOutboxSweepGateway, gracePeriod, sweepBatchSize);
	}

	@Bean
	@Lazy(false)
	OrderAnalyticsOutboxScheduler analyticsOutboxScheduler(OrderAnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase){
		OrderAnalyticsOutboxScheduler scheduler = new OrderAnalyticsOutboxScheduler(analyticsOutboxSweepUseCase);
		Thread.startVirtualThread(scheduler::sweep);
		return scheduler;
	}

	@Bean
	OrderAnalyticsBackfillGateway analyticsBackfillGateway(OrderJpaRepository orderJpaRepository,
	                                                       OrderItemJpaRepository orderItemJpaRepository,
	                                                       OrderPromotionJpaRepository orderPromotionJpaRepository,
	                                                       LaundryServiceJpaRepository laundryServiceJpaRepository,
	                                                       CryptoTool cryptoTool){
		return new JpaOrderAnalyticsBackfillGateway(orderJpaRepository, orderItemJpaRepository, orderPromotionJpaRepository,
				laundryServiceJpaRepository, cryptoTool);
	}

	@Bean
	OrderAnalyticsBackfillUseCase analyticsBackfillUseCase(OrderAnalyticsBackfillGateway analyticsBackfillGateway,
	                                                       OrderAnalyticsPublisher analyticsEventPublisher,
	                                                       @Value("${app.analytics.outbox.backfill-batch-size}") int backfillBatchSize){
		return new DefaultOrderAnalyticsBackfillUseCase(analyticsBackfillGateway, analyticsEventPublisher, backfillBatchSize);
	}

//	@Bean
//	@Lazy(false)
	ApplicationRunner analyticsBackfillRunner(OrderAnalyticsBackfillUseCase analyticsBackfillUseCase,
	                                          @Value("${app.analytics.backfill.tenant-id:}") String tenantId){
		return _ -> {
			OrderAnalyticsBackfillResponse response = analyticsBackfillUseCase.execute(new OrderAnalyticsBackfillRequest(tenantId));
			log.info("Analytics backfill done: {} laundry service(s), {} order(s) published", response.services(),
					response.orders());
		};
	}

}
