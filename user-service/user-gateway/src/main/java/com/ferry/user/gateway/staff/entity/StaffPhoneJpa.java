package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.staff.StaffPhone;
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
@Table(name = StaffPhoneJpa.TABLE, indexes = @Index(name = "idx_staff_phones_phone_hash", columnList = "phone_hash"))
public class StaffPhoneJpa{
	static final String TABLE = "staff_phones";
	private static final String COLUMN_PHONE = "phone";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private StaffJpa staff;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "staff_id", insertable = false, updatable = false)
	private String staffId;
	@Column(name = "phone", nullable = false, length = 128)
	private String phoneCipher;
	@Column(name = "phone_hash", length = 64)
	private String phoneHash;
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

	public void setStaff(StaffJpa staff){
		this.staff = staff;
		this.staffId = staff.getId();
	}

	private static CryptoAad aad(String staffId){
		return new CryptoAad(TABLE, COLUMN_PHONE, staffId);
	}

	public static StaffPhoneJpa construct(String id, StaffPhone register, StaffJpa staff,
	                                            CryptoTool cryptoTool){
		StaffPhoneJpa entity = new StaffPhoneJpa();
		entity.setId(id);
		entity.setStaff(staff);
		String phone = register.phone().value();
		entity.setPhoneCipher(cryptoTool.encrypt(phone, aad(staff.getId())));
		entity.setPhoneHash(cryptoTool.blindIndex(phone));
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		entity.setDeleted(register.deleted());
		entity.setVersion(register.version());
		return entity;
	}

	public static StaffPhone construct(StaffPhoneJpa saved, CryptoTool cryptoTool){
		String phone = cryptoTool.decrypt(saved.getPhoneCipher(), aad(saved.getStaffId()));
		return new StaffPhone(saved.getId(), saved.getStaffId(), new Phone(phone), saved.getVersion(),
				saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

	public static String decryptPhone(String phoneCipher, String staffId, CryptoTool cryptoTool){
		return cryptoTool.decrypt(phoneCipher, aad(staffId));
	}

	public void backfill(CryptoTool cryptoTool){
		String phone = cryptoTool.decrypt(phoneCipher, aad(staffId));
		if(phone.equals(phoneCipher)){
			phoneCipher = cryptoTool.encrypt(phone, aad(staffId));
		}
		phoneHash = cryptoTool.blindIndex(phone);
	}
}
