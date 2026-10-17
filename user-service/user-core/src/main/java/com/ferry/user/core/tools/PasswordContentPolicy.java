package com.ferry.user.core.tools;

import com.ferry.user.domain.common.exception.PasswordPolicyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public final class PasswordContentPolicy{
	private static final int MIN_FRAGMENT_LENGTH = 3;
	private static final int MIN_PHONE_LENGTH = 6;
	private static final int MAX_COUNTRY_CODE_LENGTH = 3;
	private static final char AT_SYMBOL = '@';

	private PasswordContentPolicy(){
	}

	public static void check(String rawPassword, PasswordSubject... subjects){
		String password = alphanumeric(rawPassword);
		String passwordDigits = digits(rawPassword);
		for(PasswordSubject subject : subjects){
			rejectIfContainsAny(password, identifierFragments(subject.username()), "your username");
			rejectIfContainsAny(password, nameFragments(subject.fullName()), "your name");
			rejectIfContainsAny(password, nameFragments(subject.tenantName()), "the tenant name");
			rejectIfContainsAny(password, identifierFragments(subject.tenantUsername()), "the tenant username");
			rejectIfContainsAny(password, emailFragments(subject.emails()), "your email address");
			rejectIfContainsAny(passwordDigits, phoneFragments(subject.phones()), "your phone number");
		}
	}

	private static void rejectIfContainsAny(String password, List<String> fragments, String subject){
		for(String fragment : fragments){
			if(password.contains(fragment)){
				throw new PasswordPolicyException("Password must not contain " + subject);
			}
		}
	}

	private static List<String> identifierFragments(String value){
		List<String> fragments = new ArrayList<>();
		addIfLongEnough(fragments, alphanumeric(value));
		return fragments;
	}

	private static List<String> nameFragments(String value){
		List<String> fragments = new ArrayList<>();
		if(value == null){
			return fragments;
		}
		addIfLongEnough(fragments, alphanumeric(value));
		for(String word : value.trim().split("\\s+")){
			addIfLongEnough(fragments, alphanumeric(word));
		}
		return fragments;
	}

	private static List<String> emailFragments(List<String> emails){
		List<String> fragments = new ArrayList<>();
		for(String email : emails){
			if(email == null){
				continue;
			}
			addIfLongEnough(fragments, alphanumeric(email));
			int atIndex = email.indexOf(AT_SYMBOL);
			if(atIndex > 0){
				addIfLongEnough(fragments, alphanumeric(email.substring(0, atIndex)));
			}
		}
		return fragments;
	}

	private static List<String> phoneFragments(List<String> phones){
		List<String> fragments = new ArrayList<>();
		for(String phone : phones){
			String phoneDigits = digits(phone);
			for(int countryCodeLength = 0; countryCodeLength <= MAX_COUNTRY_CODE_LENGTH; countryCodeLength++){
				if(phoneDigits.length() - countryCodeLength < MIN_PHONE_LENGTH){
					break;
				}
				String national = phoneDigits.substring(countryCodeLength);
				fragments.add(national);
				if(countryCodeLength > 0){
					fragments.add('0' + national);
				}
			}
		}
		return fragments;
	}

	private static void addIfLongEnough(List<String> fragments, String fragment){
		if(fragment.length() >= MIN_FRAGMENT_LENGTH){
			fragments.add(fragment);
		}
	}

	private static String alphanumeric(String value){
		return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
	}

	private static String digits(String value){
		return value == null ? "" : value.replaceAll("\\D", "");
	}
}
