package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.list.CustomerListGateway;
import com.ferry.user.domain.customer.*;
import com.ferry.user.gateway.customer.entity.CustomerAddressJpa;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpa;
import com.ferry.user.gateway.customer.entity.CustomerJpa;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpa;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerListGateway implements CustomerListGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public CursorFetch<Customer> findByFilter(CustomerFilter filter){
		List<CustomerJpa> raw = fetchByFilter(filter);
		List<Customer> rows = raw.stream().map(CustomerJpa::construct).toList();
		return CursorFetch.of(rows, filter.pageSize(), filter.pageDirection());
	}

	private List<CustomerJpa> fetchByFilter(CustomerFilter filter){
		String phoneHash = filter.hasPhone() ? cryptoTool.blindIndex(filter.phone()) : null;
		Pageable pageable = PageRequest.ofSize(filter.pageSize() + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == CustomerListSortBy.NAME){
			return useAfterQuery
					? customerJpaRepository.findAfterByFullName(filter, phoneHash, pageable)
					: customerJpaRepository.findBeforeByFullName(filter, phoneHash, pageable);
		}
		return useAfterQuery
				? customerJpaRepository.findAfterById(filter, phoneHash, pageable)
				: customerJpaRepository.findBeforeById(filter, phoneHash, pageable);
	}

	@Override
	public List<CustomerEmail> findEmailsByFilter(CustomerEmailFilter filter){
		return customerEmailJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerEmailJpa.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerPhone> findPhonesByFilter(CustomerPhoneFilter filter){
		return customerPhoneJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerPhoneJpa.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerAddress> findAddressesByFilter(CustomerAddressFilter filter){
		return customerAddressJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerAddressJpa.construct(entity, cryptoTool))
				.toList();
	}

}
