package com.ferry.analytics.webservice.config;

import com.clickhouse.client.api.Client;
import com.ferry.analytics.core.customer.summary.CustomerSummaryGateway;
import com.ferry.analytics.core.customer.summary.CustomerSummaryUseCase;
import com.ferry.analytics.core.customer.summary.DefaultCustomerSummaryUseCase;
import com.ferry.analytics.core.customer.top.DefaultTopCustomerUseCase;
import com.ferry.analytics.core.customer.top.TopCustomerGateway;
import com.ferry.analytics.core.customer.top.TopCustomerUseCase;
import com.ferry.analytics.core.customer.totals.CustomerTotalsGateway;
import com.ferry.analytics.core.customer.totals.CustomerTotalsUseCase;
import com.ferry.analytics.core.customer.totals.DefaultCustomerTotalsUseCase;
import com.ferry.analytics.core.dashboard.DashboardSummaryGateway;
import com.ferry.analytics.core.dashboard.DashboardSummaryUseCase;
import com.ferry.analytics.core.dashboard.DefaultDashboardSummaryUseCase;
import com.ferry.analytics.core.event.customer.CustomerEventGateway;
import com.ferry.analytics.core.event.customer.CustomerEventUseCase;
import com.ferry.analytics.core.event.customer.DefaultCustomerEventUseCase;
import com.ferry.analytics.core.event.laundryservice.DefaultLaundryServiceEventUseCase;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventGateway;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventUseCase;
import com.ferry.analytics.core.event.order.DefaultOrderEventUseCase;
import com.ferry.analytics.core.event.order.OrderEventGateway;
import com.ferry.analytics.core.event.order.OrderEventUseCase;
import com.ferry.analytics.core.event.promotion.DefaultPromotionEventUseCase;
import com.ferry.analytics.core.event.promotion.PromotionEventGateway;
import com.ferry.analytics.core.event.promotion.PromotionEventUseCase;
import com.ferry.analytics.core.event.staff.DefaultStaffEventUseCase;
import com.ferry.analytics.core.event.staff.StaffEventGateway;
import com.ferry.analytics.core.event.staff.StaffEventUseCase;
import com.ferry.analytics.core.promotion.DefaultPromotionSummaryUseCase;
import com.ferry.analytics.core.promotion.PromotionSummaryGateway;
import com.ferry.analytics.core.promotion.PromotionSummaryUseCase;
import com.ferry.analytics.core.report.DefaultReportUseCase;
import com.ferry.analytics.core.report.ReportGateway;
import com.ferry.analytics.core.report.ReportUseCase;
import com.ferry.analytics.core.service.DefaultServiceSummaryUseCase;
import com.ferry.analytics.core.service.ServiceSummaryGateway;
import com.ferry.analytics.core.service.ServiceSummaryUseCase;
import com.ferry.analytics.core.staff.DefaultStaffSummaryUseCase;
import com.ferry.analytics.core.staff.StaffSummaryGateway;
import com.ferry.analytics.core.staff.StaffSummaryUseCase;
import com.ferry.analytics.gateway.common.ClickHouseAnalyticStore;
import com.ferry.analytics.gateway.common.AnalyticStore;
import com.ferry.analytics.gateway.customer.ClickHouseCustomerSummaryGateway;
import com.ferry.analytics.gateway.customer.ClickHouseCustomerTotalsGateway;
import com.ferry.analytics.gateway.customer.ClickHouseTopCustomerGateway;
import com.ferry.analytics.gateway.dashboard.ClickHouseDashboardSummaryGateway;
import com.ferry.analytics.gateway.event.ClickHouseCustomerEventGateway;
import com.ferry.analytics.gateway.event.ClickHouseLaundryServiceEventGateway;
import com.ferry.analytics.gateway.event.ClickHouseOrderEventGateway;
import com.ferry.analytics.gateway.event.ClickHousePromotionEventGateway;
import com.ferry.analytics.gateway.event.ClickHouseStaffEventGateway;
import com.ferry.analytics.gateway.promotion.ClickHousePromotionSummaryGateway;
import com.ferry.analytics.gateway.report.ClickHouseReportGateway;
import com.ferry.analytics.gateway.service.ClickHouseServiceSummaryGateway;
import com.ferry.analytics.gateway.staff.ClickHouseStaffSummaryGateway;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Configuration
@Lazy
public class AnalyticsWebConfig{

	@Bean
	JsonManager jsonManager(ObjectMapper objectMapper){
		return new DefaultJsonManager(objectMapper);
	}

	@Bean
	Clock clock(){
		return Clock.systemUTC();
	}

	@Bean(destroyMethod = "close")
	Client clickHouseClient(@Value("${app.clickhouse.url}") String url,
	                        @Value("${app.clickhouse.database}") String database,
	                        @Value("${app.clickhouse.username}") String username,
	                        @Value("${app.clickhouse.password}") String password){
		return new Client.Builder()
				.addEndpoint(url)
				.setUsername(username)
				.setPassword(password)
				.setDefaultDatabase(database)
				.build();
	}

	@Bean
	AnalyticStore analyticStore(Client clickHouseClient, JsonManager jsonManager,
	                            @Value("${app.clickhouse.timeout:10s}") Duration timeout){
		return new ClickHouseAnalyticStore(clickHouseClient, jsonManager, timeout);
	}

	@Bean
	OrderEventGateway orderEventGateway(AnalyticStore analyticStore){
		return new ClickHouseOrderEventGateway(analyticStore);
	}

	@Bean
	OrderEventUseCase orderEventUseCase(OrderEventGateway orderEventGateway){
		return new DefaultOrderEventUseCase(orderEventGateway);
	}

	@Bean
	LaundryServiceEventGateway laundryServiceEventGateway(AnalyticStore analyticStore){
		return new ClickHouseLaundryServiceEventGateway(analyticStore);
	}

	@Bean
	LaundryServiceEventUseCase laundryServiceEventUseCase(LaundryServiceEventGateway laundryServiceEventGateway){
		return new DefaultLaundryServiceEventUseCase(laundryServiceEventGateway);
	}

	@Bean
	DashboardSummaryGateway dashboardSummaryGateway(AnalyticStore analyticStore){
		return new ClickHouseDashboardSummaryGateway(analyticStore);
	}

	@Bean
	DashboardSummaryUseCase dashboardSummaryUseCase(DashboardSummaryGateway dashboardSummaryGateway, Clock clock){
		return new DefaultDashboardSummaryUseCase(dashboardSummaryGateway, clock);
	}

	@Bean
	ReportGateway reportGateway(AnalyticStore analyticStore){
		return new ClickHouseReportGateway(analyticStore);
	}

	@Bean
	ReportUseCase reportUseCase(ReportGateway reportGateway, Clock clock){
		return new DefaultReportUseCase(reportGateway, clock);
	}

	@Bean
	StaffEventGateway staffEventGateway(AnalyticStore analyticStore){
		return new ClickHouseStaffEventGateway(analyticStore);
	}

	@Bean
	StaffEventUseCase staffEventUseCase(StaffEventGateway staffEventGateway){
		return new DefaultStaffEventUseCase(staffEventGateway);
	}

	@Bean
	CustomerEventGateway customerEventGateway(AnalyticStore analyticStore){
		return new ClickHouseCustomerEventGateway(analyticStore);
	}

	@Bean
	CustomerEventUseCase customerEventUseCase(CustomerEventGateway customerEventGateway){
		return new DefaultCustomerEventUseCase(customerEventGateway);
	}

	@Bean
	PromotionEventGateway promotionEventGateway(AnalyticStore analyticStore){
		return new ClickHousePromotionEventGateway(analyticStore);
	}

	@Bean
	PromotionEventUseCase promotionEventUseCase(PromotionEventGateway promotionEventGateway){
		return new DefaultPromotionEventUseCase(promotionEventGateway);
	}

	@Bean
	StaffSummaryGateway staffSummaryGateway(AnalyticStore analyticStore){
		return new ClickHouseStaffSummaryGateway(analyticStore);
	}

	@Bean
	StaffSummaryUseCase staffSummaryUseCase(StaffSummaryGateway staffSummaryGateway, Clock clock){
		return new DefaultStaffSummaryUseCase(staffSummaryGateway, clock);
	}

	@Bean
	CustomerSummaryGateway customerSummaryGateway(AnalyticStore analyticStore){
		return new ClickHouseCustomerSummaryGateway(analyticStore);
	}

	@Bean
	CustomerSummaryUseCase customerSummaryUseCase(CustomerSummaryGateway customerSummaryGateway, Clock clock){
		return new DefaultCustomerSummaryUseCase(customerSummaryGateway, clock);
	}

	@Bean
	TopCustomerGateway topCustomerGateway(AnalyticStore analyticStore){
		return new ClickHouseTopCustomerGateway(analyticStore);
	}

	@Bean
	TopCustomerUseCase topCustomerUseCase(TopCustomerGateway topCustomerGateway, Clock clock){
		return new DefaultTopCustomerUseCase(topCustomerGateway, clock);
	}

	@Bean
	CustomerTotalsGateway customerTotalsGateway(AnalyticStore analyticStore){
		return new ClickHouseCustomerTotalsGateway(analyticStore);
	}

	@Bean
	CustomerTotalsUseCase customerTotalsUseCase(CustomerTotalsGateway customerTotalsGateway){
		return new DefaultCustomerTotalsUseCase(customerTotalsGateway);
	}

	@Bean
	PromotionSummaryGateway promotionSummaryGateway(AnalyticStore analyticStore){
		return new ClickHousePromotionSummaryGateway(analyticStore);
	}

	@Bean
	PromotionSummaryUseCase promotionSummaryUseCase(PromotionSummaryGateway promotionSummaryGateway, Clock clock){
		return new DefaultPromotionSummaryUseCase(promotionSummaryGateway, clock);
	}

	@Bean
	ServiceSummaryGateway serviceSummaryGateway(AnalyticStore analyticStore){
		return new ClickHouseServiceSummaryGateway(analyticStore);
	}

	@Bean
	ServiceSummaryUseCase serviceSummaryUseCase(ServiceSummaryGateway serviceSummaryGateway){
		return new DefaultServiceSummaryUseCase(serviceSummaryGateway);
	}

}
