package com.ferry.user.core.tools;

import com.ferry.user.domain.common.exception.PasswordPolicyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

class PasswordContentPolicyTest{

	private static final PasswordSubject SUBJECT = new PasswordSubject("hendrastafflogin", "Hendra Wijaya",
			"Laundry Sejahtera", "qlk_cabang07", List.of("kasir.depan@mailku.id", "ab@mailku.id"),
			List.of("+6281377001234"));

	@ParameterizedTest
	@ValueSource(strings = {"xxHendraStaffLoginxx", "hendra_staff_login99", "Hendra.Staff.Login!1", "HENDRASTAFFLOGIN"})
	void givenPasswordContainsUsernameInAnyCaseOrSeparator_thenThrowsPasswordPolicyException(String password){
		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check(password, SUBJECT))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain your username"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"Wijaya#2026!", "My-hendra-77", "HendraWijaya!9", "hendra wijaya 1"})
	void givenPasswordContainsFullNameOrAnyWordOfIt_thenThrowsPasswordPolicyException(String password){
		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check(password, SUBJECT))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain your name"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"Sejahtera99!!", "laundry-2026-x", "LaundrySejahtera#1"})
	void givenPasswordContainsTenantNameOrAnyWordOfIt_thenThrowsPasswordPolicyException(String password){
		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check(password, SUBJECT))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain the tenant name"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"Xx-QLK_Cabang07-Zz", "qlkcabang07!!", "QLK.cabang07.9"})
	void givenPasswordContainsTenantUsername_thenThrowsPasswordPolicyException(String password){
		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check(password, SUBJECT))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain the tenant username"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"Z-kasirdepan-Z", "KASIR.DEPAN#55", "kasir.depan@mailku.id", "x-kasirdepanmailkuid-x"})
	void givenPasswordContainsEmailWithOrWithoutDomain_thenThrowsPasswordPolicyException(String password){
		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check(password, SUBJECT))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain your email address"));
	}

	@Test
	void givenEmailLocalPartIsTooShort_thenOnlyTheFullEmailIsRejected(){
		thenSoftly(softly -> {
			softly.then(catchThrowable(
					() -> PasswordContentPolicy.check("ab-Q7#zUv-91", SUBJECT))).isNull();
			softly.thenThrownBy(() -> PasswordContentPolicy.check("abmailkuid-77", SUBJECT))
					.isInstanceOf(PasswordPolicyException.class)
					.hasMessage("Password must not contain your email address");
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {"P6281377001234x", "P+62-813-7700-1234!", "P081377001234x", "P81377001234x",
			"Q813-7700-1234z", "z0813 7700 1234!"})
	void givenPasswordContainsPhoneNumberWithOrWithoutCountryCode_thenThrowsPasswordPolicyException(String password){
		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check(password, SUBJECT))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain your phone number"));
	}

	@Test
	void givenPhoneNumberWasTypedWithNationalFormat_thenRejectsPasswordWithTheInternationalForm(){
		PasswordSubject typedNationally = new PasswordSubject(null, null, null, null, null, List.of("0812 5566 9090"));

		thenSoftly(softly -> {
			softly.thenThrownBy(() -> PasswordContentPolicy.check("k6281255669090k", typedNationally))
					.isInstanceOf(PasswordPolicyException.class)
					.hasMessage("Password must not contain your phone number");
			softly.thenThrownBy(() -> PasswordContentPolicy.check("k81255669090k", typedNationally))
					.isInstanceOf(PasswordPolicyException.class);
		});
	}

	@Test
	void givenPasswordOnlySharesAFewDigitsWithPhoneNumber_thenAccepted(){
		thenSoftly(softly -> softly.then(catchThrowable(
				() -> PasswordContentPolicy.check("Tr0ub4dor&3-1234-77", SUBJECT))).isNull());
	}

	@Test
	void givenNamePartsShorterThanThreeCharacters_thenTheyAreIgnored(){
		PasswordSubject shortNames = new PasswordSubject("sitiaminah", "Al Li", "Jo Co", null, null, null);

		thenSoftly(softly -> {
			softly.then(catchThrowable(
					() -> PasswordContentPolicy.check("Al-Zebra-Co-77", shortNames))).isNull();
			softly.thenThrownBy(() -> PasswordContentPolicy.check("xx-AlLi-xx", shortNames))
					.isInstanceOf(PasswordPolicyException.class)
					.hasMessage("Password must not contain your name");
		});
	}

	@Test
	void givenSeveralSubjects_thenChecksAllOfThem(){
		PasswordSubject current = new PasswordSubject("budisantoso", "Budi Santoso", "Cuci Kilat", "cucikilat01",
				List.of("budi@mail.id"), List.of());
		PasswordSubject requested = new PasswordSubject("budisantoso", "Budi Hartono", "Cuci Kilat", "cucikilat01",
				List.of("hartono.b@mail.id"), List.of());

		thenSoftly(softly -> softly.thenThrownBy(() -> PasswordContentPolicy.check("HartonoB-Secret-1", current,
						requested))
				.isInstanceOf(PasswordPolicyException.class));
	}

	@Test
	void givenSubjectWithoutAnyPersonalData_thenAcceptsAnyPassword(){
		PasswordSubject empty = new PasswordSubject(null, null, null, null, null, null);

		thenSoftly(softly -> softly.then(catchThrowable(
				() -> PasswordContentPolicy.check("Anything-Goes-2026", empty))).isNull());
	}

	@Test
	void givenUnrelatedPassword_thenAccepted(){
		thenSoftly(softly -> softly.then(catchThrowable(
				() -> PasswordContentPolicy.check("Tr0ub4dor&3-Horse", SUBJECT))).isNull());
	}

}
