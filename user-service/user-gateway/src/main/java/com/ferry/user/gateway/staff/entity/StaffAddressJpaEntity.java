package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.AddressLineDomain;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.utils.crypto.CryptoAad;
import com.ferry.utils.crypto.CryptoTool;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = StaffAddressJpaEntity.TABLE)
public class StaffAddressJpaEntity{
	static final String TABLE = "staff_addresses";
	private static final String COLUMN_ADDRESS_LINE = "address_line";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffJpaEntity staff;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "staff_id", insertable = false, updatable = false)
	private String staffId;
	@Column(name = "address_line", nullable = false, columnDefinition = "text")
	private String addressLineCipher;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	public void setStaff(StaffJpaEntity staff){
		this.staff = staff;
		this.staffId = staff.getId();
	}

	private static CryptoAad aad(String staffId){
		return new CryptoAad(TABLE, COLUMN_ADDRESS_LINE, staffId);
	}

	public static StaffAddressJpaEntity construct(String id, StaffAddressDomain register, StaffJpaEntity staff,
	                                              CryptoTool cryptoTool){
		StaffAddressJpaEntity entity = new StaffAddressJpaEntity();
		entity.id = id;
		entity.staffId = staff.getId();
		entity.staff = staff;
		entity.addressLineCipher = cryptoTool.encrypt(register.addressLine().value(), aad(staff.getId()));
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static StaffAddressDomain constructUserAddressDomain(StaffAddressJpaEntity saved, CryptoTool cryptoTool){
		String addressLine = cryptoTool.decrypt(saved.addressLineCipher, aad(saved.staffId));
		return new StaffAddressDomain(saved.id, saved.staffId, new AddressLineDomain(addressLine), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

	public static String decryptAddressLine(String addressLineCipher, String staffId, CryptoTool cryptoTool){
		return cryptoTool.decrypt(addressLineCipher, aad(staffId));
	}

	public void backfill(CryptoTool cryptoTool){
		String addressLine = cryptoTool.decrypt(addressLineCipher, aad(staffId));
		if(addressLine.equals(addressLineCipher)){
			addressLineCipher = cryptoTool.encrypt(addressLine, aad(staffId));
		}
	}
}
