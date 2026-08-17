/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

import React, {useState} from 'react';
import {Phone} from 'lucide-react';
import {Input, Select} from './Field';

export interface CountryDialCode {
	iso: string;
	name: string;
	dialCode: string;
}

export const COUNTRY_DIAL_CODES: CountryDialCode[] = [
	{iso: 'ID', name: 'Indonesia', dialCode: '+62'},
	{iso: 'SG', name: 'Singapore', dialCode: '+65'},
	{iso: 'MY', name: 'Malaysia', dialCode: '+60'},
	{iso: 'TH', name: 'Thailand', dialCode: '+66'},
	{iso: 'PH', name: 'Philippines', dialCode: '+63'},
	{iso: 'VN', name: 'Vietnam', dialCode: '+84'},
	{iso: 'IN', name: 'India', dialCode: '+91'},
	{iso: 'CN', name: 'China', dialCode: '+86'},
	{iso: 'JP', name: 'Japan', dialCode: '+81'},
	{iso: 'KR', name: 'South Korea', dialCode: '+82'},
	{iso: 'AU', name: 'Australia', dialCode: '+61'},
	{iso: 'AE', name: 'United Arab Emirates', dialCode: '+971'},
	{iso: 'SA', name: 'Saudi Arabia', dialCode: '+966'},
	{iso: 'GB', name: 'United Kingdom', dialCode: '+44'},
	{iso: 'US', name: 'United States', dialCode: '+1'},
];

export const DEFAULT_DIAL_CODE = '+62';

const byLengthDesc = [...COUNTRY_DIAL_CODES].sort((a, b) => b.dialCode.length - a.dialCode.length);

function matchDialCode(value: string): string {
	const match = byLengthDesc.find((c) => value.startsWith(c.dialCode));
	return match ? match.dialCode : DEFAULT_DIAL_CODE;
}

interface PhoneInputProps {
	id?: string;
	value: string;
	onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
	autoComplete?: string;
}

export const PhoneInput: React.FC<PhoneInputProps> = ({id, value, onChange, autoComplete = 'off'}) => {
	const [dialCode, setDialCode] = useState<string>(() => matchDialCode(value));
	const [lastValue, setLastValue] = useState(value);

	if (value !== lastValue) {
		setLastValue(value);
		if (value && !value.startsWith(dialCode)) {
			setDialCode(matchDialCode(value));
		}
	}

	const nationalNumber = value.startsWith(dialCode) ? value.slice(dialCode.length) : '';
	const emit = (nextValue: string) => onChange({target: {value: nextValue}} as React.ChangeEvent<HTMLInputElement>);

	return (
			<div className="input-with-icon phone-input">
				<Phone size={15}/>
				<Select
						className="phone-input__code"
						aria-label="Country code"
						value={dialCode}
						onChange={(e) => {
							const nextDialCode = e.target.value;
							setDialCode(nextDialCode);
							emit(nationalNumber ? `${nextDialCode}${nationalNumber}` : '');
						}}
				>
					{COUNTRY_DIAL_CODES.map((c) => (
							<option key={c.iso} value={c.dialCode}>{c.iso} {c.dialCode}</option>
					))}
				</Select>
				<Input
						id={id}
						className="phone-input__number"
						type="tel"
						inputMode="numeric"
						autoComplete={autoComplete}
						placeholder="81234567890"
						value={nationalNumber}
						onChange={(e) => {
							const digits = e.target.value.replace(/\D/g, '');
							emit(digits ? `${dialCode}${digits}` : '');
						}}
				/>
			</div>
	);
};
