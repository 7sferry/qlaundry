package com.ferry.user.core.customer.list;

import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.customer.*;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserAuthPrincipal;
import com.ferry.utils.pagination.*;
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
		CustomerListSortBy sortBy = request.sortBy() == null ? CustomerListSortBy.ID : request.sortBy();
		SortDirection sortDir = request.sortDir() == null ? SortDirection.DESC : request.sortDir();
		PageDirection direction = PageDirection.direction(request.before());
		PageCursor cursor = PageCursor.cursor(request.after(), request.before());
		int pageSize = PaginationConstant.resolvePageSize(request.pageSize());
		CustomerFilter filter = CustomerFilter.builder()
				.fullName(request.fullName())
				.phone(phone)
				.tenantId(tenantId.value())
				.sortBy(sortBy)
				.sortDir(sortDir)
				.pageDirection(direction)
				.cursor(cursor)
				.pageSize(pageSize)
				.build();
		CursorFetch<CustomerDomain> fetch = gateway.findByFilter(filter);
		CursorPage<CustomerDomain> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> List.of(sortBy == CustomerListSortBy.NAME ? row.fullNameValue() : row.id(), row.id()));
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
