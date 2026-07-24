/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useState} from 'react';
import {UserPlus, Users} from 'lucide-react';
import {Button, Loading, Modal, PageHeader, StatCard, useToast} from '@/core/ui';
import {useStaff} from '../useStaff';
import StaffForm from '../components/StaffForm';
import {type StaffFormData, emptyStaffForm} from '../components/staffFormData';
import StaffTable from '../components/StaffTable';
import StaffDetailDrawer from '../components/StaffDetailDrawer';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../../domain/Staff';

export default function StaffPage() {
	const {staff, loading, createStaff, updateStaff, deleteStaff} = useStaff();
	const toast = useToast();

	const [search, setSearch] = useState('');
	const [selectedStaff, setSelectedStaff] = useState<Staff | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [editMode, setEditMode] = useState(false);
	const [form, setForm] = useState<StaffFormData>(emptyStaffForm);
	const [saving, setSaving] = useState(false);

	if (loading) return <Loading label="Memuat staf…"/>;

	const visible = staff.filter((s) => {
		const q = search.toLowerCase();
		return (
				!q ||
				s.fullName.toLowerCase().includes(q) ||
				s.username.toLowerCase().includes(q) ||
				s.phones.some((p) => p.includes(q)) ||
				s.emails.some((e) => e.toLowerCase().includes(q))
		);
	});

	const now = new Date();
	const newThisMonth = staff.filter((s) => {
		const joined = new Date(s.joinedAt);
		return joined.getMonth() === now.getMonth() && joined.getFullYear() === now.getFullYear();
	}).length;

	const openAdd = () => {
		setForm(emptyStaffForm);
		setShowAddModal(true);
	};

	const openEdit = (s: Staff) => {
		setForm({
			username: s.username,
			password: '',
			fullName: s.fullName,
			description: s.description ?? '',
			email: s.emails[0] ?? '',
			phone: s.phones[0] ?? '',
			address: s.addresses[0] ?? '',
		});
		setEditMode(true);
		setSelectedStaff(s);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		setSaving(true);
		try {
			if (editMode && selectedStaff) {
				const input: UpdateStaffInput = {
					id: selectedStaff.id,
					fullName: form.fullName,
					description: form.description || undefined,
					emails: form.email ? [form.email] : [],
					phones: form.phone ? [form.phone] : [],
					addresses: form.address ? [form.address] : [],
				};
				const updated = await updateStaff(input);
				toast.success('Data staf diperbarui.');
				setEditMode(false);
				setSelectedStaff(updated);
			} else {
				const input: CreateStaffInput = {
					username: form.username,
					password: form.password,
					fullName: form.fullName,
					description: form.description || undefined,
					emails: form.email ? [form.email] : [],
					phones: form.phone ? [form.phone] : [],
					addresses: form.address ? [form.address] : [],
				};
				await createStaff(input);
				toast.success('Staf baru berhasil ditambahkan.');
				setShowAddModal(false);
			}
			setForm(emptyStaffForm);
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Terjadi kesalahan. Coba lagi.');
		} finally {
			setSaving(false);
		}
	};

	const handleDelete = async (s: Staff) => {
		if (!confirm(`Hapus staf ${s.fullName}? Data ini tidak dapat dipulihkan.`)) return;
		try {
			await deleteStaff(s.id);
			toast.success(`${s.fullName} dihapus.`);
			setSelectedStaff(null);
		} catch {
			toast.error('Gagal menghapus staf.');
		}
	};

	const update = (key: keyof StaffFormData) => (
			e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const handleCancel = () => {
		setShowAddModal(false);
		setEditMode(false);
		setForm(emptyStaffForm);
	};

	return (
			<>
				<PageHeader
						title="Manajemen staf"
						description={`${staff.length} staf terdaftar`}
						actions={
							<Button onClick={openAdd}>
								<UserPlus size={15}/> Tambah staf
							</Button>
						}
				/>

				<div className="grid grid--stats">
					<StatCard icon={<Users size={20}/>} value={staff.length} label="Total staf" hint="Terdaftar"/>
					<StatCard icon={<UserPlus size={20}/>} value={newThisMonth} label="Staf baru"
					          hint="Bulan ini"/>
				</div>

				<StaffTable
						staff={visible}
						search={search}
						onSearchChange={setSearch}
						onSelect={setSelectedStaff}
						onEdit={openEdit}
						onDelete={(s) => void handleDelete(s)}
						onAdd={openAdd}
				/>

				<Modal open={showAddModal} onClose={() => setShowAddModal(false)} title="Tambah staf baru">
					<StaffForm
							form={form}
							update={update}
							onSubmit={handleSave}
							onCancel={handleCancel}
							saving={saving}
							editMode={editMode}
					/>
				</Modal>

				<StaffDetailDrawer
						staff={selectedStaff}
						open={!!selectedStaff && !editMode}
						onClose={() => setSelectedStaff(null)}
						onEdit={openEdit}
						onDelete={(s) => void handleDelete(s)}
				/>

				<Modal open={editMode} onClose={() => {
					setEditMode(false);
					setForm(emptyStaffForm);
				}} title={`Edit ${selectedStaff?.fullName}`}>
					<StaffForm
							form={form}
							update={update}
							onSubmit={handleSave}
							onCancel={handleCancel}
							saving={saving}
							editMode={editMode}
					/>
				</Modal>
			</>
	);
}
