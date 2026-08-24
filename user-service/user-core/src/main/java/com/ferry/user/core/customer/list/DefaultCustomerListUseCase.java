package com.ferry.user.core.customer.list;

import com.ferry.utils.pagination.CursorCodec;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.CursorPage;
import com.ferry.utils.pagination.CursorPaginator;
import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerFilter;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerListUseCase implements CustomerListUseCase{
	private final CustomerListGateway gateway;

	@Override
	public void execute(CustomerListRequest request, UserAuthPrincipal principal, CustomerListPresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		String phone = request.phone() == null || request.phone().isBlank()
				? null : new PhoneDomain(request.phone()).value();
		SortBy sortBy = request.sortBy() == null ? SortBy.ID : request.sortBy();
		SortDirection sortDir = request.sortDir() == null ? SortDirection.DESC : request.sortDir();
		PageDirection direction = request.direction() == null ? PageDirection.NEXT : request.direction();
		PageCursor cursor = request.cursor() == null ? null : CursorCodec.decode(request.cursor());
		CustomerFilter filter = CustomerFilter.builder()
				.fullName(request.fullName())
				.phone(phone)
				.tenantId(tenantId.value())
				.sortBy(sortBy)
				.sortDir(sortDir)
				.pageDirection(direction)
				.cursor(cursor)
				.build();
		CursorFetch<CustomerDomain> fetch = gateway.findByFilter(filter);
		CursorPage<CustomerDomain> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> List.of(sortBy == SortBy.NAME ? row.fullNameValue() : row.id(), row.id()));
		List<CustomerDomain> customers = page.items();
		Set<String> customerIds = customers.stream().map(CustomerDomain::id).collect(Collectors.toSet());
		Map<String, List<CustomerEmailDomain>> emailsByCustomerId = getEmailsByCustomerId(customerIds);
		Map<String, List<CustomerPhoneDomain>> phonesByCustomerId = getPhonesByCustomerId(customerIds);
		Map<String, List<CustomerAddressDomain>> addressesByCustomerId = getAddressesByCustomerId(customerIds);
		presenter.present(new CustomerListResponse(customers, emailsByCustomerId, phonesByCustomerId,
				addressesByCustomerId, page.nextCursor(), page.prevCursor()));
	}

	private Map<String, List<CustomerEmailDomain>> getEmailsByCustomerId(Set<String> customerIds){
		if(customerIds.isEmpty()){
			return Map.of();
		}
		CustomerEmailFilter filter = CustomerEmailFilter.builder()
				.customerIds(customerIds)
				.build();
		return gateway.findEmailsByFilter(filter).stream()
				.collect(Collectors.groupingBy(CustomerEmailDomain::customerId));
	}

	private Map<String, List<CustomerPhoneDomain>> getPhonesByCustomerId(Set<String> customerIds){
		if(customerIds.isEmpty()){
			return Map.of();
		}
		CustomerPhoneFilter filter = CustomerPhoneFilter.builder()
				.customerIds(customerIds)
				.build();
		return gateway.findPhonesByFilter(filter).stream()
				.collect(Collectors.groupingBy(CustomerPhoneDomain::customerId));
	}

	private Map<String, List<CustomerAddressDomain>> getAddressesByCustomerId(Set<String> customerIds){
		if(customerIds.isEmpty()){
			return Map.of();
		}
		CustomerAddressFilter filter = CustomerAddressFilter.builder()
				.customerIds(customerIds)
				.build();
		return gateway.findAddressesByFilter(filter).stream()
				.collect(Collectors.groupingBy(CustomerAddressDomain::customerId));
	}

}
