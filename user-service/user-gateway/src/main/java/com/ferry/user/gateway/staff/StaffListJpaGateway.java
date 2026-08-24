package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.list.StaffListGateway;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PaginationConstant;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffListJpaGateway implements StaffListGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffEmailJpaRepository emailJpaRepository;
	private final StaffPhoneJpaRepository phoneJpaRepository;
	private final StaffAddressJpaRepository addressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public CursorFetch<StaffListProjection> findByFilter(StaffFilter filter){
		List<StaffListProjection> raw = fetchByFilter(filter);
		return CursorFetch.of(raw, PaginationConstant.PAGE_SIZE, filter.pageDirection());
	}

	private List<StaffListProjection> fetchByFilter(StaffFilter filter){
		Pageable pageable = PageRequest.ofSize(PaginationConstant.PAGE_SIZE + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == SortBy.NAME){
			return useAfterQuery
					? staffJpaRepository.findAfterByFullName(filter, StaffListProjection.class, pageable)
					: staffJpaRepository.findBeforeByFullName(filter, StaffListProjection.class, pageable);
		}
		return useAfterQuery
				? staffJpaRepository.findAfterById(filter, StaffListProjection.class, pageable)
				: staffJpaRepository.findBeforeById(filter, StaffListProjection.class, pageable);
	}

	@Override
	public List<StaffPhoneListProjection> findPhonesByFilter(StaffPhoneFilter filter){
		return phoneJpaRepository.findListCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffPhoneListProjection(row.staffId(),
						StaffPhoneJpaEntity.decryptPhone(row.phone(), row.staffId(), cryptoTool)))
				.toList();
	}

	@Override
	public List<StaffEmailListProjection> findEmailsByFilter(StaffEmailFilter filter){
		return emailJpaRepository.findListCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffEmailListProjection(row.staffId(),
						StaffEmailJpaEntity.decryptEmail(row.email(), row.staffId(), cryptoTool)))
				.toList();
	}

	@Override
	public List<StaffAddressListProjection> findAddressesByFilter(StaffAddressFilter filter){
		return addressJpaRepository.findListCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffAddressListProjection(row.staffId(),
						StaffAddressJpaEntity.decryptAddressLine(row.addressLine(), row.staffId(), cryptoTool)))
				.toList();
	}
}
