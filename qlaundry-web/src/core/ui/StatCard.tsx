/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

interface StatCardProps {
	icon: React.ReactNode;
	value: React.ReactNode;
	label: string;
	hint?: React.ReactNode;
}

export const StatCard: React.FC<StatCardProps> = ({icon, value, label, hint}) => (
		<div className="card stat-card">
			<div className="stat-card__icon">{icon}</div>
			<div className="stat-card__value">{value}</div>
			<div className="stat-card__label">{label}</div>
			{hint && <div style={{fontSize: 12}}>{hint}</div>}
		</div>
);
