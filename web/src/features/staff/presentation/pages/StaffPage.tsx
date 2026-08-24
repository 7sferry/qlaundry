/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useCallback, useEffect, useRef, useState} from 'react';
import {UserPlus, Users} from 'lucide-react';
import {Button, Loading, Modal, PageHeader, StatCard, useToast} from '@/core/ui';
import {useAuth} from '@/features/auth/presentation/useAuth';
import type {SortBy, SortDirection} from '@/core/pagination/Pagination';
import {useStaff} from '../useStaff';
import StaffForm from '../components/StaffForm';
import {type StaffFormData, emptyStaffForm} from '../components/staffFormData';
import StaffTable from '../components/StaffTable';
import StaffDetailDrawer from '../components/StaffDetailDrawer';
import type {CreateStaffInput, Staff} from '../../domain/Staff';

export default function StaffPage() {
	const {staff, loading, hasNext, hasPrev, refresh, goNext, goPrevious, createStaff, deleteStaff} = useStaff();
	const {user} = useAuth();
	const toast = useToast();

	const canDelete = (s: Staff) => user?.staffRole === 'SUPER_STAFF' && s.username !== user.username;
	const canAdd = user?.staffRole === 'SUPER_STAFF';

	const [search, setSearch] = useState('');
	const [sortBy, setSortBy] = useState<SortBy>('id');
	const [sortDir, setSortDir] = useState<SortDirection>('desc');
	const [selectedStaff, setSelectedStaff] = useState<Staff | null>(null);
	const [showAddModal, setShowAddModal] = useState(false);
	const [form, setForm] = useState<StaffFormData>(emptyStaffForm);
	const [saving, setSaving] = useState(false);

	const didMount = useRef(false);
	useEffect(() => {
		if (!didMount.current) {
			didMount.current = true;
			return;
		}
		const timer = setTimeout(() => {
			void refresh({search: search || undefined, sortBy, sortDir});
		}, 300);
		return () => clearTimeout(timer);
	}, [search, sortBy, sortDir, refresh]);

	const handleSortChange = useCallback((by: SortBy, dir: SortDirection) => {
		setSortBy(by);
		setSortDir(dir);
	}, []);

	if (loading && staff.length === 0) return <Loading label="Loading staff…"/>;

	const now = new Date();
	const newThisMonth = staff.filter((s) => {
		const joined = new Date(s.joinedAt);
		return joined.getMonth() === now.getMonth() && joined.getFullYear() === now.getFullYear();
	}).length;

	const openAdd = () => {
		setForm(emptyStaffForm);
		setShowAddModal(true);
	};

	const handleSave = async (e: React.SubmitEvent<HTMLFormElement>) => {
		e.preventDefault();
		if (form.password !== form.confirmPassword) {
			toast.error('Password confirmation does not match.');
			return;
		}
		setSaving(true);
		try {
			const input: CreateStaffInput = {
				username: form.username,
				password: form.password,
				fullName: form.fullName,
				description: form.description || undefined,
				role: form.role,
				emails: form.email ? [form.email] : [],
				phones: form.phone ? [form.phone] : [],
				addresses: form.address ? [form.address] : [],
			};
			await createStaff(input);
			toast.success('New staff member added.');
			setShowAddModal(false);
			setForm(emptyStaffForm);
		} catch (err) {
			toast.error(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
		} finally {
			setSaving(false);
		}
	};

	const handleDelete = async (s: Staff) => {
		if (!confirm(`Delete staff ${s.fullName}? This cannot be undone.`)) return;
		try {
			await deleteStaff(s.id);
			toast.success(`${s.fullName} deleted.`);
			setSelectedStaff(null);
		} catch {
			toast.error('Failed to delete staff.');
		}
	};

	const update = (key: keyof StaffFormData) => (
		e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
	) => setForm((prev) => ({...prev, [key]: e.target.value}));

	const handleCancel = () => {
		setShowAddModal(false);
		setForm(emptyStaffForm);
	};

	return (
		<>
			<PageHeader
				title="Staff management"
				description="Manage staff accounts and access"
				actions={
					(canAdd && <Button onClick={openAdd}>
						<UserPlus size={15}/> Add staff
					</Button>)
				}
			/>

			<div className="grid grid--stats">
				<StatCard icon={<Users size={20}/>} value={staff.length} label="Staff" hint="On this page"/>
				<StatCard icon={<UserPlus size={20}/>} value={newThisMonth} label="New staff"
				          hint="This page, this month"/>
			</div>

			<StaffTable
				staff={staff}
				search={search}
				onSearchChange={setSearch}
				sortBy={sortBy}
				sortDir={sortDir}
				onSortChange={handleSortChange}
				onSelect={setSelectedStaff}
				onDelete={(s) => void handleDelete(s)}
				onAdd={openAdd}
				canDelete={canDelete}
				hasNext={hasNext}
				hasPrev={hasPrev}
				onNext={() => void goNext()}
				onPrev={() => void goPrevious()}
				loading={loading}
			/>

			<Modal open={showAddModal} onClose={() => setShowAddModal(false)} title="Add new staff">
				<StaffForm
					form={form}
					update={update}
					onSubmit={handleSave}
					onCancel={handleCancel}
					saving={saving}
					editMode={false}
				/>
			</Modal>

			<StaffDetailDrawer
				staff={selectedStaff}
				open={!!selectedStaff}
				onClose={() => setSelectedStaff(null)}
				onDelete={(s) => void handleDelete(s)}
				canDelete={selectedStaff ? canDelete(selectedStaff) : false}
			/>
		</>
	);
}
