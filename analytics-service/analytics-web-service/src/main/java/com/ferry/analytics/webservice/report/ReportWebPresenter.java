package com.ferry.analytics.webservice.report;

import com.ferry.analytics.core.report.ReportPresenter;
import com.ferry.analytics.core.report.ReportResponse;
import com.ferry.analytics.webservice.report.ReportWebResponse.ServiceShare;
import com.ferry.analytics.webservice.report.ReportWebResponse.TrendPoint;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class ReportWebPresenter implements ReportPresenter{
	private ResponseEntity<ReportWebResponse> responseEntity;

	@Override
	public void present(ReportResponse response){
		List<TrendPoint> trend = response.revenueTrend().stream()
				.map(point -> new TrendPoint(point.label(), point.revenue(), point.orders()))
				.toList();
		List<ServiceShare> breakdown = response.serviceBreakdown().stream()
				.map(share -> new ServiceShare(share.serviceId(), share.serviceName(), share.count(), share.revenue(),
						share.percentage()))
				.toList();
		responseEntity = ResponseEntity.ok(new ReportWebResponse(response.period(), trend, breakdown));
	}

}
