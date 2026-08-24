package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.list.CustomerListGateway;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PaginationConstant;
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
import com.ferry.user.gateway.customer.entity.CustomerAddressJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpaEntity;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class CustomerListJpaGateway implements CustomerListGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public CursorFetch<CustomerDomain> findByFilter(CustomerFilter filter){
		List<CustomerJpaEntity> raw = fetchByFilter(filter);
		List<CustomerDomain> rows = raw.stream().map(CustomerJpaEntity::construct).toList();
		return CursorFetch.of(rows, PaginationConstant.PAGE_SIZE, filter.pageDirection());
	}

	private List<CustomerJpaEntity> fetchByFilter(CustomerFilter filter){
		String phoneHash = filter.hasPhone() ? cryptoTool.blindIndex(filter.phone()) : null;
		Pageable pageable = PageRequest.ofSize(PaginationConstant.PAGE_SIZE + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == SortBy.NAME){
			return useAfterQuery
					? customerJpaRepository.findAfterByFullName(filter, phoneHash, pageable)
					: customerJpaRepository.findBeforeByFullName(filter, phoneHash, pageable);
		}
		return useAfterQuery
				? customerJpaRepository.findAfterById(filter, phoneHash, pageable)
				: customerJpaRepository.findBeforeById(filter, phoneHash, pageable);
	}

	@Override
	public List<CustomerEmailDomain> findEmailsByFilter(CustomerEmailFilter filter){
		return customerEmailJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerEmailJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerPhoneDomain> findPhonesByFilter(CustomerPhoneFilter filter){
		return customerPhoneJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerPhoneJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerAddressDomain> findAddressesByFilter(CustomerAddressFilter filter){
		return customerAddressJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerAddressJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

}
