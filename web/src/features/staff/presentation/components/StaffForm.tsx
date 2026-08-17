/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';
import {AtSign, Lock, Mail, MapPin, Shield} from 'lucide-react';
import {Button, Field, Input, PasswordInput, PhoneInput, Select, Textarea} from '@/core/ui';
import type {StaffFormData} from './staffFormData';

interface StaffFormProps {
	form: StaffFormData;
	update: (key: keyof StaffFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => void;
	onSubmit: (e: React.SubmitEvent<HTMLFormElement>) => void;
	onCancel: () => void;
	saving: boolean;
	editMode: boolean;
}

export default function StaffForm({form, update, onSubmit, onCancel, saving, editMode}: StaffFormProps) {
	return (
			<form onSubmit={onSubmit} style={{display: 'flex', flexDirection: 'column', gap: 0}}>
				<Field label="Username" htmlFor="sfUsername">
					<div className="input-with-icon">
						<AtSign size={15}/>
						<Input id="sfUsername" autoComplete="off" required disabled={editMode} value={form.username}
						       onChange={update('username')} placeholder="staff username" minLength={5}/>
					</div>
				</Field>
				{!editMode && (
						<>
							<Field label="Password" htmlFor="sfPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<PasswordInput id="sfPassword" required minLength={8} value={form.password}
									       onChange={update('password')} placeholder="at least 8 characters"/>
								</div>
							</Field>
							<Field label="Confirm password" htmlFor="sfConfirmPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<PasswordInput id="sfConfirmPassword" required minLength={8} value={form.confirmPassword}
									       onChange={update('confirmPassword')} placeholder="repeat password"/>
								</div>
							</Field>
						</>
				)}
				{editMode && (
						<>
							<Field label="Current password" htmlFor="sfCurrentPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<PasswordInput id="sfCurrentPassword" value={form.currentPassword}
									       onChange={update('currentPassword')} placeholder="fill to change password"/>
								</div>
							</Field>
							<Field label="New password" htmlFor="sfNewPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<PasswordInput id="sfNewPassword" minLength={8} value={form.newPassword}
									       onChange={update('newPassword')} placeholder="optional, at least 8 characters"/>
								</div>
							</Field>
							<Field label="Confirm new password" htmlFor="sfConfirmNewPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<PasswordInput id="sfConfirmNewPassword" minLength={8} value={form.confirmNewPassword}
									       onChange={update('confirmNewPassword')} placeholder="repeat new password"/>
								</div>
							</Field>
						</>
				)}
				<Field label="Full name" htmlFor="sfName">
					<Input id="sfName" required value={form.fullName} onChange={update('fullName')}
					       placeholder="Staff name" autoComplete="off"/>
				</Field>
				{!editMode && (
						<Field label="Role" htmlFor="sfRole">
							<div className="input-with-icon">
								<Shield size={15}/>
								<Select id="sfRole" required value={form.role} onChange={update('role')}>
									<option value="STAFF">Staff</option>
									<option value="SUPER_STAFF">Super staff</option>
								</Select>
							</div>
						</Field>
				)}
				<Field label="Telephone" htmlFor="sfPhone">
					<PhoneInput id="sfPhone" value={form.phone} onChange={update('phone')}/>
				</Field>
				<Field label="Email" htmlFor="sfEmail">
					<div className="input-with-icon">
						<Mail size={15}/>
						<Input id="sfEmail" type="email" required value={form.email} onChange={update('email')}
					       placeholder="name@example.com" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Address" htmlFor="sfAddr">
					<div className="input-with-icon">
						<MapPin size={15}/>
						<Input id="sfAddr" value={form.address} onChange={update('address')}
					       placeholder="optional" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Description" htmlFor="sfDesc">
					<Textarea id="sfDesc" value={form.description} onChange={update('description')}
					          placeholder="duties or notes, optional" rows={2} autoComplete="off"/>
				</Field>
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Cancel</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Saving…' : editMode ? 'Save changes' : 'Add staff'}
					</Button>
				</div>
			</form>
	);
}
