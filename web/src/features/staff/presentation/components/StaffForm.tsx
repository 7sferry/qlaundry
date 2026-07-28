/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';
import {AtSign, Lock, Mail, MapPin, Phone, Shield} from 'lucide-react';
import {Button, Field, Input, Select, Textarea} from '@/core/ui';
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
						       onChange={update('username')} placeholder="username staf" minLength={5}/>
					</div>
				</Field>
				{!editMode && (
						<Field label="Password" htmlFor="sfPassword">
							<div className="input-with-icon">
								<Lock size={15}/>
								<Input id="sfPassword" type="password" required minLength={8} value={form.password}
								       onChange={update('password')} placeholder="minimal 8 karakter"/>
							</div>
						</Field>
				)}
				{editMode && (
						<>
							<Field label="Password saat ini" htmlFor="sfCurrentPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<Input id="sfCurrentPassword" type="password" value={form.currentPassword}
									       onChange={update('currentPassword')} placeholder="isi untuk mengganti password"/>
								</div>
							</Field>
							<Field label="Password baru" htmlFor="sfNewPassword">
								<div className="input-with-icon">
									<Lock size={15}/>
									<Input id="sfNewPassword" type="password" minLength={8} value={form.newPassword}
									       onChange={update('newPassword')} placeholder="opsional, minimal 8 karakter"/>
								</div>
							</Field>
						</>
				)}
				<Field label="Nama lengkap" htmlFor="sfName">
					<Input id="sfName" required value={form.fullName} onChange={update('fullName')}
					       placeholder="Nama staf" autoComplete="off"/>
				</Field>
				{!editMode && (
						<Field label="Role" htmlFor="sfRole">
							<div className="input-with-icon">
								<Shield size={15}/>
								<Select id="sfRole" required value={form.role} onChange={update('role')}>
									<option value="STAFF">Staf</option>
									<option value="SUPER_STAFF">Super staf</option>
								</Select>
							</div>
						</Field>
				)}
				<Field label="Nomor telepon" htmlFor="sfPhone">
					<div className="input-with-icon">
						<Phone size={15}/>
						<Input id="sfPhone" type="tel" value={form.phone} onChange={update('phone')}
						       placeholder="08xxxxxxxxxx" autoComplete="off" pattern="[0-9]+"
						       title="Nomor telepon hanya boleh berisi angka"/>
					</div>
				</Field>
				<Field label="Email" htmlFor="sfEmail">
					<div className="input-with-icon">
						<Mail size={15}/>
						<Input id="sfEmail" type="email" required value={form.email} onChange={update('email')}
						       placeholder="xxx@xxx.xxx" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Alamat" htmlFor="sfAddr">
					<div className="input-with-icon">
						<MapPin size={15}/>
						<Input id="sfAddr" value={form.address} onChange={update('address')}
						       placeholder="opsional" autoComplete="off"/>
					</div>
				</Field>
				<Field label="Deskripsi" htmlFor="sfDesc">
					<Textarea id="sfDesc" value={form.description} onChange={update('description')}
					          placeholder="tugas atau catatan, opsional" rows={2} autoComplete="off"/>
				</Field>
				<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 8}}>
					<Button type="button" variant="ghost" onClick={onCancel}>Batal</Button>
					<Button type="submit" disabled={saving}>
						{saving ? 'Menyimpan…' : editMode ? 'Simpan perubahan' : 'Tambah staf'}
					</Button>
				</div>
			</form>
	);
}
