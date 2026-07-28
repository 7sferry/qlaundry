/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

interface PageHeaderProps {
	title: string;
	description?: string;
	actions?: React.ReactNode;
}

export const PageHeader: React.FC<PageHeaderProps> = ({title, description, actions}) => (
		<div className="page-header row row--between row--wrap">
			<div>
				<h1>{title}</h1>
				{description && <p>{description}</p>}
			</div>
			{actions}
		</div>
);
