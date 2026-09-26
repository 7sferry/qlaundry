package com.ferry.analytics.webservice.config;

import com.clickhouse.client.api.Client;
import com.ferry.analytics.core.dashboard.DashboardSummaryGateway;
import com.ferry.analytics.core.dashboard.DashboardSummaryUseCase;
import com.ferry.analytics.core.dashboard.DefaultDashboardSummaryUseCase;
import com.ferry.analytics.core.event.laundryservice.DefaultLaundryServiceEventUseCase;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventGateway;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventUseCase;
import com.ferry.analytics.core.event.order.DefaultOrderEventUseCase;
import com.ferry.analytics.core.event.order.OrderEventGateway;
import com.ferry.analytics.core.event.order.OrderEventUseCase;
import com.ferry.analytics.core.report.DefaultReportUseCase;
import com.ferry.analytics.core.report.ReportGateway;
import com.ferry.analytics.core.report.ReportUseCase;
import com.ferry.analytics.gateway.common.AnalyticStore;
import com.ferry.analytics.gateway.clickhouse.ClickHouseStore;
import com.ferry.analytics.gateway.dashboard.DashboardSummaryClickHouseGateway;
import com.ferry.analytics.gateway.event.LaundryServiceEventClickHouseGateway;
import com.ferry.analytics.gateway.event.OrderEventClickHouseGateway;
import com.ferry.analytics.gateway.report.ReportClickHouseGateway;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Duration;
import java.time.InstantSource;

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
	InstantSource instantSource(){
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
		return new ClickHouseStore(clickHouseClient, jsonManager, timeout);
	}

	@Bean
	OrderEventGateway orderEventGateway(AnalyticStore analyticStore){
		return new OrderEventClickHouseGateway(analyticStore);
	}

	@Bean
	OrderEventUseCase orderEventUseCase(OrderEventGateway orderEventGateway){
		return new DefaultOrderEventUseCase(orderEventGateway);
	}

	@Bean
	LaundryServiceEventGateway laundryServiceEventGateway(AnalyticStore analyticStore){
		return new LaundryServiceEventClickHouseGateway(analyticStore);
	}

	@Bean
	LaundryServiceEventUseCase laundryServiceEventUseCase(LaundryServiceEventGateway laundryServiceEventGateway){
		return new DefaultLaundryServiceEventUseCase(laundryServiceEventGateway);
	}

	@Bean
	DashboardSummaryGateway dashboardSummaryGateway(AnalyticStore analyticStore){
		return new DashboardSummaryClickHouseGateway(analyticStore);
	}

	@Bean
	DashboardSummaryUseCase dashboardSummaryUseCase(DashboardSummaryGateway dashboardSummaryGateway, InstantSource instantSource){
		return new DefaultDashboardSummaryUseCase(dashboardSummaryGateway, instantSource);
	}

	@Bean
	ReportGateway reportGateway(AnalyticStore analyticStore){
		return new ReportClickHouseGateway(analyticStore);
	}

	@Bean
	ReportUseCase reportUseCase(ReportGateway reportGateway, InstantSource instantSource){
		return new DefaultReportUseCase(reportGateway, instantSource);
	}

}
