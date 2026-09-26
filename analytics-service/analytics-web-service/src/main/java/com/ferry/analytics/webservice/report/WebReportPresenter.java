package com.ferry.analytics.webservice.report;

import com.ferry.analytics.core.report.ReportPresenter;
import com.ferry.analytics.core.report.ReportResponse;
import com.ferry.analytics.webservice.report.ReportRestResponse.ServiceShare;
import com.ferry.analytics.webservice.report.ReportRestResponse.TrendPoint;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class WebReportPresenter implements ReportPresenter{
	private ResponseEntity<ReportRestResponse> responseEntity;

	@Override
	public void present(ReportResponse response){
		List<TrendPoint> trend = response.revenueTrend().stream()
				.map(point -> new TrendPoint(point.label(), point.revenue(), point.orders()))
				.toList();
		List<ServiceShare> breakdown = response.serviceBreakdown().stream()
				.map(share -> new ServiceShare(share.serviceId(), share.serviceName(), share.count(), share.revenue(),
						share.percentage()))
				.toList();
		responseEntity = ResponseEntity.ok(new ReportRestResponse(response.period(), trend, breakdown));
	}

}
