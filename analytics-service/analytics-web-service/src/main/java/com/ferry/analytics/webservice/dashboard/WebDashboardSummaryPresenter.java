package com.ferry.analytics.webservice.dashboard;

import com.ferry.analytics.core.dashboard.DashboardSummaryPresenter;
import com.ferry.analytics.core.dashboard.DashboardSummaryResponse;
import com.ferry.analytics.webservice.dashboard.DashboardSummaryRestResponse.StatusCount;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class WebDashboardSummaryPresenter implements DashboardSummaryPresenter{
	private ResponseEntity<DashboardSummaryRestResponse> responseEntity;

	@Override
	public void present(DashboardSummaryResponse response){
		List<StatusCount> distribution = response.statusDistribution().stream()
				.map(status -> new StatusCount(status.status(), status.count()))
				.toList();
		responseEntity = ResponseEntity.ok(new DashboardSummaryRestResponse(response.todayOrders(),
				response.todayRevenue(), response.monthOrders(), response.monthRevenue(), response.pendingOrders(),
				response.inProgressOrders(), response.readyOrders(), response.revenueGrowth(),
				response.ordersGrowth(), distribution));
	}

}
