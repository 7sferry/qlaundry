/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

export const Spinner: React.FC = () => <div className="spinner" role="status" aria-label="Loading"/>;

interface LoadingProps {
	label?: string;
}

export const Loading: React.FC<LoadingProps> = ({label = 'Loading…'}) => (
		<div className="center-box">
			<Spinner/>
			<span>{label}</span>
		</div>
);
