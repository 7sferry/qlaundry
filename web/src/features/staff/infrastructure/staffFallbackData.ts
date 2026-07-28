/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {Staff} from '../domain/Staff';

export const fallbackStaff: Staff[] = [
	{
		id: 'staff-001',
		username: 'ratna.dewi',
		fullName: 'Ratna Dewi',
		description: 'Kasir shift pagi, penanggung jawab kas harian',
		emails: ['ratna.dewi@qlaundry.id'],
		phones: ['081234567801'],
		addresses: ['Jl. Anggrek No. 3, Kelapa Gading, Jakarta Utara'],
		joinedAt: '2025-02-10',
	},
	{
		id: 'staff-002',
		username: 'joko.pratama',
		fullName: 'Joko Pratama',
		description: 'Operator mesin cuci & pengering',
		emails: ['joko.pratama@qlaundry.id'],
		phones: ['081234567802'],
		addresses: ['Jl. Cempaka No. 21, Sunter, Jakarta Utara'],
		joinedAt: '2025-04-01',
	},
	{
		id: 'staff-003',
		username: 'maya.lestari',
		fullName: 'Maya Lestari',
		description: 'Setrika & lipat, quality control pakaian',
		emails: ['maya.lestari@qlaundry.id'],
		phones: ['081234567803'],
		addresses: ['Jl. Flamboyan No. 8, Tebet, Jakarta Selatan'],
		joinedAt: '2025-06-15',
	},
	{
		id: 'staff-004',
		username: 'agus.wijaya',
		fullName: 'Agus Wijaya',
		description: 'Kurir antar-jemput area Jakarta Utara',
		emails: ['agus.wijaya@qlaundry.id'],
		phones: ['081234567804', '087812349900'],
		addresses: ['Jl. Kamboja No. 17, Koja, Jakarta Utara'],
		joinedAt: '2025-09-20',
	},
	{
		id: 'staff-005',
		username: 'lina.sari',
		fullName: 'Lina Sari',
		description: 'Kasir shift sore',
		emails: ['lina.sari@qlaundry.id'],
		phones: ['081234567805'],
		addresses: ['Jl. Dahlia No. 5, Pademangan, Jakarta Utara'],
		joinedAt: '2026-01-12',
	},
	{
		id: 'staff-006',
		username: 'rudi.hartono',
		fullName: 'Rudi Hartono',
		description: 'Kurir antar-jemput area Jakarta Pusat',
		emails: [],
		phones: ['081234567806'],
		addresses: ['Jl. Teratai No. 11, Kemayoran, Jakarta Pusat'],
		joinedAt: '2026-06-30',
	},
];
